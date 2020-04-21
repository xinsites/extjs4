package com.xinsite.core.utils.web.domain.server;


import com.xinsite.common.uitls.lang.DateUtils;
import com.xinsite.common.uitls.lang.NumberUtils;
import com.xinsite.common.uitls.lang.TimeUtils;

import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * JVM相关信息
 */
public class Jvm {
    /**
     * 当前JVM占用的内存总数(M)
     */
    private double total;

    /**
     * JVM最大可用内存总数(M)
     */
    private double max;

    /**
     * JVM空闲内存(M)
     */
    private double free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    public double getTotal() {
        return NumberUtils.div(total, (1024 * 1024), 2);
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getMax() {
        return NumberUtils.div(max, (1024 * 1024), 2);
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getFree() {
        return NumberUtils.div(free, (1024 * 1024), 2);
    }

    public void setFree(double free) {
        this.free = free;
    }

    public double getUsed() {
        return NumberUtils.div(total - free, (1024 * 1024), 2);
    }

    public double getUsage() {
        return NumberUtils.mul(NumberUtils.div(total - free, total, 4), 100);
    }

    /**
     * 获取JDK名称
     */
    public String getName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    /**
     * JDK启动时间
     */
    public String getStartTime() {
        return DateUtils.formatDateTime(DateUtils.getServerStartDate());
    }

    /**
     * JDK运行时间
     */
    public String getRunTime() {
        return TimeUtils.getDatePoor(new Date(), DateUtils.getServerStartDate());
    }
}
