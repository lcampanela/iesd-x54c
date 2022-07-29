#!/usr/bin/env bash

HOST_PRIVATE_IP=$1
KUBE_VER=$2

#Initialize the k8s cluster
echo "Master Host IP = "+$HOST_PRIVATE_IP
sudo kubeadm init --pod-network-cidr=10.244.0.0/16 --apiserver-advertise-address $HOST_PRIVATE_IP | tee /kubeadm.log

mkdir -p /root/.kube
sudo cp -i /etc/kubernetes/admin.conf /root/.kube/config
sudo chown "$(id -u):$(id -g)" /root/.kube/config

echo "net.bridge.bridge-nf-call-iptables=1" | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
kubectl apply -f /manifests/flannel-cni.yml

sudo rm -rf /root/.kube

# Config for vagrant user
mkdir -p /home/vagrant/.kube
sudo cp -i /etc/kubernetes/admin.conf /home/vagrant/.kube/config
sudo chown -R "vagrant:vagrant" /home/vagrant/.kube/
