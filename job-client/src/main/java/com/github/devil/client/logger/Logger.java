package com.github.devil.client.logger;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
public interface Logger {

    /**
     * info logger
     * @param message
     * @param args
     */
    public void info(String message,Object ... args);

    /**
     * debug log
     * @param message
     * @param args
     */
    public void debug(String message,Object ... args);


    /**
     * warning log
     * @param message
     * @param args
     */
    public void warn(String message,Object ... args);

    /**
     * error log
     * @param message
     * @param args
     */
    public void error(String message,Object ... args);

}
