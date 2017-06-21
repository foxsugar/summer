#!/bin/sh  
cd ../
git pull
cd login
gradle build -x test
cd build/libs
cp -a login.war /root/login/