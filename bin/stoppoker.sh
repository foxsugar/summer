#!/bin/sh
dir="/root/poker/"
kill -9 $(ps ax|grep java |grep $dir |awk '{print$1'})