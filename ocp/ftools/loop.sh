#!/bin/sh

sleepSec=5;

if [ -z "$1" ]; then
    echo "No sleepSec argument supplied. Will default to:  $sleepSec"
elif [ "$1" -ge 1 ]; then
    sleepSec=$1
fi

# Have the container loop indefinately so as to allow a user to manually rsh in and execute commands
while /bin/true; do
    echo "Sleeping for the following seconds:" $sleepSec
    sleep $sleepSec
done
