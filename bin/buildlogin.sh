#!/bin/sh
baseDir="/root/summer/"
srcDir="login"
desDir="/root/login"
cd ../
git pull
cd $srcDir
gradle build -x test

if [ ! -d "$desDir" ];
		then
                mkdir "$desDir"
                cd "$baseDir""$srcDir""/src/main/resources"
                cp -a application.properties $desDir
        else
        echo "文件已存在"
fi

cd "$baseDir""$srcDir""/build/libs"
cp -a $srcDir*.war $desDir
