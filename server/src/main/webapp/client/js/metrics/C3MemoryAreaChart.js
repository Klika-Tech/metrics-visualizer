var React = require('react'),
    c3    = require('c3'),
    d3    = require('d3');

function initChart(selector, metricsStream) {

  var chart = c3.generate({
    bindto: selector,
    data: {
      json: [],
      keys: {
        x: 'time',
        value: ['used', 'total']
      },
      type: 'area'
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
         format: function (megabytes) {
             var fmt = d3.format('.0f');
             return fmt(megabytes) + 'MB';
         }
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
            value: ['used', 'total']
          }
        });
    });
}

var C3MemoryAreaChart = React.createClass({
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

module.exports = C3MemoryAreaChart;