package com.github.devil.srv.core.persist.core.repository;

import com.github.devil.srv.core.persist.core.entity.WorkInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author eric.yao
 * @date 2021/1/25
 **/
@Repository
public interface WorkInstanceRepository extends JpaRepository<WorkInstanceEntity,Long> {
}
