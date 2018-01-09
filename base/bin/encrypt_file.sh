#!/bin/bash
openssl des3 -a -in $1 -out $1.des3
