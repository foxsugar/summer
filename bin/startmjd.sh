#!/bin/sh
dir="/root/mahjong"
jar_prefix="mahjong"
cd $dir
rm -rf log
nohup java -server -Xmx3g -Xms3g \
-XX:+PrintGCDetails \
-XX:+PrintGCDateStamps \
-Xloggc:"$dir""/log/gc.log" \
-jar "$jar_prefix"*.jar >log &
