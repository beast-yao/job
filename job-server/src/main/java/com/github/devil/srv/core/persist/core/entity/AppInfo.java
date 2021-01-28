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
@Table(name = "job_app",indexes = {@Index(name = "idx_ja_name",columnList = "appName",unique = true)})
public class AppInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appName;

    private Date registerDate;

}
