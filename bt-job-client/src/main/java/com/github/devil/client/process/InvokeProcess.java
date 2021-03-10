package com.github.devil.client.process;

import com.github.devil.common.enums.ResultEnums;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
public interface InvokeProcess {

    /**
     * job 执行的处理逻辑
     * @param taskContext
     * @return
     */
    public ResultEnums run(TaskContext taskContext);

}
