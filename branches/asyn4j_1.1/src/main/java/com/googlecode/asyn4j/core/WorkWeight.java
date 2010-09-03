/**
 * Project: asyn4j
 * 
 * File Created at 2010-9-3
 * $Id$
 * 
 * Copyright 2008 Alibaba.com Croporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.googlecode.asyn4j.core;

/**
 * TODO Comment of WorkWeight
 * 
 * @author pan_java
 * @version WorkWeight.java 2010-9-3 下午06:19:21
 */
public enum WorkWeight {

    LOW(1),
    MIDDLE(5),
    HIGH(9);

    private int value;

    WorkWeight(int value) {
        this.value = value;
    }
    
    public int getValue(){
        return value;
    }

}
