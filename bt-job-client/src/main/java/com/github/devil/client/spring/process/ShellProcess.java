package com.github.devil.client.spring.process;

import com.github.devil.client.logger.Logger;
import com.github.devil.client.process.InvokeProcess;
import com.github.devil.client.process.TaskContext;
import com.github.devil.common.enums.LogLevel;
import com.github.devil.common.enums.ResultEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author eric.yao
 * @date 2021/3/5
 **/
@Data
@Slf4j
@AllArgsConstructor
public class ShellProcess implements InvokeProcess {

    @Override
    public ResultEnums run(TaskContext taskContext) {
        Logger logger = taskContext.getLogger();
        try {
            String meta = taskContext.getMeatInfo();

            if (meta == null || meta.isEmpty()){
                logger.error("can not execute shell task, shell script is empty");
                return ResultEnums.F;
            }

            String shellRootPath = getScriptDir();
            String fileName = writeShell(taskContext,shellRootPath);

            Runtime runtime = Runtime.getRuntime();
            // 修改权限
            if (isWindows()){
                Process process = runtime.exec(new String[]{"cmd","/Q","/c",fileName});

                new Thread(() -> logFromIo(process.getInputStream(),logger,LogLevel.INFO)).start();
                new Thread(() -> logFromIo(process.getErrorStream(),logger,LogLevel.ERROR)).start();

                process.waitFor(30, TimeUnit.SECONDS);
            } else {
                runtime.exec(new String[]{"/bin/chmod","777",fileName}).waitFor();
                Process process = runtime.exec(new String[]{"/bin/sh",fileName});

                new Thread(() -> logFromIo(process.getInputStream(),logger,LogLevel.INFO)).start();
                new Thread(() -> logFromIo(process.getErrorStream(),logger,LogLevel.ERROR)).start();

                process.waitFor(30, TimeUnit.SECONDS);
            }

            return ResultEnums.S;
        }catch (Exception e){
            log.error("execute script error,jobId [{}],instanceId:[{}],error:",taskContext.getJobId(),taskContext.getInstanceId(),e);
            logger.error("execute script error,jobId [{}],instanceId:[{}],error:",taskContext.getJobId(),taskContext.getInstanceId(),e);
            taskContext.getLogger().error("execute script error,jobId [{}],instanceId:[{}],error:",taskContext.getJobId(),taskContext.getInstanceId(),e);
            return ResultEnums.F;
        }
    }

    private String getScriptDir(){
        return SystemUtils.getJavaIoTmpDir().getAbsolutePath();
    }

    private String getFileName(TaskContext taskContext){
       return "_"+taskContext.getInstanceId()+"_"+taskContext.getClass().getName()+(isWindows()?".bat":".sh");
    }

    private String writeShell(TaskContext taskContext,String rootPath) throws IOException {

        String shellName = getFileName(taskContext);
        File file = new File(rootPath,shellName);
        try (FileWriter writer = new FileWriter(file, true)){
            writer.write(taskContext.getMeatInfo());
            writer.flush();
        }
        return file.getAbsolutePath();
    }

    private void logFromIo(InputStream in, Logger logger, LogLevel level) {
        try {
            byte[] bytes = new byte[1024];
            int length = 0;
            StringBuilder builder = new StringBuilder();
            while ((length = in.read(bytes)) > 0){
                builder.append(new String(bytes,0,length,StandardCharsets.UTF_8));
            }
            logger.log(builder.toString(),level);
        }catch (Exception e){
            logger.error("read script execute res error,",e);
        }
    }

    private boolean isWindows(){
        return SystemUtils.IS_OS_WINDOWS;
    }
}
