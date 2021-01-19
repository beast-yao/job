package com.github.devil.core.ha;

import com.github.devil.core.exception.ExceptionConstants;
import com.github.devil.core.exception.JobException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author eric.yao
 * @date 2021/1/19
 **/
@Component
public class AppService {

    final static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    final static List<AppInfo> WORKERS = new Vector<>();

    final static List<AppInfo> SERVERS = new Vector<>();

    /**
     * 选择一个节点
     * @return
     */
    public AppInfo chooseWorker(){
        return chooseNext(WORKERS);
    }

    /**
     * 选择一个节点
     * @return
     */
    public AppInfo chooseServer(){
        return chooseNext(SERVERS);
    }

    /**
     * 判断节点是否存活
     * @param appInfo
     * @return
     */
    public boolean isActivate(AppInfo appInfo){
        //todo
        return true;
    }

    private AppInfo chooseNext(List<AppInfo> appInfos){
        if (appInfos == null || appInfos.isEmpty()){
            throw new JobException(ExceptionConstants.NO_ACTIVATE_AVABLE);
        }
        return appInfos.get(RANDOM.nextInt(appInfos.size()));
    }
}
