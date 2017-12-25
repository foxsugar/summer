#!/bin/sh
dir="/root/gate"
jar_prefix="gate"
cd $dir
rm -rf log
nohup java -server -Xmx3g -Xms3g \
-XX:+PrintGCDetails \
-XX:+PrintGCDateStamps \
-Xloggc:"$dir""/log/gc.log" \
-jar "$jar_prefix"*.jar >/dev/null &
tail -f log
