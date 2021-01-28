package com.github.devil.srv.core.persist.core.repository;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
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
public interface JobInstanceRepository extends JpaRepository<InstanceEntity,Long> {

    /**
     * 修改触发时间和任务状态
     * @param triggerTime
     * @param executeStatue
     * @param id
     * @return
     */
    @Modifying
    @Query("update InstanceEntity set triggerTime = ?1 , executeStatue = ?2 , upt = ?4 where id = ?3")
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    int updateTriggerTimeAndStatus(Date triggerTime, ExecuteStatue executeStatue,Long id,Date upt);

    /**
     * 通过serverhost和executeStatue查询
     * @param serveHost
     * @param executeStatue
     * @return
     */
    List<InstanceEntity> findByServeHostAndExecuteStatueIn(String serveHost,List<ExecuteStatue> executeStatue);
}
