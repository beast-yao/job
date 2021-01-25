package com.github.devil.srv.core.persist.core.entity;

import com.github.devil.common.enums.ExecuteType;
import com.github.devil.common.enums.TimeType;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author eric.yao
 * @date 2021/1/18
 **/
@Data
@Entity
@Table(name = "job_info")
@ToString(exclude = "instanceEntities")
public class JobInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TimeType timeType;

    @Column(nullable = false)
    private String timeVal;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ExecuteType executeType;

    @Column(unique = true,nullable = false,updatable = false)
    private String uniqueName;

    @Column(nullable = false)
    private String appName;

    @Column(nullable = false)
    private String jobMeta;

    private String serveHost;

    private String workerHost;

    @Column(insertable = false)
    private Date lastTriggerTime;

    private Date nextTriggerTime;

    private String params;

    @CreationTimestamp
    @Column(insertable = true,updatable = false)
    private Date crt;

    @UpdateTimestamp
    @Column(insertable = false,updatable = true)
    private Date upt;

    @OneToMany(fetch = FetchType.LAZY,targetEntity = InstanceEntity.class,mappedBy = "jobId")
    private List<InstanceEntity> instanceEntities;
}
