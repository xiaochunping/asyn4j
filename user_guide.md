**1.3更新
  1. 优化代码
  1. 新增任务持久与恢复功能
  1. 任务调用和回调做为一个整体**




## 例子: ##
### 1.调用普通方法 ###
主方法
```
	public static void main(String[] args) {
		// 初始化异步工作服务
		AsynService anycService = AsynServiceImpl.getService(300, 3000L, 100, 100,1000);
                //异步工作缓冲处理器
                anycService.setWorkQueueFullHandler(new CacheAsynWorkHandler(100));
                //服务启动和关闭处理器
                anycService.setServiceHandler(new FileAsynServiceHandler());
                //异步工作执行异常处理器
                anycService.setErrorAsynWorkHandler(new DefaultErrorAsynWorkHandler());
		// 启动服务
		asynService.init();
		// 异步回调对象
		AsynCallBack back = new TargetBack();
		for (int i = 0; i < 1000; i++) {
			// 添加加异步工作- TargetService 的 test 方法 ，方法参数 asynej+ i
			asynService.addWork(TargetService.class, "test", new Object[] { "asyn4j" + i },new TargetBack());
                        //实例化目标对象再调用
                       // TargetService targetService = new TargetService ();
                       //asynService.addWork(
		//		targetService , "test",new Object[] { "asyn4j" + i }, new TargetBack());
                       
		}
        }

```

回调方法
```
//回调需继承AsynCallBack抽象类
public class TargetBack extends AsynCallBack {

	@Override
	public void doNotify() {
		//输出异步方法调用结果
		System.out.println(this.methodResult);

	}

}
```

目标对象
```
//异步调用对象
public class TargetService {
	public String test(String name){
		System.out.println(name +" test is execute!");
		return name;
	}

}
```

### 2.调用Spring Bean的异步方法 ###
调用 Spring testBean 的 myName 方法
```
   applicationContext.xml 加入
    <bean id="springBeanUtil" class="com.googlecode.asyn4j.spring.AsynSpringUtil">
	</bean>

  <bean id="asynService" class="com.googlecode.asyn4j.spring.AsynServiceFactoryBean">
	    <!--设置自定义相关参数-->
	    <property name="maxCacheWork" value="100"></property>
		<property name="addWorkWaitTime" value="2000"></property>
		<property name="workThreadNum" value="3"></property>
		<property name="callbackThreadNum" value="2"></property>
                 <property name="closeServiceWaitTime" value="2000"></property>
		<!--添加相关处理器-->
		<property name="errorAsynWorkHandler">
			<bean class="com.googlecode.asyn4j.core.handler.DefaultErrorAsynWorkHandler"/>
	    </property>
		<property name="workQueueFullHandler">
			<bean class="com.googlecode.asyn4j.core.handler.CacheAsynWorkHandler"/>
	   </property>
<property name="asynServiceHandler">
			<bean class="com.googlecode.asyn4j.core.handler.FileAsynServiceHandler"/>
	   </property>
   </bean>


  public class TestMain {
	
	public AsynService asynService;

	public void setAsynService(AsynService asynService) {
		this.asynService = asynService;
	}
	
	public void maintest(){
		for(int i=0;i<10000;i++){
			asynService.addWork("testBean", "myName",new Object[] { "panxiuyan" + i });
		}
	}

}
```
### 3.相关处理器 ###



#### 3.1异步工作缓冲器--(当工作队列工作数超过maxCacheWork时由处里器处理) ####
```
AsynService anycService = AsynServiceImpl.getService(300, 3000L, 100,
				100);
		anycService.setWorkQueueFullHandler(new CacheAsynWorkHandler(100));
		anycService.init();

```

> 当工作队列中的工作超过300个时，异步工作将由CacheAsynWorkHandler处理;

> 自定义处理器 继承 WorkQueueFullHandler 抽象类

> #### 3.2服务启动和关闭处理器 --(当服务启动和关闭调用) ####
```
      anycService.setCloseHander(new FileAsynServiceHandler("c:/asyn4j.data"));
```

> 设置c:/asyn4j.data为持久化文件
> FileAsynServiceHandler 是 AsynServiceHandler  的一个例子将任务持久化到文件，当系统启动时加载文件内容到内存，关闭时将未执行的任务持久化到文件。大家可以参考源码将任务持久化到别外的地方（memcached）


> 自定义处理器 继承  AsynServiceHandler  抽象类

> #### 3.3异步工作执行异常处理器 --(当工作执行出现异常时处理器) ####
```
    anycService.setErrorAsynWorkHandler(new DefaultErrorAsynWorkHandler());
```

> 自定义处理器 继承 ErrorAsynWorkHandler 抽象类

> #### 3.4所有处理器为可选配置，建议根据自己的业务继承相关的类实现自己的处理器 ####


### 4.异步工作优级 ###
> 分成三个等级WorkWeight.LOW,WorkWeight.MIDDLE, WorkWeight.HIGH
> 默认优先级为WorkWeight.MIDDLE 。

