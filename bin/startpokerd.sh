#!/bin/sh
dir="/root/poker"
jar_prefix="poker"
cd $dir
rm -rf log
nohup java -server -Xmx3g -Xms3g \
-XX:+PrintGCDetails \
-XX:+PrintGCDateStamps \
-Xloggc:"$dir""/log/gc.log" \
-jar "$jar_prefix"*.jar >log &
