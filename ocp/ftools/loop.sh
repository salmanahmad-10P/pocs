#!/bin/sh

sleepSec=5;

if [ -z "$1" ]; then
    echo "No argument supplied"
elif [ "$1" -ge 1 ]; then
    sleepSec=$1
fi

while /bin/true; do
    echo "Sleeping for the following seconds:" $sleepSec
    sleep $sleepSec
done
