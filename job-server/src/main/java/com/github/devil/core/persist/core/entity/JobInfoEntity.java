package com.github.devil.core.persist.core.entity;

import com.github.devil.enums.ExecuteType;
import com.github.devil.enums.TimeType;
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

    @Enumerated(value = EnumType.STRING)
    private TimeType timeType;

    private String timeVal;

    @Enumerated(value = EnumType.STRING)
    private ExecuteType executeType;

    @Column(unique = true,nullable = false,updatable = false)
    private String uniqueName;

    private String appName;

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
