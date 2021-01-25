package com.github.devil.srv.core.persist.core.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/1/25
 **/
@Data
@Entity
@Table(name = "job_app")
public class AppInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String appName;

    private Date registerDate;

}
