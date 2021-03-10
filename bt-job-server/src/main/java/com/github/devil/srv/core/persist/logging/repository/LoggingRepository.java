package com.github.devil.srv.core.persist.logging.repository;

import com.github.devil.srv.core.persist.logging.entity.LoggingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author eric.yao
 * @date 2021/1/22
 **/
@Repository
public interface LoggingRepository extends JpaRepository<LoggingEntity,Long> {

    /**
     * 删除
     * @param instanceId
     * @return
     */
    @Transactional(transactionManager = "loggingTransactionManager",rollbackFor = Exception.class)
    int deleteByInstanceIdLessThan(long instanceId);

}
