#!/usr/bin/env bash


# Starting zookeeper from a Vagrant up or reload
/opt/zookeeper-3.8.0/bin/zkServer.sh start

# The boot configuration file /etc/rc.local,
# to restart zookeeper after a reboot
printf "#!/bin/sh -e\n#\n# rc.local\n/opt/zookeeper-3.8.0/bin/zkServer.sh start\n" > /etc/rc.local
chmod +x /etc/rc.local
