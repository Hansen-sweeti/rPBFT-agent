#!/usr/bin/env bash
echo bashshell

mkdir private
mkdir crl

touch index.txt

echo 01>serial

openssl rand -out .rand 1000


echo CA

#生成根密钥
openssl genrsa -out rootca.key 1024

openssl req -x509  -new -nodes -key rootca.key -sha256 -days 3650 -subj  "//C=CN/ST=CQ/L=CQ/O=dorby.com/OU=zlex/CN=server.dorby.com" -out rootca.pem






