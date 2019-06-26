#!/bin/bash

docker exec -it elasticsearch /usr/share/elasticsearch/bin/elasticsearch-setup-passwords interactive -v --url https://127.0.0.1:9200 -E xpack.security.http.ssl.key=certs/elastic-certificates.key.pem -E xpack.security.http.ssl.certificate=certs/elastic-certificates.crt.pem -E xpack.security.http.ssl.certificate_authorities=certs/elastic-stack-ca.crt.pem
