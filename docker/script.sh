#!/bin/bash
mongo ./mongo_bootstrap.js 
mongo ./mongo_createUsers.js
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d "$(cat connector.json )"
