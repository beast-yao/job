package com.github.devil.srv.core.persist.core.repository;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Repository
public interface JobInfoRepository extends JpaRepository<JobInfoEntity,Long> {

    /**
     * 修改下次执行时间和上次执行时间
     * @param id
     * @param next
     * @param pre
     * @param serveHost
     */
    @Modifying
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    @Query("update JobInfoEntity set lastTriggerTime=?3 , nextTriggerTime=?2 ,version=version+1,serveHost=?4 where id=?1")
    void updateNextAndPreTriggerTimeAndServerById(Long id, Date next, Date pre,String serveHost);

    /**
     * 查询需要执行的任务
     * @param serveHost
     * @param time
     * @return
     */
    @Query(value = "select * from job_info where serve_host=?1 and next_trigger_time < ?2 and time_type != 'DELAY' and job_status = 'NORMAL' and id not in (select job_id from job_instance where job_instance.version=job_info.version)" +
            " union " +
            "select * from job_info where serve_host=?1 and next_trigger_time < ?2 and time_type = 'DELAY' and job_status = 'NORMAL' and id not in (select job_id from job_instance where job_instance.execute_statue in (?3))",nativeQuery = true)
    List<JobInfoEntity> findUnExecuteJob(String serveHost, Date time,List<String> unCompleteStatus);
}
