package com.github.devil.srv.core.persist.core.entity;

import com.github.devil.common.enums.ExecuteStatue;
import com.github.devil.common.enums.ExecuteType;
import com.github.devil.common.enums.TimeType;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Data
@Entity
@Table(name = "job_instance")
@ToString(exclude = "jobInfoEntity")
public class InstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,updatable = false)
    private Long jobId;

    @Column(updatable = false)
    @Enumerated(value = EnumType.STRING)
    private TimeType timeType;

    @Column(updatable = false)
    private String timeVal;

    @Enumerated(value = EnumType.STRING)
    private ExecuteType executeType;

    @Column(nullable = false,updatable = false)
    private String uniqueName;

    @Column(updatable = false)
    private String params;

    @Column(updatable = false)
    private String appName;

    @Column(updatable = false)
    private String jobMeta;

    @Column(updatable = false)
    private String serveHost;

    @Column(updatable = false)
    private String workerHost;

    @Enumerated(value = EnumType.STRING)
    private ExecuteStatue executeStatue;

    @Column(updatable = false)
    private Date exceptTriggerTime;

    private Date executeStartTime;

    private Date executeEndTime;

    @CreationTimestamp
    @Column(insertable = true,updatable = false)
    private Date crt;

    @UpdateTimestamp
    @Column(insertable = false,updatable = true)
    private Date upt;

    @JoinColumn(name = "jobId",referencedColumnName = "id",updatable = false,insertable = false)
    @ManyToOne(targetEntity = JobInfoEntity.class,fetch = FetchType.LAZY)
    private JobInfoEntity jobInfoEntity;
}
