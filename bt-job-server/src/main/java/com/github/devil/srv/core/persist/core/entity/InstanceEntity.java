package com.github.devil.srv.core.persist.core.entity;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.common.enums.ExecuteType;
import com.github.devil.common.enums.TaskType;
import com.github.devil.common.enums.TimeType;
import com.github.devil.srv.core.enums.InstanceType;
import com.github.devil.srv.core.persist.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "job_instance",indexes = {
        @Index(name = "idx_ins_job_version",columnList = "jobId,version"),
        @Index(name = "idx_ins_app",columnList = "appName")
})
@ToString(exclude = "jobInfoEntity",callSuper = true)
public class InstanceEntity extends BaseEntity {

    @Column
    private String des;

    @Column(nullable = false,updatable = false)
    private Long jobId;

    @Column(updatable = false,nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TimeType timeType;

    @Column(updatable = false,nullable = false)
    private String timeVal;

    @Column(nullable = false,updatable = false)
    @Enumerated(value = EnumType.STRING)
    private ExecuteType executeType;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TaskType taskType;

    @Column(updatable = false)
    private String uniqueName;

    @Column(updatable = false)
    private String params;

    @Column(updatable = false,nullable = false)
    private String appName;

    @Column(updatable = false)
    private String jobMeta;

    @Column(updatable = false,nullable = false)
    private String serveHost;

    @Column(updatable = false)
    private String workerHost;

    @Column(updatable = false)
    @Enumerated(value = EnumType.STRING)
    private InstanceType instanceType;

    @Enumerated(value = EnumType.STRING)
    private ExecuteStatue executeStatue;

    private Integer version;

    @Column(updatable = false)
    private Date exceptTriggerTime;

    private Date triggerTime;

    private Date executeEndTime;

    @JoinColumn(name = "jobId",referencedColumnName = "id",updatable = false,insertable = false)
    @ManyToOne(targetEntity = JobInfoEntity.class,fetch = FetchType.LAZY)
    private JobInfoEntity jobInfoEntity;
}
