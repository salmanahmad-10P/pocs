CLUSTER_NAME=ratwater

START_DELAY=45

function shutdown() {
    virsh stop $CLUSTER_NAME-lb

    for OCP_TYPE in worker master; do
        for VM_NUM in 1 2 3; do
            virsh destroy $CLUSTER_NAME-$OCP_TYPE-$VM_NUM
        done
    done
}

function start() {
    virsh start $CLUSTER_NAME-lb

    for OCP_TYPE in master worker; do
        for VM_NUM in 1 2 3; do
            sleep $START_DELAY
            virsh start $CLUSTER_NAME-$OCP_TYPE-$VM_NUM
        done
    done
}


if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

case "$1" in
    shutdown|start)
        $1
        ;;
    *)
    echo 1>&2 $"Usage: $0 {shutdown|start}"
    exit 1
esac
