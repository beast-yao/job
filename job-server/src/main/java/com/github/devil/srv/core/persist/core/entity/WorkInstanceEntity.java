package com.github.devil.srv.core.persist.core.entity;

import com.github.devil.common.enums.ExecuteStatue;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/1/25
 **/
@Data
@Entity
@Table(name = "job_work_instance",indexes = {@Index(columnList = "instanceId",name = "idx_jwi_instance")})
public class WorkInstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,updatable = false)
    private Long jobId;

    @Column(nullable = false,updatable = false)
    private Long instanceId;

    @Column(updatable = false)
    private String params;

    @Column(updatable = false,nullable = false)
    private String appName;

    @Column(updatable = false)
    private String jobMeta;

    @Column(updatable = false,nullable = false)
    private String serveHost;

    @Column(updatable = false,nullable = false)
    private String workerHost;

    @Enumerated(value = EnumType.STRING)
    private ExecuteStatue executeStatue;

    private Date triggerTime;

    private Date executeEndTime;

    @CreationTimestamp
    @Column(insertable = true,updatable = false)
    private Date crt;

    @UpdateTimestamp
    @Column(insertable = false,updatable = true)
    private Date upt;
}
