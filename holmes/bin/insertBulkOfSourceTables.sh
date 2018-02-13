#!/usr/bin/env bash

##mysql -h172.31.2.16 -P3306 -uholmes -pholmes@Saiku18 -Dzydb -e'show tables;' > tables
for tableName in `cat tables`;
do
curl -X POST --header 'Content-Type: application/json;charset=UTF-8' --header 'Accept: application/json' -d '{
   "connectionName": "zydb",
   "tableName": "'$tableName'"
 }' 'http://172.31.2.219:8088/holmes/source/table';
done