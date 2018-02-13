#!/usr/bin/env bash

for tableName in `cat tables`;
do
    curl -X DELETE --header 'Accept: application/json' 'http://172.31.2.219:8088/holmes/source/table/conn/zydb/table/'$tableName
done