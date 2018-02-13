#!/usr/bin/env bash

ruleName=$1
do
    curl -X GET --header 'Accept: application/json' 'http://172.31.2.219:8088/execution/submit/'$ruleName
done










