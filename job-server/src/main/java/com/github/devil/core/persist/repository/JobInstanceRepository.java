package com.github.devil.core.persist.repository;

import com.github.devil.core.persist.entity.InstanceEntity;
import com.github.devil.core.persist.entity.JobInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Repository
public interface JobInstanceRepository extends JpaRepository<InstanceEntity,Long> {
}
