package com.github.devil.client.process;

import com.github.devil.client.logger.Logger;
import com.github.devil.client.logger.impl.LoggerImpl;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
@Data
public class TaskContext {

    private Long jobId;

    private Long instanceId;

    private Long workInstanceId;

    private String param;

    private String server;

    private String name;

    private Logger logger = new LoggerImpl(this);
}
