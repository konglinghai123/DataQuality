#!/usr/bin/env bash

for ruleName in `cat rules`;
do
    curl -X DELETE --header 'Accept: application/json' 'http://172.31.2.219:8088/holmes/rule/'$ruleName
done