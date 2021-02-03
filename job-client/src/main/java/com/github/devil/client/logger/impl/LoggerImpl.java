package com.github.devil.client.logger.impl;

import com.github.devil.client.akka.ClientAkkaServer;
import com.github.devil.client.logger.LogPushCenter;
import com.github.devil.client.logger.Logger;
import com.github.devil.client.process.TaskContext;
import com.github.devil.common.enums.LogLevel;
import com.github.devil.common.request.LogContent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
public class LoggerImpl implements Logger {

    private TaskContext taskContext;

    public LoggerImpl(TaskContext taskContext){
        this.taskContext = taskContext;
    }

    @Override
    public void info(String message, Object ... args) {
        LogPushCenter.log(getLogContent(LogLevel.INFO,message,args));
    }

    @Override
    public void debug(String message, Object ... args) {
        LogPushCenter.log(getLogContent(LogLevel.DEBUG,message,args));
    }

    @Override
    public void warn(String message, Object ... args) {
        LogPushCenter.log(getLogContent(LogLevel.WARNING,message,args));
    }

    @Override
    public void error(String message, Object ... args) {
        LogPushCenter.log(getLogContent(LogLevel.ERROR,message,args));
    }

    private LogContent getLogContent(LogLevel logLevel,String message, Object[] args){
        LogContent content = new LogContent();
        FormattingTuple tuple = MessageFormatter.arrayFormat(message,args);

        String formatMsg = tuple.getMessage();

        if (tuple.getThrowable() != null){
            String stack = ExceptionUtils.getStackTrace(tuple.getThrowable());
            formatMsg += System.lineSeparator() + stack;
        }

        content.setInstanceId(taskContext.getInstanceId());
        content.setJobId(taskContext.getJobId());
        content.setLoggingContent(formatMsg);
        content.setLoggingTime(new Date());
        content.setLogLevel(logLevel);
        content.setServerHost(taskContext.getServer());
        content.setWorkAddress(ClientAkkaServer.getCurrentHost());
        content.setWorkInstanceId(taskContext.getWorkInstanceId());
        return content;
    }
}
