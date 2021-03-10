package com.github.devil.srv.core.service;

import com.github.devil.common.dto.LogContent;
import com.github.devil.common.dto.LoggingReq;
import com.github.devil.srv.core.persist.logging.entity.LoggingEntity;
import com.github.devil.srv.core.persist.logging.repository.LoggingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
@Slf4j
@Service
public class LoggingService {

    @Resource
    private LoggingRepository loggingRepository;

    @Transactional(transactionManager = "loggingTransactionManager",rollbackFor = Exception.class)
    public void saveLogRequest( LoggingReq request){
        validRequest(request);
        loggingRepository.saveAll(request.getContents()
                .stream()
                .filter(Objects::nonNull)
                .map(this::convert).collect(Collectors.toList()));
    }

    private void validRequest(LoggingReq request){
        Set<ConstraintViolation<LoggingReq>> validateRes = Validation.buildDefaultValidatorFactory().getValidator().validate(request);
        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation<LoggingReq> validateRe : validateRes) {
            stringBuilder.append("[").append(validateRe.getPropertyPath()).append("]").append(validateRe.getMessage()).append("\t");
        }
        if (stringBuilder.length() > 0){
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private LoggingEntity convert(LogContent content){
        LoggingEntity entity = new LoggingEntity();
        BeanUtils.copyProperties(content,entity);
        return entity;
    }
}
