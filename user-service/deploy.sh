#!/bin/bash
pid=$(pgrep -f user-service)
if [ -n "${pid}" ]
then
        kill -15 ${pid}
        echo kill process ${pid}
else
        echo no process
fi

chmod +x ./user-service-0.0.1-SNAPSHOT.jar
nohup java -jar ./user-service-0.0.1-SNAPSHOT.jar >> application.log 2> /dev/null &


