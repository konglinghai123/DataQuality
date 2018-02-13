#!/usr/bin/env bash

##mysql -h172.31.2.219 -P33306 -uroot -proot -Dholmes -e'select rule_name from rule;' > rules
for ruleName in `cat rules`;
do
    curl -X POST --header 'Content-Type: application/json;charset=UTF-8' --header 'Accept: application/json' -d '{
   "cron": "0 0 9 * * ?",
   "ruleName": "'$ruleName'"
 }' 'http://172.31.2.219:8088/holmes/schedule'
done