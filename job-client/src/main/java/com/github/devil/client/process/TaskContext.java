package com.github.devil.client.process;

import com.github.devil.client.logger.Logger;
import com.github.devil.client.logger.impl.LoggerImpl;
import com.github.devil.common.enums.ExecuteType;
import com.github.devil.common.enums.TaskType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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

    private TaskType taskType;

    private String meatInfo;

    private Map<String,Object> extension = new HashMap<>();

    private Logger logger = new LoggerImpl(this);
}
