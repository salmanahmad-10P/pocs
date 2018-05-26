# Purpose:
#  Provisions master1, infra1 and nodes for OCP environment
#
# Assumes existance of a rhel7 base vm

# virsh net-edit default

#  <ip address='192.168.122.1' netmask='255.255.255.0'>
#    <dhcp>
#      <range start='192.168.122.2' end='192.168.122.254'/>
#      <host mac='52:54:00:e1:2f:1e' name='ocp.master1' ip='192.168.122.229'/>
#      <host mac='52:54:00:ce:67:eb' name='ocp.infra1' ip='192.168.122.12'/>
#      <host mac='52:54:00:03:cd:2d' name='ocp.node1' ip='192.168.122.169'/>
#      <host mac='52:54:00:d6:ab:29' name='ocp.node2' ip='192.168.122.170'/>
#      <host mac='52:54:00:a8:38:bf' name='ocp.node3' ip='192.168.122.211'/>
#    </dhcp>
#  </ip>

# virsh net-destroy default
# virsh net-start default


base_image_name=rhel7_base_sparse
net_domain=jbridethinkpad.com
dns2=75.75.75.75
dns3=75.75.75.76
image_path=/u02/vm
node_memory=6144
node_cpus=2
virt_sleep=30

master1_mac=52:54:00:e1:2f:1e
infra1_mac=52:54:00:ce:67:eb
node1_mac=52:54:00:03:cd:2d
node2_mac=52:54:00:d6:ab:29
node3_mac=52:54:00:a8:38:bf

setup() {
  for node in master1 infra1 node1 node2 node3; do
    echo "setup() destroying, undefining, deleting and re-copying base image for: $node";
    virsh destroy ocp.$node
    virsh undefine ocp.$node
    rm -f $image_path/ocp/$node.qcow2
    cp $image_path/base/$base_image_name.qcow2 $image_path/ocp/$node.qcow2

  done;
}


provision() {
  for node in master1 infra1 node1 node2 node3; do
    if [ "$node" = "master1" ] || [ "$node" = "infra1" ];then
        memory=2048
        cpus=2
    else
        memory=$node_memory
        cpus=$node_cpus
    fi
    if [ "$node" = "master1" ];then mac_address=$master1_mac; fi
    if [ "$node" = "infra1" ];then mac_address=$infra1_mac; fi
    if [ "$node" = "node1" ];then mac_address=$node1_mac; fi
    if [ "$node" = "node2" ];then mac_address=$node2_mac; fi
    if [ "$node" = "node3" ];then mac_address=$node3_mac; fi

    echo -en "\n\nprovision(): ocp.$node memory=$memory : cpus=$cpus : mac_address=$mac_address : hostname=$node.$net_domain"

    # 1) provision VM
    virt-install --import -n ocp.$node  -r $memory --vcpus=$cpus --os-type=linux --os-variant=rhel7 --noautoconsole \
        --network network=default,model=virtio,mac=$mac_address \
        --disk $image_path/ocp/$node.qcow2,format=qcow2,bus=virtio
    sleep $virt_sleep
    virsh destroy ocp.$node
    sleep 5

    # 2) Change hostname of VM
    virt-customize -d ocp.$node --hostname $node.$net_domain

    # 3) Restart VM
    virsh start ocp.$node
    echo "provision() mac address for $node = `virsh dumpxml ocp.$node | grep 'mac address'`"
    
  done;
  
}

post() {
  for node in master1 infra1 node1 node2 node3; do
    echo "post() node = $node";
    ssh root@$node.$net_domain "yum install wget git net-tools bind-utils iptables-services bridge-utils bash-completion kexec-tools sos psacct atomic-openshift-utils"
    ssh root@$node.$net_domain "systemctl enable docker.service"
    ssh root@$node.$net_domain "systemctl restart docker.service"

    # TO-DO: dnsmasq from libvirtd should provide this
    ssh root@$node.$net_domain "echo nameserver $dns2 >> /etc/resolv.conf"
    ssh root@$node.$net_domain "echo nameserver $dns3 >> /etc/resolv.conf"
  done
}

all() {
  setup;
  provision;
  post;
}

me=`whoami`
if [ $me != root ]; then
  echo "must run as root";
  exit 1;
fi

case "$1" in
    setup|provision|post|all)
        $1
        ;;
    *)
    echo 1>&2 $"Usage: $0 {setup|provision|post|all}"
    exit 1
esac

