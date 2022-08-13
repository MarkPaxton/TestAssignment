#!/bin/bash

mkdir credentials
cd credentials || exit
openssl genrsa -out credentials.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -in credentials.pem -out private-key-pkcs8.pem -nocrypt
openssl rsa -in private-key-pkcs8.pem -pubout -out public-key.pem
cd ..