package com.alibaba.markovdemo.entity;

import java.io.Serializable;
public abstract class BaseEntity implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private Long id;

    public BaseEntity() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
