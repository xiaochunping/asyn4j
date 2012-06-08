package com.googlecode.asyn4j.disruptor;

import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.lmax.disruptor.EventHandler;

public class AsynEventHandler implements EventHandler<AsynEvent> {
	     public void onEvent(AsynEvent event, long sequence,
	             boolean endOfBatch) throws Exception {
	    	 AsynCallBack result = null;
	         try {
	             //asyn work execute
	             result = event.getWork().call();
	             
	             if (result != null) {//execute callback
	             	result.run();
	             }
	             
	         } catch (Throwable throwable ) {
	             //if execute asyn work is error,errorAsynWorkHandler disposal
	             //if (applicationContext.getErrorAsynWorkHandler() != null) {
	             	//applicationContext.getErrorAsynWorkHandler().addErrorWork(asynWork,throwable);
	            // }
	        }
	     }
	 }