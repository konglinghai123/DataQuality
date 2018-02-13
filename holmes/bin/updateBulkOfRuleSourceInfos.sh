#!/usr/bin/env bash

for tableName in `cat tables`;
do
    curl -X PUT --header 'Content-Type: application/json;charset=UTF-8' --header 'Accept: application/json' -d '{
       "sourceInfo": {
         "partitionName": "data_date",
         "partitionValueFormat": "yyyy-MM-dd",
         "sourceConnectionName": "zydb",
         "sourceTableName": "'$tableName'"
       }
     }' 'http://172.31.2.219:8088/holmes/rule/'$tableName'_dataVolume'
done