activator-akka-http-reactjs
=========================

Project represents a web application that visualizes OS's metrics: physical memory size and system cpu load. 

Install MongoDB
----------------

sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10

echo "deb http://repo.mongodb.org/apt/debian wheezy/mongodb-org/3.0 main" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.0.list

sudo apt-get update

sudo apt-get install -y mongodb-org

Run Akka Http server - Terminal #1
----------------------------------

*Default agent will be created!*

sbt server/run

Run Akka Http agent - Terminal #2
----------------------------------

sbt agent/run
