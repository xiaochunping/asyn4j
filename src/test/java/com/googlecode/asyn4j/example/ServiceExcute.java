package com.googlecode.asyn4j.example;

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
import com.googlecode.asyn4j.core.result.AsynResult;

import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;
import com.googlecode.asyn4j.springbean.TestBean;


public class ServiceExcute {
	static ApplicationContext context = null;

	@Before
	public void setUp() {
		//context = new FileSystemXmlApplicationContext("D:/java/asyn4j/src/main/java/applicationContext.xml");

	}

	/*@Test
	public void testExecut1() throws InterruptedException {
		AnycService anycService = (AnycService) context.getBean("anycService");
		for(int i=0;i<100;i++)
		   anycService.addService(null, "testBean", "test", new MyResult());
		Thread.sleep(20000);
	}*/

    
	@Test
	
	public void testExecut2() throws InterruptedException {
		AsynService anycService = new AsynServiceImpl();
		anycService.setWorkQueueFullHandler(new CacheAsynWorkHandler(100));
		anycService.init();
		for(long i=0;i<Integer.MAX_VALUE;i++){
			anycService.addWork(new Object[] { "panxiuyan"+i }, TestBean.class,
					"myName", new MyResult());
			
			if(i%99==0){
				System.out.println(anycService.getRunStatInfo());
			}
		}
		
		Thread.sleep(Long.MAX_VALUE);
		
	}

  
   
	@Test
	@Ignore
	public void testExecut3() throws InterruptedException {
		AsynService anycService = new AsynServiceImpl();
		anycService.addWork(new Object[] { "panxiuyan" }, TestBean.class, "myName",new MyResult());
		
	}
	
	/*@Test
	public void testExecut4() throws InterruptedException {
		AnycService anycService = (AnycService) context.getBean("anycService");
		anycService.addService(new Object[] { "panxiuyan", "tina",new Integer(12)},
				"testBean", "myName", new MyHasResult());
		Thread.sleep(2000);
	}
*/
	public static class MyResult extends AsynResult {
        public void doNotify() {
			System.out.println("excute ok!");
		}
	}
	
	public static class MyHasResult extends AsynResult {
		
      public void doNotify() {
			if(this.methodResult!=null){
		        System.out.println(methodResult.toString());		
			}
		}
	}

}
