package com.github.devil.srv.core.persist;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @author eric.yao
 * @date 2021/3/9
 **/
@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(insertable = true,updatable = false)
    private Date crt;

    @UpdateTimestamp
    @Column(insertable = false,updatable = true)
    private Date upt;
}
