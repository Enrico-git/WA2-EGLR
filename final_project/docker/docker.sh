#!/bin/bash
read -p "r for restart, s to stop, rem for remove, st for starting " answer
case $answer in
	r) sudo docker container restart zookeeper kafka mongo kafka_connect;; 
 	s) sudo docker container stop kafka zookeeper mongo kafka_connect;;
        rem) sudo docker container rm kafka zookeeper mongo kafka_connect;; 
        st) sudo docker container start zookeeper kafka mongo kafka_connect;;
esac 
