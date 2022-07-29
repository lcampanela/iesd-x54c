#!/usr/bin/env bash

HOST_PRIVATE_IP=$1
KUBE_VER=$2

# update and upgrade linux OS
sudo apt-get update && sudo apt-get upgrade -y
sudo apt-get install ca-certificates software-properties-common apt-transport-https curl gnupg lsb-release -y

# disable swap
sudo swapoff -a && sudo sed -i 's/\/swap/#\/swap/g' /etc/fstab

# Install docker
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list >/dev/null

sudo apt-get update
sudo apt-get install -y docker.io
sudo apt-mark hold docker.io

sudo tee /etc/docker/daemon.json >/dev/null <<EOF
{
  "exec-opts": ["native.cgroupdriver=systemd"],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m"
  },
  "storage-driver": "overlay2"
}
EOF

sudo mkdir -p /etc/systemd/system/docker.service.d
sudo systemctl daemon-reload
sudo systemctl restart docker

# Install kubeadm, kubectl and kubelet
sudo apt-get install -y kubelet=$KUBE_VER-00 kubeadm=$KUBE_VER-00 kubectl=$KUBE_VER-00
sudo apt-mark hold kubelet kubeadm kubectl

echo "KUBELET_EXTRA_ARGS=--node-ip=$HOST_PRIVATE_IP" >> /etc/default/kubelet
sudo systemctl daemon-reload
sudo systemctl restart kubelet
