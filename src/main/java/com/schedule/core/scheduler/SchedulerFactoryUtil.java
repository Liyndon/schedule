package com.schedule.core.scheduler;

import com.google.common.collect.Lists;
import com.schedule.util.LoggerUtil;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Created by fengwei.cfw on 2017/9/6.
 */
@Component
public class SchedulerFactoryUtil {

    @Value("${org.quartz.scheduler.quantity}")
    private Integer quantity;

    @Value("${org.quartz.scheduler.instanceName}")
    private String instanceName;

    @Value("${org.quartz.threadPool.threadCount}")
    private String threadCount;

    @Value("${org.quartz.jobStore.class}")
    private String jobStoreClass;

    @Value("${org.quartz.jobStore.driverDelegateClass}")
    private String driverDelegateClass;

    @Value("${org.quartz.jobStore.tablePrefix}")
    private String tablePrefix;

    @Value("${org.quartz.jobStore.dataSource}")
    private String dataSource;

    @Value("${org.quartz.dataSource.myDS.driver}")
    private String driver;

    @Value("${org.quartz.dataSource.myDS.maxConnections}")
    private String maxConnections;

    @Value("${spring.datasource.url}")
    private String URL;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    private static List<Scheduler> schedulers = Lists.newArrayList();

    private static Random random = new Random();

    private final static Logger LOGGER = LoggerFactory.getLogger(SchedulerFactoryUtil.class);

    @PostConstruct
    public void init() throws SchedulerException {
        Properties props = new Properties();
        props.put("org.quartz.threadPool.threadCount", threadCount);
        props.put("org.quartz.jobStore.class", jobStoreClass);
        props.put("org.quartz.jobStore.driverDelegateClass", driverDelegateClass);
        props.put("org.quartz.jobStore.tablePrefix", tablePrefix);
        props.put("org.quartz.jobStore.dataSource", dataSource);
        props.put("org.quartz.dataSource.myDS.driver", driver);
        props.put("org.quartz.dataSource.myDS.maxConnections", maxConnections);
        props.put("org.quartz.dataSource.myDS.URL", URL);
        props.put("org.quartz.dataSource.myDS.user", user);
        props.put("org.quartz.dataSource.myDS.password", password);
        for (int i = 0; i < quantity; i++) {
            props.put("org.quartz.scheduler.instanceName", instanceName + "_" + i);
            StdSchedulerFactory factory = new StdSchedulerFactory(props);
            schedulers.add(factory.getScheduler());
        }
        props.put("org.quartz.scheduler.instanceName", instanceName);
        StdSchedulerFactory factory = new StdSchedulerFactory(props);
        schedulers.add(factory.getScheduler());
    }

    public static Scheduler getRandomScheduler() throws SchedulerException {
        int index = random.nextInt(schedulers.size());
        LoggerUtil.info(LOGGER, "RandomScheduler is:{}", index);
        return schedulers.get(index);
    }

    public static List<Scheduler> getSchedulers() {
        return schedulers;
    }
}
