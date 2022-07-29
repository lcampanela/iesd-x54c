#!/usr/bin/env bash

# ISEL/DEETC/MEIC/IESD 2022
# Installation and configuration of a Apache Zookeeper server (instance)
apt update
apt install -y default-jdk
mkdir -p /var/lib/zookeeper
chown vagrant:vagrant /var/lib/zookeeper
wget http://apache.uvigo.es/zookeeper/zookeeper-3.8.0/apache-zookeeper-3.8.0-bin.tar.gz
tar xvf apache-zookeeper-3.8.0-bin.tar.gz
rm apache-zookeeper-3.8.0-bin.tar.gz
mv apache-zookeeper-3.8.0-bin /opt/zookeeper-3.8.0
chown -R vagrant:vagrant /opt/zookeeper-3.8.0
echo -e 'tickTime=2000\ndataDir=/var/lib/zookeeper\nclientPort=2181\ninitLimit=5\nsyncLimit=2' > /opt/zookeeper-3.8.0/conf/zoo.cfg

# Starting zookeeper from a Vagrant up or reload
/opt/zookeeper-3.8.0/bin/zkServer.sh start

# The boot configuration file /etc/rc.local,
# to restart zookeeper after a reboot
printf "#!/bin/sh -e\n#\n# rc.local\n/opt/zookeeper-3.8.0/bin/zkServer.sh start\n" > /etc/rc.local
chmod +x /etc/rc.local

# Install net-tools, e.g., make available the shell ifconfig command
apt install net-tools
