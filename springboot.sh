#! /bin/sh
#chmod a+x springboot.sh
#环境参数
package=schedule
profiles=dev
serverPort=9606
#启动方法
start(){
 now=`date "+%Y%m%d"`
 exec java -Xms256m -Xmx2048m -jar "$package".jar --spring.profiles.active="$profiles" --server.port="$serverPort"> logs/"$package"_"$now".log &
}
#停止方法
stop(){
 ps -ef|grep "$package"|grep java |grep "$serverPort" |awk '{print $2}'|while read pid
 do
    kill -9 $pid
 done
}

case "$1" in
start)
start
;;
stop)
stop
;;
restart)
stop
start
;;
*)
printf 'Usage: %s {start|stop|restart}\n' "$prog"
exit 1
;;
esac