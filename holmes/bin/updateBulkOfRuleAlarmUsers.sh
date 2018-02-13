#!/usr/bin/env bash

for ruleName in `cat rules`;

do
    curl -X PUT --header 'Content-Type: application/json;charset=UTF-8' --header 'Accept: application/json' -d '{ \
     "alarmUser": {
             "user": [
               "1228|1199"
             ],
             "email": [
               "zizi@jollycorp.com,evan@jollycorp.com"
             ] 
           }
     }' 'http://172.31.2.219:8088/holmes/rule/'$ruleName
done










