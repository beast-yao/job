package com.github.devil.srv.core.persist.core.repository;

import com.github.devil.srv.core.persist.core.entity.InstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Repository
public interface JobInstanceRepository extends JpaRepository<InstanceEntity,Long> {
}
