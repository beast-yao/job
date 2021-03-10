package com.github.devil.common.dto;

import com.github.devil.common.serialization.JobSerializable;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author eric.yao
 * @date 2021/2/3
 **/
@Data
public class LoggingReq implements JobSerializable {

    @Valid
    @NotEmpty(message = "log contents is required")
    private List<LogContent> contents;

}
