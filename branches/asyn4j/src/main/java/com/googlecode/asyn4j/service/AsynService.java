package com.googlecode.asyn4j.service;

import java.util.Map;

import com.googlecode.asyn4j.core.WorkQueueFullHandler;
import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.core.work.AsynWork;


/**
 * 
 * @author pan_java
 *
 */
public interface AsynService {
	
	/**
	 * 添加异步工作
	 * @param params  －－ 参数
	 * @param clzss  －－ 目标类ClASS
	 * @param method  －－ 对应方法 
	 * @param anycResult --回调
	 */
	public void addWork(Object[] params,Class clzss,String method,AsynCallBack asynCallBack);
	
	/**
	 * 添加异步工作
	 * @param params  －－ 参数
	 * @param clzss  －－ 目标类对象
	 * @param method  －－ 对应方法 
	 * @param asynCallBack --回调
	 */
	public void addWork(Object[] params, Object tagerObject, String method,AsynCallBack asynCallBack);
	
	
	/**
	 * 添加异步工作
	 * @param params  －－ 参数
	 * @param clzss  －－ 目标类ClASS
	 * @param method  －－ 对应方法 
	 * @param asynCallBack －－ 回调
	 */
	public void addWork(Object[] params,Class clzss,String method,AsynCallBack asynCallBack,int weight);
	
	
	/**
	 * 添加异步工作
	 * @param params  －－ 参数
	 * @param clzss  －－ 目标类对象
	 * @param method  －－ 对应方法 
	 * @param asynCallBack －－回调
	 * @param weight －－  异步工作权重
	 */
	public void addWork(Object[] params, Object tagerObject, String method,AsynCallBack asynCallBack, int weight);
	
	
	
	
	/**
	 * 添加异步工作依赖Spring Bean
	 * @param params  －－ 参数
	 * @param target  －－ 目标
	 * @param method  －－ 对应方法 
	 * @param asynCallBack －－回调
	 */
	public void addWorkWithSpring(Object[] params,String target,String method,AsynCallBack asynCallBack);
	
	
	/**
	 * 添加异步工作依赖Spring Bean
	 * @param params  －－ 参数
	 * @param target  －－ 目标
	 * @param method  －－ 对应方法 
	 * @param asynCallBack －－回调
	 * @param weight －－  异步工作权重
	 * 
	 */
	public void addWorkWithSpring(Object[] params,String target,String method,AsynCallBack asynCallBack,int weight);
	
	
	
	/**
	 *  添加异步工作
	 * @param asynWork   －－ 异步工作实体
	 */
	public void addAsynWork(AsynWork asynWork);
	
	
	/**
	 * 
	 * 获取运行状态MAP
	 * @return
	 */
	public Map<String,Integer> getRunStatMap();
	
	/**
	 *  获取运行状态字符信息
	 * @return
	 */
	public String getRunStatInfo();
	
	/**
	 * 设置缓存工作队列处理器
	 * @param workQueueFullHandler
	 */
	public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler);
	
	/**
	 * 启动服务
	 */
	public void init();
	
	
	
	

}
