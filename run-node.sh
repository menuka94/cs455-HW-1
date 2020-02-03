#!/bin/bash

gradle build;
cd build/classes/java/main && java -cp ../../../libs/cs455-1-1.0-SNAPSHOT.jar cs455.overlay.node.MessagingNode ${1} 5600;
