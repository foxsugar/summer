#!/bin/sh
dir="/root/gate/"
kill -9 $(ps ax|grep java |grep $dir |awk '{print$1'})