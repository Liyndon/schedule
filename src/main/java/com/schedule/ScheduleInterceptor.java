package com.schedule;

import com.schedule.dao.domain.CommonResult;
import com.schedule.util.LoggerUtil;
import com.schedule.util.PreconditonException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by fengwei.cfw on 2017/4/26.
 */
@Aspect
@Component
public class ScheduleInterceptor {

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleInterceptor.class);

    @Around("execution(public * com.schedule.core..*(..))")
    public Object process(ProceedingJoinPoint point) throws Throwable {
        try {
            return point.proceed();
        } catch (PreconditonException e) {
            LoggerUtil.error(LOGGER, "schedule处理异常:{}", e.getMessage());
            return CommonResult.fail("参数校验异常:" + e.getMessage());
        } catch (Throwable e) {
            LoggerUtil.error(LOGGER, e, "schedule处理异常");
            return CommonResult.fail("系统内部异常" + e.getMessage());
        }
    }
}
