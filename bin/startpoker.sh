#!/bin/sh
dir="/root/poker"
jar_prefix="poker"
cd $dir
nohup java -server -Xmx3g -Xms3g \
-XX:+PrintGCDetails \
-XX:+PrintGCDateStamps \
-Xloggc:"$dir""/log/gc.log" \
-jar "$jar_prefix"*.jar >log &
tail -f log
