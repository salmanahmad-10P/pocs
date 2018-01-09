#!/bin/sh

ls $1
jar -tf $1 | grep -n $2
