#!/bin/bash
echo "CA:"
openssl pkcs12 -in elastic-stack-ca.p12 -out elastic-stack-ca.crt.pem -clcerts -nokeys
openssl pkcs12 -in elastic-stack-ca.p12 -out elastic-stack-ca.key.pem -nocerts -nodes
echo "Cert:"
openssl pkcs12 -in elastic-certificates.p12 -out elastic-certificates.crt.pem -clcerts -nokeys
openssl pkcs12 -in elastic-certificates.p12 -out elastic-certificates.key.pem -nocerts -nodes
