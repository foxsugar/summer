#!/bin/sh
dir="/root/login"
jar_prefix="login"
cd $dir
nohup java -server -Xmx3g -Xms3g \
-XX:+PrintGCDetails \
-XX:+PrintGCDateStamps \
-Xloggc:"$dir""/log/gc.log" \
-jar "$jar_prefix"*.war >log &
tail -f log
