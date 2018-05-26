#!/bin/sh

### ======================================== ###
###    JA Bride , 29 Nov 2010                ###
### ======================================== ###

# rsync to droid
# - install sshdroid app on phone
# - (phone):  mkdir /sdcard/audio/
# - (laptop): rsync -trv --delete --progress -e 'ssh -p 2222' /u02/audio/default/ root@192.168.0.128:/sdcard/audio/default

PHOTOS_HOME=/u02/photos
AUDIO_HOME=/u02/audio
CUSTOMERS_HOME=/u02/customers
OLD_SOFTWARE_HOME=/u02/oldSoftware
VIDEO_HOME=/u02/video
DOWNLOADS_HOME=/u02/downloads
VIRTUAL_MACHINES=/u02/virtual_machines
RECORDINGS=/u02/redhat/recordings

REMOTE_USER=jbride
REMOTE_IP=localhost
RSYNC_PATH="/run/media/jbride/_u03"

# simple-mtpfs $HOME/phone
# sudo umount $HOME/phone
RSYNC_DROID_PATH="$HOME/phone/"
LOCAL_DROID_PHOTOS_PATH=$PHOTOS_HOME/moto2017

syncLocalFromBackup() {
    cd $HOME
    echo " ***** now synching in : $HOME from $RSYNC_PATH" 
    rsync   -trv \
            --include=* \
            $RSYNC_PATH/jbride /home   

    rsync -trv $RSYNC_PATH/photos /u02
    rsync -trv $RSYNC_PATH/audio /u02
    rsync -trv $RSYNC_PATH/oldSoftware /u02
}

syncBackupFromLocal() {
    echo -en "\n\n***** now synching in : $RSYNC_PATH with $HOME"    
    cd $HOME    
#   rsync -trv --delete . \
    rsync -trv . \
               --include=.bashrc \
               --include=.vimrc \
               --include=.gitconfig \
               --include=./My\ Kindle\ Content \
               --include=.ssh \
               --include=.m2/*.xml \
               --include=.ethereum/keystore \
               --include=.gnupg \
               --include=.electrum \
               --include=.thunderbird \
               --include=.rhtoken.json \
               --include=.ApacheDirectoryStudio \
               --include=.ocp_details_rc \
               --exclude=.* \
               --exclude=**/*.txt \
               --exclude=Downloads \
               --exclude=lab \
               --exclude=Music \
               --exclude=Public \
               --exclude=Templates \
               --exclude=Videos \
               --exclude=Documents \
               $RSYNC_PATH/jbride    
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi    
        
    echo -en "\n\n***** now synching in : $PHOTOS_HOME at :  $RSYNC_PATH"    
    cd $PHOTOS_HOME    
    if [ $? -ne 0 ];then    
        exit 1;    
    fi    
    rsync -trv . --exclude=.* $RSYNC_PATH/photos    
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi
    
    echo -en "\n\n ***** now synching in : $AUDIO_HOME at :  $RSYNC_PATH"    
    cd $AUDIO_HOME    
    if [ $? -ne 0 ];then    
        exit 1;    
    fi    
    rsync -trv . --exclude=.* $RSYNC_PATH/audio    
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi    
        
    echo -en "\n\n***** now synching in : $OLD_SOFTWARE_HOME at :  $RSYNC_PATH"    
    cd $OLD_SOFTWARE_HOME    
    if [ $? -ne 0 ];then    
        exit 1;    
    fi    
    rsync -trv --delete . --exclude=.* $RSYNC_PATH/oldSoftware    
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi 
   
    echo -en "\n\n***** now synching in : $RECORDINGS at :  $RSYNC_PATH"    
    cd $RECORDINGS
    if [ $? -ne 0 ];then    
        exit 1;    
    fi    
    rsync -trv . --exclude=* $RSYNC_PATH/recordings    
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi 
   
    echo -en "\n\n***** now synching in : $VIDEO_HOME at :  $RSYNC_PATH"    
    cd $VIDEO_HOME    
    if [ $? -ne 0 ];then    
        exit 1;    
    fi    
    rsync -trv . --exclude=.* $RSYNC_PATH/video    
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi    


    #echo "\n\n***** now synching in : $CUSTOMERS_HOME at :  $RSYNC_PATH"    
    #cd $CUSTOMERS_HOME    
    #rsync -trv --delete . --exclude=.* --exclude=hp/sdm/jboss $RSYNC_PATH/customers    
    #rsyncReturnCode=$?    
    #if [ $rsyncReturnCode -ne 0 ];then    
    #    exit 1;    
    #fi    

        
    #echo -ne "\n\n ***** now synching in : $DOWNLOADS_HOME at :  $RSYNC_PATH"    
    #cd $DOWNLOADS_HOME    
    #rsync -trv --delete . --exclude=.* $RSYNC_PATH/downloads    
    #rsyncReturnCode=$?    
    #if [ $rsyncReturnCode -ne 0 ];then    
    #    exit 1;    
    #fi    

    #echo -en "\n\n***** now synching in : $VIRTUAL_MACHINES at :  $RSYNC_PATH"    
    #cd $VIRTUAL_MACHINES   
    #rsync -trv --delete . --exclude=.* --exclude=docker* $RSYNC_PATH/virtual_machines    
    #rsyncReturnCode=$?    
    #if [ $rsyncReturnCode -ne 0 ];then    
    #    exit 1;    
    #fi    
}

