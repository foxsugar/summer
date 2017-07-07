#!/bin/sh
dir="/root/login/"
kill -9 $(ps ax|grep java |grep $dir |awk '{print$1'})