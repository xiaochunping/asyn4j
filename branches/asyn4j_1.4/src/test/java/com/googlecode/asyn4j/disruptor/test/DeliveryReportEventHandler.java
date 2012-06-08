package com.googlecode.asyn4j.disruptor.test;

import com.lmax.disruptor.EventHandler;

public class DeliveryReportEventHandler implements EventHandler<DeliveryReportEvent> {
	     public void onEvent(DeliveryReportEvent event, long sequence,
	             boolean endOfBatch) throws Exception {
	         System.out.println(event.getDeliveryReport().getMessageId());
	     }
	 }