#!/usr/bin/env bash

HOST_PRIVATE_IP=$1
KUBE_VER=$2

echo "net.bridge.bridge-nf-call-iptables=1" | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
