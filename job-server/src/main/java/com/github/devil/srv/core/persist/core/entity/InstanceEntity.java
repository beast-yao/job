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

    @Column(nullable = false)
    private Long jobId;

    @Enumerated(value = EnumType.STRING)
    private TimeType timeType;

    private String timeVal;

    @Enumerated(value = EnumType.STRING)
    private ExecuteType executeType;

    @Column(nullable = false,updatable = false)
    private String uniqueName;

    private String params;

    private String appName;

    private String jobMeta;

    private String serveHost;

    private String workerHost;

    @Enumerated(value = EnumType.STRING)
    private ExecuteStatue executeStatue;

    private Date exceptTriggerTime;

    @Column(nullable = false)
    private Date executeStartTime;

    @Column(nullable = false)
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
