#!/bin/sh  
srcDir = "gate"
desDir = "/root/gate"
cd ../
git pull
cd $srcDir
gradle build -x test

if[! -d $desDir]
	then
		mkdir $desDir
		cd ../../src/main/resources
		cp -a application.properties $desDir
	else
		echo ""
fi

cd build/libs
cp -a $srcDir*.jar $desDir