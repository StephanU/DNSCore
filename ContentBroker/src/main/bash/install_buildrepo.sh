#!/bin/bash

# author: Daniel M. de Oliveira

cd /ci/DNSCore/ContentBroker
REVISION_NUMBER=`git rev-parse HEAD`
cp -r target/installation /ci/BuildRepository/installation.$REVISION_NUMBER