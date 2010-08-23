package com.googlecode.asyn4j.core.work;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.googlecode.asyn4j.core.callback.AsynCallBack;
import com.googlecode.asyn4j.util.AsynSpringUtil;
import com.googlecode.asyn4j.util.MethodUtil;

public class AsynWorkEntity implements AsynWork, Comparable<AsynWorkEntity> {

	private Object target;

	private String method;

	private Object[] params;

	private AsynCallBack anycResult;

	private int weight = 10;

	// method Cache Map
	private final static Map<String, Method> methodCacheMap = new ConcurrentHashMap<String, Method>();

	public AsynWorkEntity(Object target, String method, Object[] params,
			AsynCallBack anycResult) {
		this.target = target;
		this.method = method;
		this.params = params;
		this.anycResult = anycResult;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public AsynCallBack call() throws Exception {

		if (target == null)
			throw new RuntimeException("target object is null");

		Class clazz = target.getClass();

		String methodKey = MethodUtil.getClassMethodKey(clazz, params, method);

		Method targetMethod = methodCacheMap.get(methodKey);

		if (targetMethod == null) {
			targetMethod = MethodUtil.getTargetMethod(clazz, params, method);
			if (targetMethod != null) {
				methodCacheMap.put(methodKey, targetMethod);
			}
		}
		
		if(targetMethod==null){
			throw new IllegalArgumentException("target method is null");
		}

		Object result = targetMethod.invoke(target, params);
		if(anycResult!=null){//if call back is not null
			anycResult.setInokeResult(result);
		}
		return anycResult;

	}
	
	@Override
	public AsynCallBack getAnycResult() {
		return anycResult;
	}

	@Override
	public int compareTo(AsynWorkEntity o) {
		return o.getWeight() - this.weight;
	}

	@Override
	public String getThreadName() {
		String className =this.target.getClass().getSimpleName();
		StringBuilder sb = new StringBuilder();
		sb.append(className).append("/").append(this.method).append("/");
		if(this.params!=null){
			sb.append(params.length);
		}else{
			sb.append(0);
		}
		return sb.toString();
	}
	
	

}
