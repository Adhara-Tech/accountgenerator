#!/bin/bash
set -eo pipefail

docker run --rm --name accountgenerator \
  -v $PWD/pwd:/etc/accountgenerator/keyfiles/pwd \
  -p 7545:7545 \
  adharaprojects/accountgenerator:1.0.0-ADHARA-SNAPSHOT \
  --http-listen-port=7545 \
  --http-listen-host="0.0.0.0" \
  --directory="/etc/accountgenerator/keyfiles/" \
  file-based-account-generator \
  --password-file="/etc/accountgenerator/keyfiles/pwd" \
