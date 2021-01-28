package com.github.devil.srv.core.persist.core.repository;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.srv.core.persist.core.entity.WorkInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    int mergeTriggerTimeAndExecuteStatueById(ExecuteStatue statue, Date triggerTime, Date upt, Long id);

}
