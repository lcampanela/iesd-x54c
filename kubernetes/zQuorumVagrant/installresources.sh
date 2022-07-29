#!/usr/bin/env bash

apt update
apt install -y default-jdk
mkdir -p /var/lib/zookeeper
chown vagrant:vagrant /var/lib/zookeeper
wget http://apache.uvigo.es/zookeeper/zookeeper-3.8.0/apache-zookeeper-3.8.0-bin.tar.gz
tar xvf apache-zookeeper-3.8.0-bin.tar.gz
rm apache-zookeeper-3.8.0-bin.tar.gz
mv apache-zookeeper-3.8.0-bin /opt/zookeeper-3.8.0
chown -R vagrant:vagrant /opt/zookeeper-3.8.0
echo -e 'tickTime=2000\ndataDir=/var/lib/zookeeper\nclientPort=2181\ninitLimit=5\nsyncLimit=2\nserver.1=192.168.56.110:2888:3888\nserver.2=192.168.56.111:2888:3888\nserver.3=192.168.56.112:2888:3888\nserver.4=192.168.56.113:2888:3888\nserver.5=192.168.56.114:2888:3888' > /opt/zookeeper-3.8.0/conf/zoo.cfg
