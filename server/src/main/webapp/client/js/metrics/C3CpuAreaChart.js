var React = require('react'),
    c3    = require('c3');

function initChart(selector, metricsStream) {

  var chart = c3.generate({
    bindto: selector,
    data: {
        json: [],
        keys: {
        x: 'time',
        value: ['cpu']
        },
        type: 'area-spline'
    },
    axis: {
        x: {
            type: 'timeseries',
            tick: {
             format: "%X"
            }
        },
        y: {
          tick: {
           format: function(value) {return Math.round(value*100) + '%';}
          }
        }
    }
  });
  metricsStream
    .onValue(function (updates) {
        chart.load({
          json: updates,
          keys: {
            x: 'time',
            value: ['cpu']
          }
        });
    });
}

var C3CpuAreaChart = React.createClass({
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

module.exports = C3CpuAreaChart;