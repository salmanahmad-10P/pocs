#!/bin/bash

#   use if interested in output to a file
openssl des3 -d -a -in $1 -out $2

#   use if interested in output to std out
#openssl des3 -d -a -in $1 
