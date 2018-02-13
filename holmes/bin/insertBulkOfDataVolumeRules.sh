#!/usr/bin/env bash

for tableName in `cat tables`;
do
curl -X POST --header 'Content-Type: application/json;charset=UTF-8' --header 'Accept: application/json' -d '{
  "alarmType": "both",
  "alarmUser": {
    "email": [
      "evan@jollycorp.com","zizi@jollycorp.com"
    ],
    "user": [
      "1199","1228"
    ]
  },
  "ruleDescription": "",
  "ruleInfo": {
    "operator": "<",
    "value": 1
  },
  "ruleName": "'$tableName'_dataVolume",
  "ruleType": "dataVolume",
  "sourceInfo": {
    "partitionName": "data_date",
    "partitionValueFormat": "yyyyMMdd",
    "sourceConnectionName": "zydb",
    "sourceTableName": "'$tableName'"
  }
}' 'http://172.31.2.219:8088/holmes/rule/type/dataVolume';
done