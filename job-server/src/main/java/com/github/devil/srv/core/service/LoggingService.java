package com.github.devil.srv.core.service;

import com.github.devil.common.request.LoggingRequest;
import com.github.devil.srv.core.persist.logging.entity.LoggingEntity;
import com.github.devil.srv.core.persist.logging.repository.LoggingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Validation;

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
    public void saveLogRequest( LoggingRequest request){
        validRequest(request);

        LoggingEntity loggingEntity = new LoggingEntity();
        BeanUtils.copyProperties(request,loggingEntity);

        loggingRepository.save(loggingEntity);
    }

    private void validRequest(LoggingRequest request){
        Validation.buildDefaultValidatorFactory().getValidator().validate(request);
    }
}
