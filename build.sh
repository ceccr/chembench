#!/bin/bash
mvn clean compile war:exploded
chmod -R g+rwX target
sudo /etc/init.d/tomcat6 stop
rm -rf /CHEMBENCH/ROOT/*
cp -r target/chembench*/* /CHEMBENCH/ROOT
sudo /etc/init.d/tomcat6 start