## API说明 ##
### 一.默认构造函数 ###
```
   AsynServiceImpl.getService();
```
**采用的默认参数为,
  1. (maxCacheWork)最大工作队列缓存工作数 – 300(默认值)
  1. (addWorkWaitTime)当工作队列满时添加工作等待时间-- Long.MAX\_VALUE(默认值)
  1. (workThreadNum)异步工作执行线程池大小  ---- CPU核数/2 +1(默认值)
  1. (callBackThreadNum)回调执行线程池大小  --- CPU核数/2(默认值)
  1. (closeServiceWaitTime) 服务关闭等待时间  ---- 60000s(默认值)**


### 二.自定义参数构造函数,参数顺序对应前面的说明 ###
```
 AsynServiceImpl.getService (1000, 1000L, 3, 2,60 * 1000);
```

AsynServiceImpl 是线程安全的，可以初始化一个实例，所有程序再引用.

### 三.设置缓存工作队列处理器（在init方法调用前设置） ###
```
 public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler);
```

系统有一个默认的处理器  CacheAsynWorkHandler 建议实现自己的处理器,需实现 addAsynWork，process 方法 。process建议启动一个守护线程监听.


### 四.启动服务 ###
```
 public void init(); 
```

### 五．添加异步工作API ###
```
 /**
     * 添加异步工作
     * @param tagerObject -- 目标对象（可以是 Class,Object,String(spring)）
     * @param method  -- 目标方法
     */
    public void addWork(Object tagerObject, String method);

    /**
     * 添加异步工作
     * @param tagerObject －－ 目标对象（可以是 Class,Object,String(spring)）
     * @param method －－ 目标方法
     * @param params -- 目标方法参数
     */
    public void addWork(Object tagerObject, String method,Object[] params);

    /**
     * 添加异步工作
     * 
     * 
     * @param tagerObject －－ 目标对象（可以是 Class,Object,String(spring)）
     * @param method －－ 目标方法
     * @param asynCallBack --回调对象
     * @param params －－ 目标方法参数
     */
    public void addWork(Object tagerObject, String method,Object[] params, AsynCallBack asynCallBack);

    
    /**
     * 添加异步工作
     * 
     * 
     * @param tagerObject －－ 目标对象（可以是 Class,Object,String(spring)）
     * @param method －－ 目标方法
     * @param asynCallBack --回调对象
     * @param params －－ 目标方法参数
     * @param weight －－ 工作权重
     
     */
    public void addWork(Object tagerObject, String method, Object[] params,AsynCallBack asynCallBack, WorkWeight weight);
    
    
    /**
     * 添加异步工作
     * @param tagerObject －－ 目标对象（可以是 Class,Object,String(spring)）
     * @param method －－ 目标方法
     * @param asynCallBack --回调对象
     * @param params －－ 目标方法参数
     * @param weight －－ 工作权重
     * @param cache －－  如果目标对象为class，实例化后是否缓存
     */
    public void addWork(Object tagerObject, String method, Object[] params, AsynCallBack asynCallBack, WorkWeight weight,
            boolean cache);


    /**
     *  Spring 添加异步工作
     * 
     * @param target －－ 目标对象BeanName
     * @param method －－ 目标方法
     * @param asynCallBack --回调对象
     * @param params －－ 目标方法参数
     * @param weight －－ 工作权重
     */
    public void addWorkWithSpring( String target, String method,Object[] params, AsynCallBack asynCallBack, WorkWeight weight);

    /**
     * 添加异步工作 
     * 
     * @param asynWork －－ 异步工作实例
     */
    public void addAsynWork(AsynWork asynWork);
	
```
### 六.获取运行状态信息 ###
```
/**
	 * 
	 * 获取运行状态MAP
	 * @return
	 */
	public Map<String,Integer> getRunStatMap();

   Map key说明
   total:累计接收异步工作数
   execute:执行异步工作数
   callback:执行回调数
```
```
	/**
	 *  获取运行状态字符信息
	 * @return
	 */
	public String getRunStatInfo();

```

### 七. 调用基于Spring Bean的异步方法 ###

1.applicationContext.xml 加入
```
<bean id="springBeanUtil" class="com.googlecode.asyn4j.spring.AsynSpringUtil">
	</bean>
```


使用下列方法添加异步工作
```
   asynService.addWork("testBean", "myName",new Object[] { "asyn4j" + i });

```


### 八．创建基于Spring 的依赖Bean ###

asynService spring bean 工厂
```
   <bean id="asynService" class="com.googlecode.asyn4j.spring.AsynServiceFactoryBean">
	    <!--设置自定义相关参数-->
	    <property name="maxCacheWork" value="100"></property>
		<property name="addWorkWaitTime" value="2000"></property>
		<property name="workThreadNum" value="3"></property>
		<property name="callbackThreadNum" value="2"></property>
		<!--添加相关处理器-->
		<property name="errorAsynWorkHandler">
			<bean class="com.googlecode.asyn4j.core.handler.DefaultErrorAsynWorkHandler"/>
	    </property>
		<property name="workQueueFullHandler">
			<bean class="com.googlecode.asyn4j.core.handler.CacheAsynWorkHandler"/>
	   </property>
   </bean>
```


### 九.关闭服务 ###

```
   /**
     * 关闭服务等待 waitTime 秒
     * @ wait time
     */
    public void close(long waitTime);
    
    /**
     * 关闭服务等待1分钟
     */
    public void close();
```

### 十.注意 ###
查找异步工作目标方法时,无法区分方法同名的并且参数是继承关系的方法.

