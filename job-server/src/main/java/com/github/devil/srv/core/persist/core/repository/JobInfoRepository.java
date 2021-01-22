package com.github.devil.srv.core.persist.core.repository;

import com.github.devil.srv.core.persist.core.entity.JobInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


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
     */
    @Modifying
    @Transactional(transactionManager = "transactionManager",rollbackFor = Exception.class)
    @Query("update JobInfoEntity set lastTriggerTime=?3 , nextTriggerTime=?2 where id=?1")
    void updateNextAndPreTriggerTimeById(Long id, Date next, Date pre);
}
