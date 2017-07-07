#!/bin/sh
dir="/root/mahjong/"
kill -9 $(ps ax|grep java |grep $dir |awk '{print$1'})