package com.github.devil.srv.core.persist.core.entity;

import com.github.devil.common.enums.ExecuteType;
import com.github.devil.common.enums.TaskType;
import com.github.devil.common.enums.TimeType;
import com.github.devil.srv.core.enums.JobStatus;
import com.github.devil.srv.core.persist.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "job_info",indexes = {@Index(name = "idx_ji_server",columnList = "serveHost")})
@ToString(exclude = "instanceEntities",callSuper = true)
public class JobInfoEntity extends BaseEntity {

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TimeType timeType;

    @Column(nullable = false)
    private String timeVal;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ExecuteType executeType;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TaskType taskType;

    @Column(nullable = true,updatable = false)
    private String uniqueName;

    @Column(nullable = false)
    private String appName;

    @Column(nullable = true)
    private String jobMeta;

    private String serveHost;

    private String workerHost;

    private Integer version = 0;

    @Column(insertable = false)
    private Date lastTriggerTime;

    private Date nextTriggerTime;

    private String params;

    @Enumerated(value = EnumType.STRING)
    private JobStatus jobStatus;

    @OneToMany(fetch = FetchType.LAZY,targetEntity = InstanceEntity.class,mappedBy = "jobId")
    private List<InstanceEntity> instanceEntities;
}
