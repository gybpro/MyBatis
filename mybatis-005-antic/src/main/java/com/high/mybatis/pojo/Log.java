package com.high.mybatis.pojo;

/**
 * 日志相关信息类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class Log {
    private Integer id;
    private String log;
    private String time;

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", log='" + log + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Log(Integer id, String log, String time) {
        this.id = id;
        this.log = log;
        this.time = time;
    }

    public Log() {
    }
}
