package com.schedule.dao;

import com.google.common.collect.Maps;
import com.schedule.dao.domain.ScheduleJob;
import com.schedule.dao.domain.ScheduleJobHistory;
import com.schedule.dao.mapper.ScheduleJobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by fengwei.cfw on 2017/4/26.
 */
@Service
public class ScheduleJobDao {

    @Autowired
    private ScheduleJobMapper jobMapper;

    public Integer insert(ScheduleJob job) {
        return jobMapper.insert(job);
    }

    public ScheduleJob get(String taskId, String submitGroup) {
        Map<String, String> param = Maps.newHashMap();
        param.put("taskId", taskId);
        param.put("submitGroup", submitGroup);
        return jobMapper.get(param);
    }

    public ScheduleJob get(Integer id) {
        return jobMapper.getById(id);
    }

    public List<ScheduleJob> getUndoneTriggerJob(Integer delaySecond) {
        return jobMapper.getUndoneTriggerJob(delaySecond);
    }

    public Integer delete(String taskId) {
        return jobMapper.delete(taskId);
    }

    public Integer update(ScheduleJob job) {
        return jobMapper.update(job);
    }

    public Integer deleteJob() {
        return jobMapper.deleteJob();
    }

    public Integer insertHistory(ScheduleJobHistory history) {
        return jobMapper.insertHistory(history);
    }

    public Integer count(String taskId) {
        return jobMapper.countHistory(taskId);
    }
}
