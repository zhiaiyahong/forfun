package com.yhwch.idgen.model;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IDBuffer {

    /**
     * 配置名称
     */
    private String key;

    /**
     * buffer池 存两个
     */
    private IDIncrease[] idIncreases;

    /**
     * 当前buffer的index
     */
    private volatile int currentPos;

    /**
     * 下一个buffer是否准备完毕
     */
    private volatile boolean nextReady;

    /**
     * 线程是否在运行中
     */
    private final AtomicBoolean threadRunning;

    private final ReadWriteLock lock;

    private volatile int step;
    private volatile int minStep;
    private volatile boolean initOk;

    public IDBuffer(){
        currentPos = 0;
        threadRunning = new AtomicBoolean(false);
        idIncreases = new IDIncrease[]{new IDIncrease(),new IDIncrease()};
        lock = new ReentrantReadWriteLock();
    }

    /**
     * 获取当前buffer
     * @return
     */
    public IDIncrease getCurrent(){
        return idIncreases[currentPos];
    }

    public IDIncrease getNext(){
        return idIncreases[nextPos()];
    }

    public int nextPos() {
        return (currentPos + 1) % 2;
    }

    public void switchPos() {
        currentPos = nextPos();
    }

    public Lock rLock() {
        return lock.readLock();
    }

    public Lock wLock() {
        return lock.writeLock();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AtomicBoolean getThreadRunning() {
        return threadRunning;
    }

    public boolean isNextReady() {
        return nextReady;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public int getMinStep() {
        return minStep;
    }

    public int getStep() {
        return step;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public void setMinStep(int minStep) {
        this.minStep = minStep;
    }

    public void setNextReady(boolean nextReady) {
        this.nextReady = nextReady;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public boolean isInitOk() {
        return initOk;
    }

    public void setInitOk(boolean initOk) {
        this.initOk = initOk;
    }

    @Override
    public String toString() {
        StringBuilder sbu = new StringBuilder();
        sbu.append("{key=").append(key)
                .append(",currentPos=").append(currentPos)
                .append(",nextReady=").append(nextReady)
                .append(",step=").append(step)
                .append(",minStep=").append(minStep)
                .append(",idIncreases=").append(Arrays.toString(idIncreases));
        return sbu.toString();
    }
}
