package com.schedule.dao.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by fengwei.cfw on 2017/4/26.
 */
public class ScheduleJob implements Serializable {

    /**
     * 主键编号
     */
    private Integer id;

    /**
     * 任务编号
     */
    private String taskId;

    /**
     * 提交的节点
     */
    private String submitGroup;

    /**
     * 执行的节点
     */
    private String trackerGroup;

    /**
     * 透传参数
     */
    private String extParams;

    /**
     * 该任务最大的重试次数
     */
    private Integer maxRetryTimes = 3;

    /**
     * 执行表达式 和 quartz 的一样
     * 如果这个为空，表示立即执行的
     */
    private String cronExpression;

    /**
     * 触发时间
     * 如果设置了 cronExpression， 那么这个字段没用
     */
    private Long gmtTrigger = Long.valueOf(0);

    private Integer retryTimes;

    private Date gmtCreate;

    private Date gmtModify;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSubmitGroup() {
        return submitGroup;
    }

    public void setSubmitGroup(String submitGroup) {
        this.submitGroup = submitGroup;
    }

    public String getTrackerGroup() {
        return trackerGroup;
    }

    public void setTrackerGroup(String trackerGroup) {
        this.trackerGroup = trackerGroup;
    }

    public String getExtParams() {
        return extParams;
    }

    public void setExtParams(String extParams) {
        this.extParams = extParams;
    }

    public Integer getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(Integer maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Long getGmtTrigger() {
        return gmtTrigger;
    }

    public void setGmtTrigger(Long gmtTrigger) {
        this.gmtTrigger = gmtTrigger;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
