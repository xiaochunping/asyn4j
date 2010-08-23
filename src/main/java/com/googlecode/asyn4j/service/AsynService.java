package com.googlecode.asyn4j.service;

import java.util.Map;

import com.googlecode.asyn4j.core.WorkQueueFullHandler;
import com.googlecode.asyn4j.core.result.AsynResult;
import com.googlecode.asyn4j.core.work.AsynWork;

public interface AsynService {
	
	/**
	 * 添加异步工作
	 * @param params  －－ 参数
	 * @param clzss  －－ 目标类ClASS
	 * @param method  －－ 对应方法 
	 * @param anycResult --回调
	 */
	public void addWork(Object[] params,Class clzss,String method,AsynResult anycResult);
	
	/**
	 * 添加异步工作
	 * @param params  －－ 参数
	 * @param clzss  －－ 目标类对象
	 * @param method  －－ 对应方法 
	 * @param anycResult --回调
	 */
	public void addWork(Object[] params, Object tagerObject, String method,AsynResult anycResult);
	
	
	/**
	 * 添加异步工作
	 * @param params  －－ 参数
	 * @param clzss  －－ 目标类ClASS
	 * @param method  －－ 对应方法 
	 * @param anycResult --回调
	 */
	public void addWork(Object[] params,Class clzss,String method,AsynResult anycResult,int weight);
	
	
	/**
	 * 添加异步工作
	 * @param params  －－ 参数
	 * @param clzss  －－ 目标类对象
	 * @param method  －－ 对应方法 
	 * @param anycResult --回调
	 */
	public void addWork(Object[] params, Object tagerObject, String method,AsynResult anycResult, int weight);
	
	
	
	
	/**
	 * 添加异步工作依赖Spring Bean
	 * @param params  －－ 参数
	 * @param target  －－ 目标
	 * @param method  －－ 对应方法 
	 * @param anycResult --回调
	 */
	public void addWorkWithSpring(Object[] params,String target,String method,AsynResult anycResult);
	
	
	/**
	 * 添加异步工作依赖Spring Bean
	 * @param params  －－ 参数
	 * @param target  －－ 目标
	 * @param method  －－ 对应方法 
	 * @param anycResult --回调
	 * 
	 */
	public void addWorkWithSpring(Object[] params,String target,String method,AsynResult anycResult,int weight);
	
	
	
	/**
	 * add  asyn  work
	 * @param asynWork   -- asynWork entity
	 */
	public void addAsynWork(AsynWork asynWork);
	
	
	/**
	 * get stat map
	 * @return
	 */
	public Map<String,Integer> getRunStatMap();
	
	/**
	 * get stat info
	 * @return
	 */
	public String getRunStatInfo();
	
	
	public void setWorkQueueFullHandler(WorkQueueFullHandler workQueueFullHandler);
	
	
	public void init();
	
	
	
	

}
