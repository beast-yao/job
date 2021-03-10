package com.github.devil.common.dto;

import com.github.devil.common.serialization.JobSerializable;
import lombok.Data;

import java.util.Set;

/**
 * @author eric.yao
 * @date 2021/2/20
 **/
@Data
public class ServicesRes  implements JobSerializable {

    private Set<String> services;

    private Long time;

}
