package com.github.devil;

import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import com.github.devil.srv.core.persist.core.repository.JobInfoRepository;
import com.github.devil.srv.core.persist.core.repository.JobInstanceRepository;
import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.common.enums.ExecuteType;
import com.github.devil.common.enums.TimeType;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
public class JpaTest extends BaseTest {

    @Resource
    private JobInfoRepository jobInfoRepository;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private JobInstanceRepository jobInstanceRepository;

    @Test
    public void testSaveJob(){
        JobInfoEntity entity = new JobInfoEntity();
        entity.setAppName("test-app");
        entity.setExecuteType(ExecuteType.BROADCAST);
        entity.setTimeType(TimeType.DELAY);
        entity.setTimeVal("1000");
        entity.setUniqueName("test-app");

        try {
            JobInfoEntity db = jobInfoRepository.save(entity);
            System.out.println(db);
        }catch (DataIntegrityViolationException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveInstance(){
        InstanceEntity entity = new InstanceEntity();
        entity.setAppName("test-app");
        entity.setExecuteType(ExecuteType.BROADCAST);
        entity.setTimeType(TimeType.DELAY);
        entity.setTimeVal("1000");
        entity.setUniqueName("test-app");
        entity.setJobId(1L);
        entity.setExecuteStartTime(new Date());
        entity.setExecuteEndTime(new Date());
        entity.setExecuteStatue(ExecuteStatue.SUCCESS);

        jobInstanceRepository.save(entity);
        entity.setId(null);
        jobInstanceRepository.save(entity);
        entity.setId(null);
        jobInstanceRepository.save(entity);
        entity.setId(null);
        jobInstanceRepository.save(entity);
        entity.setId(null);
        jobInstanceRepository.save(entity);
        entity.setId(null);
        jobInstanceRepository.save(entity);
    }

    @Test
    public void testQuery() {
        transactionTemplate.execute(status -> {
            List<JobInfoEntity> tmp = jobInfoRepository.findAll();

            System.out.println("=========================================");

            List<InstanceEntity> instanceEntities = tmp.get(0).getInstanceEntities();

            System.out.println(instanceEntities);
            return instanceEntities;
        });

    }
}
