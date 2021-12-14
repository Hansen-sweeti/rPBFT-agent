#!/usr/bin/env bash
echo bashshell

#dir=C:\\Users\\ASUS\\Desktop\\pbft\\pbft-agent\\src\\main\\java\\cc\\ca
echo server

openssl genrsa -out server.key 1024

openssl req -new -key server.key -out server.csr -subj "//C=CN/ST=CQ/L=CQ/O=dorby.com/OU=zlex/CN=server.dorby.com"

openssl x509 -req -in server.csr -CA rootca.pem -CAkey rootca.key -CAcreateserial -out  server.pem -days 3650 -sha256
