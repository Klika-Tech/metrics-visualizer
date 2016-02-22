/** @jsx React.DOM */
var React    = require('react'),
    Router   = require('react-router-component'),
    Metrics     = require('./metrics');

var Locations = Router.Locations,
    Location = Router.Location,
    NotFound = Router.NotFound,
    Link = Router.Link;

var NotFoundPage = React.createClass({
  render: function() {
    return (
      <div>
        Sorry! Page is not found.
        Back to <Link href="/">home</Link>.
      </div>
    )
  }
});

var Content = React.createClass({
  render: function() {
    return (
      <Locations>
        <Location path="/" handler={Metrics} />
        <NotFound handler={NotFoundPage} />
      </Locations>
    )
  }
});

module.exports = Content;
