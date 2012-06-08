package com.googlecode.asyn4j.disruptor.test;

import com.googlecode.asyn4j.disruptor.Helper;

public class Test {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		DisruptorHelper.initAndStart();
		for(int i=0;i<1000;i++){
			DisruptorHelper.produce(new DeliveryReport(i));
		}
		Thread.sleep(Long.MAX_VALUE);
		Helper.shutdown();
		
		

	}

}
