package com.googlecode.asyn4j.core;

import java.util.LinkedList;
import java.util.Map;

import com.googlecode.asyn4j.core.work.AsynWork;

public final class CacheAsynWorkHandler extends WorkQueueFullHandler {
	
	private static  int max_length_link = Integer.MAX_VALUE;
    
	private  LinkedList<AsynWork> cacheLink = new LinkedList<AsynWork>();
	
	public CacheAsynWorkHandler(){
		this(max_length_link);
	}
	
	public CacheAsynWorkHandler(int maxLength){
		this.max_length_link = maxLength;
	}
	
	@Override
	public void addAsynWork(AsynWork asynWork) {
		if(cacheLink.size()>max_length_link)
			return;
		cacheLink.addLast(asynWork);
		this.notifyAll();
	}

	@Override
	public void process() {
		Runnable runnable = new Runnable(){
			public void run(){
				while(true){
					Map<String,Integer> runstatMap = asynService.getRunStatMap();
					if(runstatMap.get("total")-runstatMap.get("execute")>300){
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
	
						}
						continue;
					}
					if(cacheLink.size()==0){
					    try {
							this.wait();
						} catch (InterruptedException e) {
						}	
					}else{
						AsynWork asynWork = cacheLink.getLast();
						asynService.addAsynWork(asynWork);
					}
				}
			}
		};
		
		new Thread(runnable).start();
		
	}
	
	
	
	
	
	

	

}
