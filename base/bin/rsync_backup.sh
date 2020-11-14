#!/bin/sh

### ======================================== ###
###    JA Bride , 29 Nov 2010                ###
### ======================================== ###

PHOTOS_HOME=/u02/photos
AUDIO_HOME=/u02/audio
CUSTOMERS_HOME=/u02/customers
OLD_SOFTWARE_HOME=/u02/oldSoftware
VIDEO_HOME=/u02/video
DOWNLOADS_HOME=/u02/downloads
VIRTUAL_MACHINES=/u02/vm
RECORDINGS=/u02/redhat/recordings
THUNDERBIRD_HOME=/u02/thunderbird

REMOTE_USER=jbride
REMOTE_IP=poweredge
#RSYNC_PATH="/external"
RSYNC_PATH="jbride@$REMOTE_IP:/u02/backup"

# simple-mtpfs $HOME/phone
# sudo umount $HOME/phone
LOCAL_DROID_PHOTOS_PATH=$PHOTOS_HOME/androidone
RSYNC_DROID_PATH=/external

syncLocalFromBackup() {
    cd $HOME
    echo " ***** now synching in : $HOME from $RSYNC_PATH" 
    rsync   -trv \
            --include=* \
            $RSYNC_PATH/jbride /u02

    rsync -trv $RSYNC_PATH/photos /u02
    rsync -trv $RSYNC_PATH/audio /u02
    rsync -trv $RSYNC_PATH/oldSoftware /u02
}

