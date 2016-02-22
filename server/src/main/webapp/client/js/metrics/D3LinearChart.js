/** @jsx React.DOM */
var React = require('react'),
    ReactDOM = require('react-dom'),
    Bacon = require("baconjs").Bacon,
    d3    = require('d3');

function initChart(selection, metricsStream, numberOfLastMetrics) {
  var samplingTime = 1000;

  var width = 700,
      height = 500,
      margins = {
          top: 20,
          bottom: 50,
          left: 70,
          right: 20
      };

  if (!selection.empty()) {
    var svg = selection
        .attr("width", width)
        .attr("height", height);

    var xRange = d3.time.scale().range([margins.left, width - margins.right])
       .domain([new Date(), new Date()]);

    var yRange = d3.scale.linear().range([height - margins.bottom, margins.top])
        .domain([0, 0]);
    var xAxis = d3.svg.axis()
        .scale(xRange)
        .tickSize(5)
        .tickSubdivide(true)
        .tickFormat(d3.time.format("%X"));
    var yAxis = d3.svg.axis()
        .scale(yRange)
        .tickSize(5)
        .orient("left")
        .tickSubdivide(true)
        .tickFormat(d3.format('.0f'));

    var xAxisElement = svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(10," + (height - margins.bottom) + ")")
        .call(xAxis);

    // Add a label to the middle of the x axis
    var xAxisWidth = ((width - margins.right) - margins.left) / 2;
    xAxisElement.append("text")
        .attr("x", margins.left + xAxisWidth)
        .attr("y", 0)
        .attr("dy", "3em")
        .style("text-anchor", "middle")
        .text("Time");

    var yAxisElement = svg.append("g")
        .attr("class", "y axis")
        .attr("transform", "translate(" + (margins.left + 10) + ",0)")
        .call(yAxis);

    // Add a label to the middle of the y axis
    var yAxisHeight = ((height - margins.bottom) - margins.top) / 2;
    yAxisElement.append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 0)
        .attr("x", -(margins.top + yAxisHeight))
        .attr("dy", "-4.5em")
        .style("text-anchor", "middle")
        .text("Used memory");

    svg.append("defs").append("clipPath")
        .attr("id", "clip")
        .append("rect")
        .attr("x", margins.left + 10)
        .attr("y", margins.top)
        .attr("width", width)
        .attr("height", height);

    var line = svg.append("g")
          .attr("clip-path", "url(#clip)")
          .append("path")
          .attr("stroke", "blue")
          .attr("fill", "none");

    // Define our line series
    var lineFunc = d3.svg.line()
        .x(function(d) { return xRange(d.timestamp); })
        .y(function(d) { return yRange(d.value); })
        .interpolate("linear");


    metricsStream
        .map(function (streamMetric) {
           var timestamp = new Date(streamMetric.timestamp);
           var metric = {timestamp: timestamp, value: streamMetric.value};
           return metric;
        })
        .slidingWindow(numberOfLastMetrics, 2)
        .onValue(function (updates) {
            update(updates);
        });

    function update(updates) {
      if (updates.length > 0)   {
          xRange.domain(d3.extent(updates, function(d) { return d.timestamp; }));
          yRange.domain(d3.extent(updates, function(d) { return d.value; }));
      }

      if (updates.length < numberOfLastMetrics) {
          line.transition()
              .ease("linear")
              .attr("d", lineFunc(updates));

          svg.selectAll("g.x.axis")
              .transition()
              .ease("linear")
              .call(xAxis);
      } else {
          var xTranslation = xRange(updates[0].timestamp) - xRange(updates[1].timestamp);

          line
              .attr("d", lineFunc(updates))
              .attr("transform", null)
              .transition()
              .duration(samplingTime - 20)
              .ease("linear")
              .attr("transform", "translate(" + xTranslation + ", 0)");

          svg.selectAll("g.x.axis")
              .transition()
              .duration(samplingTime - 20)
              .ease("linear")
              .call(xAxis);
      }

      svg.selectAll("g.y.axis")
          .transition()
          .call(yAxis);
    }

  } else {
    console.log("Selection is empty!")
  }

}

var D3LinearChart = React.createClass({
  componentDidMount: function(){
    initChart(this.selection, this.props.stream, this.props.numberOfLastMetrics);
  },
  render: function() {
    var chartId = this.props.selector.substring(1);
    return (
      <svg id={chartId} ref={(ref) => this.selection = d3.select(ReactDOM.findDOMNode(this))}></svg>
    );
  }
});

module.exports = D3LinearChart;