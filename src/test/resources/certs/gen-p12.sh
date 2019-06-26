#!/bin/bash

rm *.p12
$HOME/es/latest/bin/elasticsearch-certutil ca   --out $PWD/elastic-stack-ca.p12 
$HOME/es/latest/bin/elasticsearch-certutil cert --ca $PWD/elastic-stack-ca.p12 --out $PWD/elastic-certificates.p12 --name localhost --dns localhost --ip 127.0.0.1