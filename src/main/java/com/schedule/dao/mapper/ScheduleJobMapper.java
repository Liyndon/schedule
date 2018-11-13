package com.schedule.dao.mapper;

import com.schedule.dao.domain.ScheduleJob;
import com.schedule.dao.domain.ScheduleJobHistory;

import java.util.List;
import java.util.Map;

/**
 * Created by fengwei.cfw on 2017/4/26.
 */
public interface ScheduleJobMapper {

    Integer insert(ScheduleJob job);

    ScheduleJob get(Map<String, String> param);

    ScheduleJob getById(Integer id);

    List<ScheduleJob> getUndoneTriggerJob(Integer delaySecond);

    Integer delete(String taskId);

    Integer update(ScheduleJob job);

    Integer deleteJob();

    Integer insertHistory(ScheduleJobHistory history);

    Integer countHistory(String taskId);
}
