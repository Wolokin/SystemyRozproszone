#!/bin/bash

cd ../Lab07/apache-zookeeper-3.8.0-bin/bin
for i in 1 2 3; do ./zkServer.sh start ../conf/zoo$i.cfg; done
read
for i in 1 2 3; do ./zkServer.sh stop ../conf/zoo$i.cfg; done
cd -
