/** @jsx React.DOM */
var React             = require('react'),
    Bacon             = require("baconjs").Bacon,
    d3                = require('d3'),
    //D3LinearChart     = require('./D3LinearChart'),
    C3MemoryAreaChart = require('./C3MemoryAreaChart'),
    C3CpuAreaChart = require('./C3CpuAreaChart'),
    C3CpuGauge        = require('./C3CpuGauge'),
    Metrics;

var numberOfLastMetrics = 60;

var ws = new WebSocket("ws://localhost:9080/ws/agents/srv01/metrics?numberOfLastMetrics=" + numberOfLastMetrics);
ws.onopen = function () {
    console.log("Connection opened");
};

ws.onclose = function () {
    console.log("Connection is closed...");
};

var metricsStream = Bacon.fromEventTarget(ws, "message").map(function(event) {
  var dataString = event.data;
  return JSON.parse(dataString);
});

//CPU
var cpuStream = metricsStream.filter(function(update) {
  return update.name === "system.cpu.load";
});

//Total memory
var totalMemoryStream = metricsStream.filter(function(update) {
  return update.name === "system.memory.total";
});

//Used memory
var usedMemoryStream = metricsStream.filter(function(update) {
  return update.name === "system.memory.used";
});

var memoryChartStream =
    totalMemoryStream.zip(usedMemoryStream)
        .map(function (streamMetric) {
            var total = streamMetric[0];
            var used = streamMetric[1];
            var metric = {time: total.timestamp, used: used.value, total: total.value};
            return metric;
        })
        .slidingWindow(numberOfLastMetrics);

var cpuChartStream =
    cpuStream
        .map(function (streamMetric) {
            var metric = {time: streamMetric.timestamp, cpu: streamMetric.value};
            return metric;
        })
        .slidingWindow(numberOfLastMetrics);

var Metrics = React.createClass({
  render: function() {
    return (
      <div className="container-fluid">

        <div className="row">

          <h1>Metrics Dashboard</h1>

          <div className="col-xs-12">

            <div className="row">

              <div className="col-xs-12 chart">
                <C3MemoryAreaChart selector="#memory_chart" stream={memoryChartStream} />
              </div>

            </div>

          </div>

          <div className="col-xs-12">

            <div className="row">
                <div className="col-xs-6 vcenter">
                    <div>
                      <C3CpuAreaChart selector="#cpu_chart" stream={cpuChartStream} />
                    </div>
                </div>
                <div className="col-xs-6 vcenter">
                  <div>
                    <C3CpuGauge selector="#cpu_gauge" stream={cpuStream} />
                  </div>
                </div>
            </div>

          </div>

        </div>

      </div>
    )
  }
});

module.exports = Metrics;