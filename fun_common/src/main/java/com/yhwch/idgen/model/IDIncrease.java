package com.yhwch.idgen.model;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class IDIncrease {
    private int step;
    private AtomicLong value = new AtomicLong(0);
    private volatile long max ;

    public long getIdle(){
        return this.getMax() - value.get();
    }
}
