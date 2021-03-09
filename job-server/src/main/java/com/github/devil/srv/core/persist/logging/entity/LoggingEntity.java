package com.github.devil.srv.core.persist.logging.entity;

import com.github.devil.common.enums.LogLevel;
import com.github.devil.srv.core.persist.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/1/22
 **/
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "job_logging",indexes = {@Index(columnList = "instanceId",name = "idx_jl_instance")})
public class LoggingEntity extends BaseEntity {

    private Long workInstanceId;

    private Long jobId;

    private Long instanceId;

    private Date loggingTime;

    @Column(columnDefinition = "varchar(4000)")
    private String loggingContent;

    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;

    private String workAddress;
}
