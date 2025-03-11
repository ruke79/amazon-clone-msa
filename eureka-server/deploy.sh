#!/bin/bash
pid=$(pgrep -f eureka-server)
if [ -n "${pid}" ]
then
        kill -15 ${pid}
        echo kill process ${pid}
else
        echo no process
fi

chmod +x ./eureka-server-0.0.1-SNAPSHOT.jar
nohup java -jar ./eureka-server-0.0.1-SNAPSHOT.jar >> application.log 2> /dev/null &
