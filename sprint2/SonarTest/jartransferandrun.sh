#!/bin/bash

JAR_OUPUT_PATH=build/libs
REMOTE_DEST_PATH=/home/pi/alarmctx
JARNAME=it.unibo.ctxalarm.MainCtxalarmKt-1.0.jar
IP=192.168.1.115
port=8097

if [ -z "$1" ] ; then
    gradle -PmainClass=it.unibo.ctxalarm.MainCtxalarmKt jar
    scp $JAR_OUPUT_PATH/$JARNAME pi@$IP:$REMOTE_DEST_PATH
    scp SonarAlone.c pi@$IP:$REMOTE_DEST_PATH
    scp SonarAlone pi@$IP:$REMOTE_DEST_PATH
    scp sysRules.pl pi@$IP:$REMOTE_DEST_PATH
    scp demo0.pl pi@$IP:$REMOTE_DEST_PATH
else
  echo "skipping compilation and transfer"
fi

ssh pi@$IP "sudo kill -9 $( sudo ss -ntlp | grep $port  | awk -F 'pid=' '{ print $2 }' | cut -d',' -f1 )"
ssh pi@$IP -t "cd $REMOTE_DEST_PATH; java -jar $JARNAME"
ssh pi@$IP "sudo kill -9 $( sudo ss -ntlp | grep $port  | awk -F 'pid=' '{ print $2 }' | cut -d',' -f1 )"