syncBackupJbrideFromLocal() {
    cd $HOME    
    echo " ***** now synching in : $RSYNC_PATH with $HOME"    
    rsync -trv --delete . \
               --include=./My\ Kindle\ Content \
               --include=.ssh \
               --include=.ethereum/keystore \
               --include=.gnupg \
               --include=.electrum \
               --include=.thunderbird \
               --include=.config/Slack \
               --include=.config/Atom \
               --exclude=.* \
               --exclude=**/*.txt \
               --exclude=Downloads \
               --exclude=lab \
               --exclude=provisioning_output \
               --exclude=bin \
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
} 
        

syncAllBackupFromLocal() {

    syncBackupJbrideFromLocal

    mkdir -p $PHOTOS_HOME; cd $PHOTOS_HOME    
    echo " ***** now synching from $PHOTOS_HOME to :  $RSYNC_PATH"    
    rsync -trv . --exclude=.* $RSYNC_PATH/photos    
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi
    
    mkdir -p $AUDIO_HOME; cd $AUDIO_HOME    
    echo " ***** now synching from : $AUDIO_HOME to :  $RSYNC_PATH"    
    rsync -trv . --delete --exclude=.* $RSYNC_PATH/audio    
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi    
        
    mkdir -p $VIDEO_HOME; cd $VIDEO_HOME    
    echo " ***** now synching from $VIDEO_HOME to  $RSYNC_PATH"    
    rsync -trv . --exclude=.* $RSYNC_PATH/video    
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi    

    mkdir -p $OLD_SOFTWARE_HOME; cd $OLD_SOFTWARE_HOME    
    echo " ***** now synching from $OLD_SOFTWARE_HOME to  $RSYNC_PATH"    
    rsync -trv --delete . --exclude=.* $RSYNC_PATH/oldSoftware    
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi 
   

    #mkdir -p $THUNDERBIRD_HOME; cd $THUNDERBIRD_HOME    
    #echo " ***** now synching from $THUNDERBIRD_HOME to  $RSYNC_PATH"    
    #rsync -trv . --exclude=.* $RSYNC_PATH/thunderbird   
    #rsyncReturnCode=$?    
    #if [ $rsyncReturnCode -ne 0 ];then    
    #    exit 1;    
    #fi    

    #mkdir -p $RECORDINGS; cd $RECORDINGS
    #echo " ***** now synching from $RECORDINGS to  $RSYNC_PATH"    
    #rsync -trv --delete . --exclude=.* $RSYNC_PATH/recordings    
    #rsync -trv . --exclude=* $RSYNC_PATH/recordings    
    rsyncReturnCode=$?    
    #if [ $rsyncReturnCode -ne 0 ];then    
    #    exit 1;    
    #fi 
   

    #mkdir -p $CUSTOMERS_HOME; cd $CUSTOMERS_HOME    
    #echo " ***** now synching from $CUSTOMERS_HOME to  $RSYNC_PATH"    
    #rsync -trv --delete . --exclude=.* --exclude=hp/sdm/jboss $RSYNC_PATH/customers    
    #rsyncReturnCode=$?    
    #if [ $rsyncReturnCode -ne 0 ];then    
    #    exit 1;    
    #fi    

        
    #mkdir -p $DOWNLOADS_HOME; cd $DOWNLOADS_HOME    
    #echo " ***** now synching from $DOWNLOADS_HOME to  $RSYNC_PATH"    
    #rsync -trv --delete . --exclude=.* $RSYNC_PATH/downloads    
    #rsyncReturnCode=$?    
    #if [ $rsyncReturnCode -ne 0 ];then    
    #    exit 1;    
    #fi    

    #mkidr -p $VIRTUAL_MACHINES; cd $VIRTUAL_MACHINES   
    #echo " ***** now synching from $VIRTUAL_MACHINES to  $RSYNC_PATH"    
    #rsync -trv --delete . --exclude=.* --exclude=docker* $RSYNC_PATH/virtual_machines    
    #rsyncReturnCode=$?    
    #if [ $rsyncReturnCode -ne 0 ];then    
    #    exit 1;    
    #fi    
}

syncDroidFromLocal() {
    cd $AUDIO_HOME/default    
    echo " ***** now synching from $AUDIO_HOME to  $RSYNC_DROID_PATH/audio/default"    
    rsync -trv --delete . $RSYNC_DROID_PATH/audio/default 
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi    

    cd $AUDIO_HOME/boomChakalaka    
    echo " ***** now synching from $AUDIO_HOME to  $RSYNC_DROID_PATH/audio/boomChakalaka"    
    rsync -trv --delete . $RSYNC_DROID_PATH/audio/boomChakalaka 
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi    

    cd $AUDIO_HOME/christmas    
    echo " ***** now synching from $AUDIO_HOME to  $RSYNC_DROID_PATH/audio/christmas"    
    rsync -trv --delete . $RSYNC_DROID_PATH/audio/christmas 
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi    

    cd $AUDIO_HOME/lounge    
    echo " ***** now synching from $AUDIO_HOME to  $RSYNC_DROID_PATH/audio/lounge"    
    rsync -trv --delete . $RSYNC_DROID_PATH/audio/lounge
    rsyncReturnCode=$?    
    if [ $rsyncReturnCode -ne 0 ];then    
        exit 1;    
    fi    

    cd $AUDIO_HOME/faith    
    echo " ***** now synching from $AUDIO_HOME to  $RSYNC_DROID_PATH/audio/faith"    
    rsync -trv --delete . $RSYNC_DROID_PATH/audio/faith
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

    cd $RSYNC_DROID_PATH/DCIM
    echo " ***** now synching from $RSYNC_DROID_PATH/DCIM to  $LOCAL_DROID_PHOTOS_PATH"
    rsync -trv . $LOCAL_DROID_PHOTOS_PATH/DCIM
    rsyncReturnCode=$?
    if [ $rsyncReturnCode -ne 0 ];then
        exit 1;
    fi

    cd $RSYNC_DROID_PATH/Download
    echo " ***** now synching from $RSYNC_DROID_PATH/download  to $LOCAL_DROID_PHOTOS_PATH/download"
    rsync -trv . --include=*.jpeg $LOCAL_DROID_PHOTOS_PATH/download
    rsyncReturnCode=$?
    if [ $rsyncReturnCode -ne 0 ];then
        exit 1;
    fi
}

createTarBundles() {
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
    syncDroidFromLocal|syncLocalFromDroid|syncLocalFromBackup|createTarBundles|syncBackupJbrideFromLocal|syncAllBackupFromLocal)
        $1
        ;;
    *)
    echo 1>&2 $"Usage: $0 {syncDroidFromLocal|syncLocalFromDroid|syncLocalFromBackup|createTarBundles|syncBackupJbrideFromLocal|syncAllBackupFromLocal}"
    exit 1
esac

