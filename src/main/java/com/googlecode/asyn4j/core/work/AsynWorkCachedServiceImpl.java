package com.googlecode.asyn4j.core.work;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.asyn4j.core.AsynWorkExecute;
import com.googlecode.asyn4j.core.handler.WorkQueueFullHandler;

/**
 * @author pan_java
 */
public class AsynWorkCachedServiceImpl implements AsynWorkCachedService {

    private static final Log          log             = LogFactory.getLog(AsynWorkCachedServiceImpl.class);

    // work queue
    protected BlockingQueue<AsynWork> workQueue       = null;

    private long                      addWorkWaitTime = 0;

    private int                       maxCacheWork    = 0;

    // total asyn work total
    private static int                totalWork       = 0;

    private WorkQueueFullHandler      workQueueFullHandler;

    public AsynWorkCachedServiceImpl() {
        workQueue = new PriorityBlockingQueue<AsynWork>();
    }

    public AsynWorkCachedServiceImpl(int maxCacheWork, long addWorkWaitTime, WorkQueueFullHandler workQueueFullHandler) {
        this.addWorkWaitTime = addWorkWaitTime;
        this.maxCacheWork = maxCacheWork;
        this.workQueueFullHandler = workQueueFullHandler;
        workQueue = new PriorityBlockingQueue<AsynWork>();
    }

    public AsynWorkCachedServiceImpl(int maxCacheWork, long addWorkWaitTime, WorkQueueFullHandler workQueueFullHandler,
                                     BlockingQueue<AsynWork> workQueue) {
        if (workQueue == null) {
            throw new IllegalArgumentException("workQueue is null");
        }
        this.workQueue = workQueue;
        this.addWorkWaitTime = addWorkWaitTime;
        this.maxCacheWork = maxCacheWork;
    }

    @Override
    public void addWork(AsynWork anycWork) {
        try {
            if (totalWork - AsynWorkExecute.getExecuteWork() > maxCacheWork) {
                if (workQueueFullHandler != null) {
                    log.warn("asyn work add to cache queue");
                    workQueueFullHandler.addAsynWork(anycWork);
                } else {
                    log.warn("work queue is full");
                }
                return;
            }
            // if work queue full,wait time
            boolean addFlag = workQueue.offer(anycWork, addWorkWaitTime, TimeUnit.MILLISECONDS);
            if (!addFlag) {
                log.warn("work add fail");
                workQueueFullHandler.addAsynWork(anycWork);
            } else {
                ++totalWork;
            }
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    public AsynWork getWork() {
        try {
            return workQueue.take();
        } catch (InterruptedException e) {
            log.error(e);
            throw new RuntimeException();
        }
    }

    public int getTotalWork() {
        return totalWork;
    }

}
