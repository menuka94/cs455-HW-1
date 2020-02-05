#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"
JAR_NAME="cs455-1-1.0-SNAPSHOT"
JAR_PATH="$DIR/conf/:$DIR/build/libs/$JAR_NAME.jar"
MACHINE_LIST="$DIR/conf/machine_list"
SCRIPT="java -cp $JAR_PATH cs455.overlay.node.MessagingNode <registry-host> <registry-port>"
COMMAND='gnome-terminal --geometry=200x40'
for machine in `cat $MACHINE_LIST`
do
OPTION='--tab -t "'$machine'" -e "ssh -t '$machine' cd '$DIR'; echo '$SCRIPT'; '$SCRIPT'"'
COMMAND+=" $OPTION"
done
eval $COMMAND &

832864124