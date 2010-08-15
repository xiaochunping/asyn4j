package com.googlecode.asyn4j.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class CachedService<T> {

	protected BlockingQueue<T> anycQueue = null;

	public CachedService(int queueLength) {
		this.anycQueue = new LinkedBlockingQueue<T>(queueLength);
	}

	public CachedService(BlockingQueue<T> anycQueue) {
		this.anycQueue = anycQueue;
	}

}
