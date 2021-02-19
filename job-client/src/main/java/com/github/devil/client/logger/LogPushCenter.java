package com.github.devil.client.logger;

import com.github.devil.client.ThreadUtil;
import com.github.devil.client.akka.ClientAkkaServer;
import com.github.devil.common.request.LogContent;
import com.github.devil.common.request.LoggingRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
@Slf4j
public class LogPushCenter {

    private static AtomicBoolean starting = new AtomicBoolean(false);

    private final static DelayQueue<DelayLog> loggers = new DelayQueue<>();

    public static void log(LogContent logContent){
        loggers.add(new DelayLog(logContent));
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
            /**
             * akka server is still running
             */
            while (ClientAkkaServer.hasStart()){
                // poll log
                List<DelayLog> delayLogs = new ArrayList<>();
                loggers.drainTo(delayLogs);
                pushLog(delayLogs.stream().map(DelayLog::getLogContent).collect(Collectors.toList()));
            }

            List<LogContent> lists = new ArrayList<>();

            // local server is stop,push log to server
            while (!loggers.isEmpty()){
                DelayLog delayLog = null;
                try {
                    delayLog = loggers.poll(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignore) {

                }
                if (delayLog != null){
                    lists.add(delayLog.getLogContent());
                }
            }

            if (!lists.isEmpty()){
                pushLog(lists);
                lists.clear();
            }
        }

        private void pushLog(List<LogContent> lists){
            try {
                if (lists != null && !lists.isEmpty()) {
                    Map<String, List<LogContent>> log = lists.stream().collect(Collectors.groupingBy(LogContent::getServerHost));
                    log.forEach((s, logs) -> {
                        LoggingRequest loggingRequest = new LoggingRequest();
                        loggingRequest.setContents(logs);
                        ClientAkkaServer.getSrv(s).tell(loggingRequest, ClientAkkaServer.getActorRef());
                    });
                }
            }catch (Exception e){
                log.error("log push error,",e);
            }
        }

    }

    public static class DelayLog implements Delayed{

        @Getter
        private LogContent logContent;

        private long time;

        public DelayLog(LogContent logContent){
            Objects.requireNonNull(logContent);
            this.logContent = logContent;
            this.time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(1,TimeUnit.SECONDS);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(time - System.nanoTime(),TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (this == o){
                return 0;
            }

            if (this.logContent == null){
                return -1;
            }

            if (o instanceof DelayLog){
                DelayLog delayLog = (DelayLog)o;
                if (delayLog.getLogContent() == null){
                    return 1;
                }
            }
            return Long.compare(o.getDelay(TimeUnit.NANOSECONDS),this.getDelay(TimeUnit.NANOSECONDS));
        }
    }
}
