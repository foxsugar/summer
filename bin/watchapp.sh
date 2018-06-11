#!/bin/sh
while true
do
  echo 'checking...'
  zooPid=$(ps ax|grep java|grep zookeeper.properties|awk '{print$1'})

  #zookeeper 没有启动
  if [[ $zooPid ]]; then
    #statements
    echo 'zoo pid is : '$zooPid
  else
    echo 'start zoo'
    cd /root/tools/kafka_2.12-1.1.0/
    sh startZoo.sh
    sleep 7
  fi

  kafkaPid=$(ps ax|grep java |grep kafka| grep server.properties|awk '{print$1'})
  #kafka没启动
  if [[ $kafkaPid ]]; then
    #statements
    echo  'kafka pid is : '$kafkaPid

  else
    echo 'start kafka'
    cd /root/tools/kafka_2.12-1.1.0/
    sh startKafka.sh
  fi

  loginPid=$(ps ax|grep java |grep /root/login |awk '{print$1'})
  if [[ $loginPid ]]; then
    echo 'login pid is : '$loginPid
    #statements
  else
    echo 'start login'
    cd /root/summer/bin
    sh startlogind.sh
  fi

  gatePid=$(ps ax|grep java |grep /root/gate |awk '{print$1'})
  if [[ $gatePid ]]; then
    echo 'gate pid is : '$gatePid
    #statements
  else
    echo 'start gate'
    cd /root/summer/bin
    sh startgated.sh
  fi

  mjPid=$(ps ax|grep java |grep /root/mahjong |awk '{print$1'})
  if [[ $mjPid ]]; then
    echo 'mj pid is : '$mjPid
    #statements
  else
    echo 'start mj'
    cd /root/summer/bin
    sh startmjd.sh
  fi

  pokerPid=$(ps ax|grep java |grep /root/poker |awk '{print$1'})
  if [[ $pokerPid ]]; then
    echo 'poker pid is : '$pokerPid
    #statements
  else
    echo 'start poker'
    cd /root/summer/bin
    sh startpokerd.sh
  fi


 #循环周期
  sleep 60
done
