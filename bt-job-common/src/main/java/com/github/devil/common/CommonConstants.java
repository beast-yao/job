package com.github.devil.common;

/**
 * @author eric.yao
 * @date 2021/1/20
 **/
public class CommonConstants {

    public final static String MAIN_JOB_SRV_NAME = "JOB-SERVER";

    public final static String MAIN_JOB_ACTOR_PATH = "srv";

    public final static String MAIN_JOB_WORKER_NAME = "JOB-WORKER";

    public final static String MAIN_JOB_WORKER_ACTOR_PATH = "worker";

    public final static Long WORK_HEART_BEAT = 1000L;

    public final static String COMMON_SPLIT = ",";

    public final static String AKKA_SRV_PATH = "akka://%s@%s/user/%s";

    public final static String BASE_CONTROLLER_PATH = "/svc/v1";

    public final static String BASE_TASK_CONTROLLER_PATH = "/task";

    public final static String BASE_SERVICE_CONTROLLER_PATH = "/distro";
}