syncDroidFromLocal() {
    cd $AUDIO_HOME/default    
    echo " ***** now synching in : $AUDIO_HOME at :  $RSYNC_DROID_PATH/Music/default"    
    rsync -trv --delete . $RSYNC_DROID_PATH/Music/default 
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi    
}

syncLocalFromDroid() {
    if [ ! -d "$RSYNC_DROID_PATH" ]; then
        echo "$RSYNC_DROID_PATH does not exist";
        exit 1;
    fi
    if [ ! -d "$LOCAL_DROID_PHOTOS_PATH" ]; then
        echo "$LOCAL_DROID_PHOTOS_PATH does not exist";
        exit 1;
    fi

    cd $RSYNC_DROID_PATH/SD\ card/DCIM
    echo " ***** now synching in : $LOCAL_DROID_PHOTOS_PATH with $RSYNC_DROID_PATH/DCIM"
    rsync -trv . $LOCAL_DROID_PHOTOS_PATH/DCIM
    rsyncReturnCode=$?
    if [ $rsyncReturnCode -ne 0 ];then
        exit 1;
    fi

    cd $RSYNC_DROID_PATH/SD\ card/Download
    echo " ***** now synching in : $LOCAL_DROID_PHOTOS_PATH/download with $RSYNC_DROID_PATH/download"
    rsync -trv . --include=*.jpeg $LOCAL_DROID_PHOTOS_PATH/download
    rsyncReturnCode=$?
    if [ $rsyncReturnCode -ne 0 ];then
        exit 1;
    fi
}

syncRemoteFromHome() {
  cd /tmp
  file_name=home_`date +%s`.tar
  #tar -cvf $file_name --exclude=$HOME/.cache --exclude=$HOME/.m2 $HOME
  #gzip $file_name

  cd /u02/
  file_name=photos_`date +%s`.tar
  tar -cvf $file_name photos
}

_TestSSH() {
    local user=${1}
    local timeout=${2}

    ssh -q -q -o "BatchMode=yes" -o "ConnectTimeout ${timeout}"  -l $REMOTE_USER $REMOTE_IP "echo 2>&1" && return 0 || {
        echo "WARN:  no access to: $REMOTE_IP";
        REMOTE_IP=ratwater.dyndns.org
        echo "WARN:  will try again with: $REMOTE_IP";
        ssh -q -q -o "BatchMode=yes" -o "ConnectTimeout ${timeout}" -l $REMOTE_USER $REMOTE_IP "echo 2>&1" && return 0 || {
            echo "ERROR: no access to: $REMOTE_IP";
            exit 1;
        }
    }
}


#_TestSSH jbride 5

case "$1" in
    syncDroidFromLocal|syncLocalFromDroid|syncLocalFromBackup|syncRemoteFromHome|syncBackupFromLocal)
        $1
        ;;
    *)
    echo 1>&2 $"Usage: $0 {syncDroidFromLocal|syncLocalFromDroid|syncLocalFromBackup|syncRemoteFromHome|syncBackupFromLocal}"
    exit 1
esac

