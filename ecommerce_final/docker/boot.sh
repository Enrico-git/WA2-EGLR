#!/bin/bash
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" $(awk 'END{print $1}' /etc/hosts):8083/connectors/ -d "$(cat /kafka/bootstrap/connectorWallet.json )"
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" $(awk 'END{print $1}' /etc/hosts):8083/connectors/ -d "$(cat /kafka/bootstrap/connectorWarehouse.json )"
