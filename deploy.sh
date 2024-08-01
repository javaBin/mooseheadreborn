#!/bin/bash
set -e
./package.sh
eb deploy moosheadreborn-prod
rm app.zip
