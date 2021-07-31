#!/bin/bash
mongo ./mongo_bootstrap.js 
mongo ./mongo_createUsers.js
sudo docker cp groovy-json-3.0.8.jar kafka_connect:/kafka/connect/debezium-connector-mongodb 
sudo docker cp groovy-3.0.8.jar kafka_connect:/kafka/connect/debezium-connector-mongodb
sudo docker cp groovy-jsr223-3.0.8.jar kafka_connect:/kafka/connect/debezium-connector-mongodb
sudo docker cp debezium-scripting-1.7.0.Alpha1.jar kafka_connect:/kafka/connect/debezium-connector-mongodb
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d "$(cat connector.json )"

# The content based routing of debezium uses java syntax so you can write your own rules
