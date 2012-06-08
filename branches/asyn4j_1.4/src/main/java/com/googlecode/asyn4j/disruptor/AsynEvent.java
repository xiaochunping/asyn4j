package com.googlecode.asyn4j.disruptor;

import com.googlecode.asyn4j.core.work.AsynWork;
import com.lmax.disruptor.EventFactory;

public class AsynEvent {
    private AsynWork work;

    public AsynWork getWork() {
		return work;
	}

    public void setWork(AsynWork work) {
		this.work = work;
	}

    public final static EventFactory<AsynEvent> EVENT_FACTORY = new EventFactory<AsynEvent>() {
    	
        public AsynEvent newInstance() {
            return new AsynEvent();
        }
    };
}
