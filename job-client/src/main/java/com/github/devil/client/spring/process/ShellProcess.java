package com.github.devil.client.spring.process;

import com.github.devil.client.process.InvokeProcess;
import com.github.devil.client.process.TaskContext;
import com.github.devil.common.enums.ResultEnums;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author eric.yao
 * @date 2021/3/5
 **/
@Data
@AllArgsConstructor
public class ShellProcess implements InvokeProcess {

    @Override
    public ResultEnums run(TaskContext taskContext) {
        return null;
    }
}
