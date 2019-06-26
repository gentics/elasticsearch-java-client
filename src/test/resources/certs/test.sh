#!/bin/bash

curl -u elastic:finger --cert elastic-certificates.crt.pem --key elastic-certificates.key.pem --cacert elastic-stack-ca.crt.pem 'https://localhost:9200/_xpack/security/_authenticate?pretty'
