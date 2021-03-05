package com.github.devil.srv.core.persist.core.repository;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.core.persist.core.entity.WorkInstanceEntity;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author eric.yao
 * @date 2021/1/25
 **/
@Repository
public interface WorkInstanceRepository extends JpaRepository<WorkInstanceEntity,Long> {

    /**
     * 更新触发时间和状态
     * @param statue
     * @param triggerTime
     * @param id
     * @return
     */
    @Modifying
    @Query("update WorkInstanceEntity set executeStatue=?1,triggerTime=?2,upt=?3 where id=?4")
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    int updateTriggerTimeAndExecuteStatueById(ExecuteStatue statue, Date triggerTime, Date upt, Long id);


    @Modifying
    @Query("update WorkInstanceEntity set executeStatue=?1,triggerTime=?2,upt=?3,executeEndTime=?3 where instanceId=?4 and executeStatue = ?5")
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    int endWorkByInstanceId(ExecuteStatue statue, Date triggerTime, Date upt, Long instanceId, ExecuteStatue before);

    @Modifying
    @Query("update WorkInstanceEntity set executeStatue=?1,triggerTime=?2,upt=?3,executeEndTime=?3 where id=?4 and executeStatue = ?5")
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    int endWorkById(ExecuteStatue statue, Date triggerTime, Long id, ExecuteStatue before);

    /**
     * 修改执行状态
     * @param id
     * @param statue
     * @return
     */
    @Modifying
    @CanIgnoreReturnValue
    @Query("update WorkInstanceEntity set executeStatue=?2,executeEndTime=?3 where id=?1 and executeStatue='EXECUTING'")
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    int endWorkFromExecuteById(Long id, ExecuteStatue statue, Date endTime);

    /**
     * 更具状态和instanceId查询
     * @param statue
     * @param instanceId
     * @return
     */
    List<WorkInstanceEntity> findByExecuteStatueInAndInstanceId(List<ExecuteStatue> statue,Long instanceId);

    @Modifying
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    @Query("update WorkInstanceEntity set executeStatue='CANCEL' where executeStatue='WAIT' and serveHost=?1")
    int cancelAllInstance(String serverHost);
}
