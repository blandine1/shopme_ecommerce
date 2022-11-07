package com.shopme.common.entity.order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shopme.common.entity.IdBaseEntity;

@Entity
@Table(name = "order_track")
public class OrderTrack extends IdBaseEntity{

	@Column(length =256)
	private String notes;
	
	private Date updatedTime;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 45)
	private OrderStatus status;
	
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
	@Transient
	public String getUpdatedTimeOnForm() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		
		return dateFormat.format(this.updatedTime);
	}
	
	public void setUpdatedTimeOnForm(String dateString) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		
		try {
			this.updatedTime= dateFormat.parse(dateString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
