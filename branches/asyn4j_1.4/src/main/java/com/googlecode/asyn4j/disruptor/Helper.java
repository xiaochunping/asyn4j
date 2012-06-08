package com.googlecode.asyn4j.disruptor;

import com.googlecode.asyn4j.core.work.AsynWork;
import com.googlecode.asyn4j.disruptor.test.DeliveryReport;
import com.googlecode.asyn4j.disruptor.test.DeliveryReportEvent;
import com.googlecode.asyn4j.disruptor.test.DisruptorHelper;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

public class Helper {
	/**
	 * ringbuffer容量，最好是2的N次方
	 */
	private static final int BUFFER_SIZE = 1024 * 8;
	private RingBuffer<AsynEvent> ringBuffer;
	private SequenceBarrier sequenceBarrier;
	private AsynEventHandler handler;
	private BatchEventProcessor<AsynEvent> batchEventProcessor;
	private static Helper instance;
	private static boolean inited = false;

	private Helper() {
		ringBuffer = new RingBuffer<AsynEvent>(
				AsynEvent.EVENT_FACTORY,
				new SingleThreadedClaimStrategy(BUFFER_SIZE),
				new YieldingWaitStrategy());
		sequenceBarrier = ringBuffer.newBarrier();
		handler = new AsynEventHandler();
		batchEventProcessor = new BatchEventProcessor<AsynEvent>(
				ringBuffer, sequenceBarrier, handler);
		ringBuffer.setGatingSequences(batchEventProcessor.getSequence());
	}

	public static void initAndStart() {
		instance = new Helper();
		new Thread(instance.batchEventProcessor).start();
		inited = true;
	}

	public static void shutdown() {
		if (!inited) {
			throw new RuntimeException("Disruptor还没有初始化！");
		}
		instance.shutdown0();
	}

	private void shutdown0() {
		batchEventProcessor.halt();
	}

	private void produce0(AsynWork work) {
		// 获取下一个序列号
		long sequence = ringBuffer.next();
		// 将状态报告存入ringBuffer的该序列号中
		ringBuffer.get(sequence).setWork(work);
		// 通知消费者该资源可以消费
		ringBuffer.publish(sequence);
	}

	/**
	 * 将状态报告放入资源队列，等待处理
	 * 
	 * @param deliveryReport
	 */
	public static void produce(AsynWork work) {
		if (!inited) {
			throw new RuntimeException("Disruptor还没有初始化！");
		}
		instance.produce0(work);
	}
}