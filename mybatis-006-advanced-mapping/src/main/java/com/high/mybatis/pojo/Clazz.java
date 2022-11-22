package com.high.mybatis.pojo;

import java.util.List;

/**
 * 班级类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class Clazz {
    private Integer id;

    private String name;

    private List<Stu> stuList;

    @Override
    public String toString() {
        return "Clazz{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stuList=" + stuList +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Stu> getStuList() {
        return stuList;
    }

    public void setStuList(List<Stu> stuList) {
        this.stuList = stuList;
    }
}
