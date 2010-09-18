package com.googlecode.asyn4j.springbean;

import java.util.ArrayList;
import java.util.List;

public class TestBean {

	public void test() {
		System.out.println("test excute");
	}

	public void myName(String name) {
		try {
			if(name.equals("panxiuyan330")){
				Thread.sleep(2 * 60 * 1000);
		 	}else{
		 		Thread.sleep(4000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println(name);
	}

	public void myName(String fn, String la) {
		System.out.println(fn + "  " + la);
	}

	public String myName(String fn, String la, int a) {
		return fn + "  " + la + "  age :" + a;
	}

	public void myName(ArrayList list) {
		if (list != null) {
			System.out.println(list.size());
		}

	}

}
