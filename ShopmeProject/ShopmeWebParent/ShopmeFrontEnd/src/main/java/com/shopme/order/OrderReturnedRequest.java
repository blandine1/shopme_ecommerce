package com.shopme.order;

public class OrderReturnedRequest {
	
	private int orderId;
	private String reason;
	private String note;
	

	public OrderReturnedRequest() {
		
	}

	public OrderReturnedRequest(int orderId, String reason, String note) {
		this.orderId = orderId;
		this.reason = reason;
		this.note = note;
	}
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	

}
