var React = require('react'),
    c3    = require('c3');

function initChart(selector, metricsStream) {
  var chart = c3.generate({
      bindto: selector,
      data: {
          columns: [
              ['data', 0]
          ],
          type: 'gauge'
      },
      color: {
          pattern: ['#FF0000', '#F97600', '#F6C600', '#60B044'],
          threshold: {
              values: [30, 60, 90, 100]
          }
      },
      size: {
          height: 180
      }
  });
  metricsStream
          .onValue(function (metric) {
              var cpuValue = parseFloat(metric.value) * 100;
              chart.load({
                  columns: [['data', cpuValue]]
              });
          });
}

var C3CpuGauge = React.createClass({
  componentDidMount: function(){
    initChart(this.props.selector, this.props.stream);
  },
  render: function() {
    var chartId = this.props.selector.substring(1);
    return (
      <div id={chartId} ></div>
    );
  }
});

module.exports = C3CpuGauge;