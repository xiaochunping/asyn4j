package com.googlecode.asyn4j.springbean;

import com.googlecode.asyn4j.service.AsynService;

public class TestMain {
	
	public AsynService asynService;

	public void setAsynService(AsynService asynService) {
		this.asynService = asynService;
	}
	
	public void maintest(){
		for(int i=0;i<10000;i++){
			asynService.addWorkWithSpring(new Object[] { "panxiuyan" + i }, "testBean", "myName");
		}
	}

}
