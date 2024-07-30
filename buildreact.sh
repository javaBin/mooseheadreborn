#!/bin/bash
set -e
sed -i '' 's|address: "http://localhost:8080"|address: ""|g' moose-react-app/src/ServerConfig.ts
cd moose-react-app/
npm run build
cd ..
sed -i '' 's|address: ""|address: "http://localhost:8080"|g' moose-react-app/src/ServerConfig.ts
rm -rf src/main/resources/static
cp -R moose-react-app/build src/main/resources/static

