#!/bin/bash
pid=$(pgrep -f gateway-server)
if [ -n "${pid}" ]
then
        kill -15 ${pid}
        echo kill process ${pid}
else
        echo no process
fi

chmod +x ./gateway-server-0.0.1-SNAPSHOT.jar
nohup java -jar ./gateway-server-0.0.1-SNAPSHOT.jar >> application.log 2> /dev/null &
