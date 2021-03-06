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
    echo -en "\n\t-h                       this help manual\n\n"
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

    echo -en "\nRavello environment (n/y) ?\n\n"
    read IS_RAVELLO

    # oc cluster up won't ever use OCP admin user since current convention is to use oc cluster up with root OS user
    # access to oc cluster as system:admin user subsequently occurs leveraging:  /root/.kubeconfig
    if [[ ( "localhost" != "$GUID" && "$IS_RAVELLO" != "y" ) ]]; then
        echo -en "\nUse OCP admin user (n/y) ?\n\n"
        read USE_OCP_ADMIN_USER
    fi

}

function ocp_login() {

    if [ "y" == "$USE_OCP_ADMIN_USER" ]; then
        OCP_USERNAME=$ADMIN_OCP_USERNAME
        OCP_PASSWORD=$ADMIN_OCP_PASSWORD
    fi

    command="oc login https://master.$GUID.openshift.opentlc.com -u $OCP_USERNAME -p $OCP_PASSWORD"

    # Customize for: oc cluster up
    if [ $GUID == "localhost" ]; then
        command="oc login https://$HOSTNAME:8443 -u $OCP_USERNAME -p $OCP_PASSWORD"
    fi

    if [ $IS_RAVELLO == "y" ]; then
        command="oc login https://master00-$GUID.generic.opentlc.com -u $RAVELLO_ADMIN_OCP_USERNAME -p $RAVELLO_ADMIN_OCP_PASSWORD"
    fi

    echo -en "\n\nUsing command: $command\n\n"
    eval $command
    if [ $? -ne 0 ];then
        exit 1;
    fi
}

function ocp_wildcard_domain_env_var() {
    export REGION=`oc whoami --show-server | cut -d'.' -f 2`
    if [ $GUID == "localhost" ]; then
        export SUB_DOMAIN=`echo $HOSTNAME | cut -d'.' -f 2,3,4`
        export OCP_WILDCARD_DOMAIN=apps.$SUB_DOMAIN
    elif [ $IS_RAVELLO == "y" ]; then
        export SUB_DOMAIN=$GUID.generic.opentlc.com
        export OCP_WILDCARD_DOMAIN=apps-$SUB_DOMAIN
        echo -en "\n\nNOTE: execute the following to ssh into lab environment:  ssh <opentlc-userId>@workstation-$GUID.rhpds.opentlc.com\n"
    else
        export SUB_DOMAIN=$REGION.openshift.opentlc.com
        export OCP_WILDCARD_DOMAIN=apps.$SUB_DOMAIN
    fi

    echo -en "\n\n\nNOTE: execute the following to set appropriate env vars:\n\n\
export REGION=$REGION\n\
export SUB_DOMAIN=$SUB_DOMAIN\n\
export OCP_WILDCARD_DOMAIN=$OCP_WILDCARD_DOMAIN\n\
export OCP_USERNAME=$OCP_USERNAME 
\n   "
}


read_ocp_details
prompt_user
ocp_login
ocp_wildcard_domain_env_var
