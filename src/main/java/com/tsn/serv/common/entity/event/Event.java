package com.tsn.serv.common.entity.event;

import com.tsn.common.utils.utils.tools.json.JsonUtils;

public class Event {
	
	private EventEum eventEum;
	
	private String content;
	
	public EventEum getEventEum() {
		return eventEum;
	}

	public void setEventEum(EventEum eventEum) {
		this.eventEum = eventEum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Event(EventEum eventEum) {
		this.eventEum = eventEum;
	}
	
	public Event setFlowControl(String userId) {
		FlowControl control = new FlowControl(userId);
		this.content = JsonUtils.objectToJson(control);
		return this;
	}
	
	
	public class FlowControl {
		
		private String userId;
		
		public FlowControl(String userId) {
			this.userId = userId;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}
		
	}
	

}
