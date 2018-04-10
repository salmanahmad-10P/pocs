#!/bin/sh

OCP_DETAILS_FILE=~/.ocp_details_rc

for var in $@
do
    case "$var" in
        --OCP_DETAILS_FILE=*) OCP_DETAILS_FILE=`echo $var | cut -f2 -d\=` ;;
        -h) HELP=true ;;
        -help) HELP=true ;;
    esac
done

function help() {
    echo -en "\n\nOPTIONS:";
    echo -en "\n\t--OCP_DETAILS_FILE=*     OPTIONAL: path to ocp details file(defult = $HOME/.ocp_details_rc))"
    echo -en "\n\t-h                        this help manual\n\n"
}

function read_ocp_details() {
    if [ ! -f $OCP_DETAILS_FILE ]; then
        echo "$OCP_DETAILS_FILE : File not found!"
        exit 1;
    fi

    source $OCP_DETAILS_FILE

    if [ -z "$GUIDS" ]; then
        echo "Need to set environment variable, GUIDS, as a space delimited list of OCP cluster GUIDs";
        exit 1;
    fi

    if [ -z "$OCP_USERNAME" ]; then
        echo "Need to set environment variable, OCP_USERNAME";
        exit 1;
    fi

}

function prompt_user() {
    echo -en "\nSelect a GUID from the following list: $GUIDS\n\n"
    read GUID

    echo "The value is : $GUID"
}

function ocp_login() {
    command="oc login https://master.$GUID.openshift.opentlc.com -u $OCP_USERNAME"
    if [ -n "$OCP_PASSWORD" ]; then
        command="$command -p $OCP_PASSWORD"
    fi

    echo -en "\n\nUsing command: $command\n\n"
    eval $command
}

read_ocp_details
prompt_user
ocp_login
