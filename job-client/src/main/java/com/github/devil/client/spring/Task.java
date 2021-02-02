package com.github.devil.client.spring;

import com.github.devil.common.corn.CronSequenceGenerator;
import com.github.devil.common.enums.TimeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * @author eric.yao
 * @date 2021/2/2
 **/
@Data
@AllArgsConstructor
public class Task {

    private TimeType timeType;

    private Long timeRate;

    private String cornExpression;

    private String fixedWorker;

    private String uniqueName;


    public static Task corn(String cornExpression,String fixedWorker,String uniqueName){
        new CronSequenceGenerator(cornExpression);
        return new Task(TimeType.CORN,null,cornExpression,fixedWorker,uniqueName);
    }

    public static Task delay(Long timeRate,String fixedWorker,String uniqueName){
        Assert.isTrue(timeRate > 0,"delay time needs more than zero");
    }

}
