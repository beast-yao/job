package com.github.devil.client.logger;

import com.github.devil.client.ThreadUtil;
import com.github.devil.client.akka.ClientAkkaServer;
import com.github.devil.common.request.LogContent;
import com.github.devil.common.request.LoggingRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
public class LogPushCenter {

    private static AtomicBoolean starting = new AtomicBoolean(false);

    /**
     * log max push size
     */
    private final static int BATCH_SIZE = 2<<5;

    private final static BlockingQueue<LogContent> loggers = new LinkedBlockingQueue<>();

    public static void log(LogContent logContent){
        loggers.add(logContent);
        if (starting.compareAndSet(false,true)){
            ThreadUtil.GLOBAL.execute(new LogPushRunner());
        }
    }

    /**
     * log push thread
     */
    private static class LogPushRunner implements Runnable{

        @Override
        public void run() {

            List<LogContent> lists = new ArrayList<>();

            /**
             * akka server is still running
             */
            while (ClientAkkaServer.hasStart()){
                try {
                    // poll log
                    LogContent request = loggers.poll(100, TimeUnit.MILLISECONDS);
                    if (request != null){
                        lists.add(request);
                    }

                    // logs size more than max batch push to server
                    if (lists.size() > BATCH_SIZE){
                      pushLog(lists);
                      lists.clear();
                    }

                } catch (InterruptedException ignore) {

                }
            }

            // local server is stop,push log to server
            while (!loggers.isEmpty()){
                LogContent request = null;
                try {
                    request = loggers.poll(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignore) {

                }
                if (request != null){
                    lists.add(request);
                }
            }

            if (!lists.isEmpty()){
                pushLog(lists);
                lists.clear();
            }
        }

        private void pushLog(List<LogContent> lists){
            Map<String,List<LogContent>> log =  lists.stream().collect(Collectors.groupingBy(LogContent::getServerHost));
            log.forEach((s,logs) -> {
                LoggingRequest loggingRequest = new LoggingRequest();
                loggingRequest.setContents(logs);
                ClientAkkaServer.getSrv(s).tell(loggingRequest,ClientAkkaServer.getActorRef());
            });
        }

    }
}
