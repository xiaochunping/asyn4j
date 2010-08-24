package com.googlecode.asyn4j.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.googlecode.asyn4j.core.CacheAsynWorkHandler;
import com.googlecode.asyn4j.core.callback.AsynCallBack;

import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;
import com.googlecode.asyn4j.springbean.TestBean;
import com.googlecode.asyn4j.springbean.TestMain;

public class ServiceExcute {
	static ApplicationContext context = null;

	@Before
	public void setUp() {
		 context = new
		 FileSystemXmlApplicationContext("D:/java/asyn4j/src/main/java/applicationContext.xml");

	}

	@Test
	@Ignore
	public void testExecut2() throws InterruptedException {
		AsynService anycService = AsynServiceImpl.getService(300, 3000L, 100,
				100);
		anycService.setWorkQueueFullHandler(new CacheAsynWorkHandler(100));
		anycService.init();
		for (long i = 0; i < 1000; i++) {
			anycService.addWork(new Object[] { "panxiuyan" + i },
					TestBean.class, "myName", new MyHasResult());

			if (i % 99 == 0) {
				System.out.println(anycService.getRunStatInfo());
			}
		}

		Thread.sleep(Long.MAX_VALUE);

	}

	@Test
	@Ignore
	public void testExecut3() throws InterruptedException {
		AsynService anycService = AsynServiceImpl.getService();
		anycService.init();
		List list = new ArrayList();
		anycService.addWork(new Object[] { list }, TestBean.class, "myName",
				new MyResult());
		Thread.sleep(Long.MAX_VALUE);

	}
	@Test
	public void testExecutSpring() throws InterruptedException {
		TestMain testMain = (TestMain)context.getBean("testMain");
		testMain.maintest();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	

	public static class MyResult extends AsynCallBack {
		public void doNotify() {
			System.out.println("excute ok!");
		}
	}

	public static class MyHasResult extends AsynCallBack {

		public void doNotify() {
			if (this.methodResult != null) {
				System.out.println(methodResult.toString());
			}
		}
	}

}
