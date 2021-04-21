cd `dirname $0`/../target
target_dir=`pwd`

pid=`ps ax | grep -i 'job.srv' | grep ${target_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "No job srv running."
        exit -1;
fi

echo "The job srv(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to job srv(${pid}) OK"
