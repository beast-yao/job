# ANSI Colors
echoRed() { echo $'\e[0;31m'"$1"$'\e[0m'; }
echoGreen() { echo $'\e[0;32m'"$1"$'\e[0m'; }
echoYellow() { echo $'\e[0;33m'"$1"$'\e[0m'; }

isRunning() {
  ps -p "$1" &> /dev/null
}

# Find Java
if [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
    JAVA="$JAVA_HOME/bin/java"
elif type -p java > /dev/null 2>&1; then
    JAVA=$(type -p java)
elif [[ -x "/usr/bin/java" ]];  then
    JAVA="/usr/bin/java"
else
    echo "Unable to find Java"
    exit 1
fi

export SERVER="bt-job-server"
export MEMBER_LIST=""
while getopts ":s:c:" opt
do
    case $opt in
        s)
            SERVER=$OPTARG;;
        c)
            MEMBER_LIST=$OPTARG;;
        ?)
        echo "Unknown parameter"
        exit 1;;
    esac
done

pid_file="$(pwd $(dirname $0))/pid"
export BASE_DIR=`cd $(dirname $0)/..; pwd`
export CUSTOM_SEARCH_LOCATIONS=file:${BASE_DIR}/conf/

#===========================================================================================
# JVM Configuration
#===========================================================================================

JAVA_OPT="${JAVA_OPT} -server -Xms2g -Xmx2g -Xmn1g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${BASE_DIR}/logs/java_heapdump.hprof"
JAVA_OPT="${JAVA_OPT} -XX:-UseLargePages"

if [ -n "$MEMBER_LIST" ]; then
    JAVA_OPT="${JAVA_OPT} -Dmain.job.member-list=${MEMBER_LIST}"
fi


#JAVA_MAJOR_VERSION=$($JAVA -version 2>&1 | sed -E -n 's/.* version "([0-9]*).*$/\1/p')
#if [[ "$JAVA_MAJOR_VERSION" -ge "9" ]] ; then
#  JAVA_OPT="${JAVA_OPT} -Xlog:gc*:file=${BASE_DIR}/logs/job_gc.log:time,tags:filecount=10,filesize=102400"
#else
#  JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${JAVA_HOME}/jre/lib/ext:${JAVA_HOME}/lib/ext"
#  JAVA_OPT="${JAVA_OPT} -Xloggc:${BASE_DIR}/logs/job_gc.log -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
#fi

JAVA_OPT="${JAVA_OPT} -Djob.home=${BASE_DIR}"
JAVA_OPT="${JAVA_OPT} -Dname=${SERVER}"
JAVA_OPT="${JAVA_OPT} -Djob.logs.path=${BASE_DIR}/logs"
JAVA_OPT="${JAVA_OPT} -jar $(pwd $(dirname $0))/${SERVER}.jar"
JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
if [ -d  "$(pwd $(dirname $0))/../conf" ]; then
    JAVA_OPT="${JAVA_OPT} --spring.config.location=../conf/"
fi

if [ -r  "${BASE_DIR}/conf/job-logback.xml" ] ; then
    JAVA_OPT="${JAVA_OPT} --logging.config=${BASE_DIR}/conf/job-logback.xml"
fi
JAVA_OPT="${JAVA_OPT} --server.max-http-header-size=524288"

if [ ! -d "${BASE_DIR}/logs" ]; then
  mkdir ${BASE_DIR}/logs
fi

start ()
{
    if [ -f pid_file ]; then
        pid=$(cat "$pid_file")
        isRunning $pid || { echoYellow "already starting [$pid]"; return 1; }
    fi

    $JAVA ${JAVA_OPT} job.srv >> ${BASE_DIR}/logs/start.log 2>&1 & echo "$!">"$pid_file"
    echoGreen "started [$(cat $pid_file)]"
}
stop()
{
    [[ -f $pid_file ]] || { echoYellow "Not running (pidfile not found)"; return 0; }
    pid="$(cat "$pid_file")"
    isRunning "$pid" || { echoYellow "Not running (process ${pid}). Removing stale pid file."; rm -f "$pid_file"; return 0; }
    kill "$pid" &> /dev/null || { echoRed "Unable to kill process $pid"; return 1; }
    STOP_WAIT_TIME=60
    for i in $(seq 1 $STOP_WAIT_TIME); do
        isRunning "$pid" || { echoGreen "Stopped [$pid]"; rm -f "$pid_file"; return 0; }
        [[ $i -eq STOP_WAIT_TIME/2 ]] && kill "$pid" &> /dev/null
        sleep 1
    done
    echoRed "Unable to kill process $1";
    return 1
}

run()
{
    if [ -f pid_file ]; then
        pid = $(cat "$pid_file")
        isRunning $pid || { echoYellow "already starting [$pid]"; return 1; }
    fi
    $JAVA ${JAVA_OPT} job.srv
}

restart ()
{
    stop && start
}

status() {
  [[ -f "$pid_file" ]] || { echoRed "Not running"; return 3; }
  pid=$(cat "$pid_file")
  isRunning "$pid" || { echoRed "Not running (process ${pid} not found)"; return 1; }
  echoGreen "Running [$pid]"
  return 0
}
# Call the appropriate action function
case "$1" in
start)
  start "$@"; exit $?;;
stop)
  stop "$@"; exit $?;;
restart)
  restart "$@"; exit $?;;
status)
  status "$@"; exit $?;;
run)
  run "$@"; exit $?;;
*)
  echo "Usage: $0 {start|stop|restart|status|run}"; exit 1;
esac
