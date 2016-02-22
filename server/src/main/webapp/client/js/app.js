/** @jsx React.DOM */
var React    = require('react'),
    ReactDOM = require('react-dom'),
    Header   = require('./header'),
    Content  = require('./router'),
    App;


App = React.createClass({
  render: function () {
      return <Content/>
  }
});

App.start = function () {
  ReactDOM.render(<App/>, document.getElementById('app'));
};

module.exports = window.App = App;
