#!/bin/bash

# Assumptions:
## Existing disk mounts :   /u0A, /u0B and /u0C

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
echo -en "\nSCRIPT_DIR = $SCRIPT_DIR\n\n"

MAX_PV_ID=30

SLEEP_TIME=1

NFS_HOST=192.168.122.1


function cleanMount() {
        echo -en "\ncleanMount: /u0$MAJOR_DISK/$DIR_NAME\n"
        DIR_NAME=u0$MAJOR_DISK$MINOR_DISK
        rm -rf /u0$MAJOR_DISK/$DIR_NAME
}

function cleanAll() {
    for MAJOR_DISK in A B C; do
        for ((MINOR_DISK=10;MINOR_DISK<=$MAX_PV_ID;MINOR_DISK++)); do 
            cleanMount
        done
    done

    echo "" > /etc/exports
    exportfs -ra
    oc delete pv --all

}

function create() {

      # Create NFS mounts
      DIR_NAME=u0$MAJOR_DISK$MINOR_DISK
      mkdir -p /u0$MAJOR_DISK/$DIR_NAME
      chown -R rpcuser:rpcuser /u0$MAJOR_DISK/$DIR_NAME

      # Create corresponding PV
      lowercase="${MAJOR_DISK,,}"
      PV_NAME=pvu0$lowercase$MINOR_DISK

      # https://docs.openshift.com/container-platform/4.4/storage/persistent_storage/persistent-storage-nfs.html
      cat $SCRIPT_DIR/pv.yaml | sed "s/{PV_NAME}/$PV_NAME/g" | sed "s/{NFS_HOST}/$NFS_HOST/g" | sed "s/{MAJOR_DISK}/$MAJOR_DISK/g" | sed "s/{DIR_NAME}/$DIR_NAME/g"| oc create -f -
      chmod -R 777 /u0$MAJOR_DISK/$DIR_NAME
}

function refresh() {

    for MAJOR_DISK in A B C; do

      # verify by executing the following on the NFS host:  mount -l

      grep "u0$MAJOR_DISK" /etc/exports
      if [ $? -eq 1 ]; then
        echo -en "u0$MAJOR_DISK does not exist in /etc/exports.  Need to add\n"
        echo "/u0$MAJOR_DISK    192.168.122.0/24(rw,sync,no_subtree_check)" >> /etc/exports
        exportfs -ra
        chown -R rpcuser:rpcuser /u0$MAJOR_DISK
      fi

      for ((MINOR_DISK=10;MINOR_DISK<=$MAX_PV_ID;MINOR_DISK++)); do 

        # Create PV
        DIR_NAME=u0$MAJOR_DISK$MINOR_DISK
        lowercase="${MAJOR_DISK,,}"
        PV_NAME=pvu0$lowercase$MINOR_DISK
        status=$(oc get pv $PV_NAME -o template --template {{.status.phase}})
        if [ $? -eq 0 ];then
            if [ "Released" = $status ]; then
	      echo -en "\n\nrefresh() About to refresh the following PV and underlying filesytem: $PV_NAME\n"
              oc delete pv $PV_NAME
              absolute_path=/u0$MAJOR_DISK/$DIR_NAME
              rm -rf $absolute_path/*
              cat $SCRIPT_DIR/pv.yaml | sed "s/{PV_NAME}/$PV_NAME/g" | sed "s/{NFS_HOST}/$NFS_HOST/g" | sed "s/{MAJOR_DISK}/$MAJOR_DISK/g" | sed "s/{DIR_NAME}/$DIR_NAME/g"| oc create -f -
            fi
        else
	    create
            echo -en "refresh() the following PV is now created: $PV_NAME  .\n\n"
        fi
      done
    done
}

if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

if ! command -v oc &> /dev/null
then
    echo "oc utility could not be found on PATH = $PATH"
    exit
fi

case "$1" in
    refresh|cleanAll)
        $1
        ;;
    *)
    echo 1>&2 $"Usage: $0 {refresh|cleanAll}"
    exit 1
esac


