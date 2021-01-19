package com.github.devil.core.persist.repository;

import com.github.devil.core.persist.entity.JobInfoEntity;
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

    @Modifying
    @Transactional(transactionManager = "transactionManager")
    @Query("update JobInfoEntity set lastTriggerTime=?3 , nextTriggerTime=?2 where id=?1")
    int updateNextAndPreTriggerTimeById(Long id, Date next, Date pre);
}
