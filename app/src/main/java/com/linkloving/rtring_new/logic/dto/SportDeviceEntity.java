/**
 * SportDeviceEntity.java
 * @author Jason Lu
 * @date 2016-3-29
 * @version 1.0
 */
package com.linkloving.rtring_new.logic.dto;

import java.io.Serializable;
import java.util.Observer;

/**
 * @author Lz
 * 
 */
public class SportDeviceEntity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8649681996753435292L;

	protected transient Observer propertyChangedObserver = null;

	private int user_id, device_type;
	
	private String belong_ent_id, card_number, last_sync_device_id, last_sync_device_id2 ,model_name;
//	private String belong_ent_id="", card_number="", last_sync_device_id="", last_sync_device_id2="";

	public void setPropertyChangedObserver(Observer propertyChangedObserver)
	{
		this.propertyChangedObserver = propertyChangedObserver;
	}

	public int getUser_id()
	{
		return user_id;
	}

	public void setUser_id(int user_id)
	{
		this.user_id = user_id;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_id");
	}

	public String getBelong_ent_id()
	{
		return belong_ent_id;
	}

	public void setBelong_ent_id(String belong_ent_id)
	{
		this.belong_ent_id = belong_ent_id;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "belong_ent_id");
	}

	public String getCard_number()
	{
		return card_number;
	}

	public void setCard_number(String card_number)
	{
		this.card_number = card_number;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "card_number");
	}

	public String getLast_sync_device_id()
	{
		return last_sync_device_id;
	}

	public void setLast_sync_device_id(String last_sync_device_id)
	{
		this.last_sync_device_id = last_sync_device_id;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "last_sync_device_id");
	}

	public String getLast_sync_device_id2()
	{
		return last_sync_device_id2;
	}

	public void setLast_sync_device_id2(String last_sync_device_id2)
	{
		this.last_sync_device_id2 = last_sync_device_id2;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "last_sync_device_id2");
	}

	public int getDevice_type() {
		return device_type;
	}

	public void setDevice_type(int device_type) {
		this.device_type = device_type;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "device_type");
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "model_name");
	}

	@Override
	public String toString() {
		return "SportDeviceEntity{" +
				"propertyChangedObserver=" + propertyChangedObserver +
				", user_id=" + user_id +
				", belong_ent_id='" + belong_ent_id + '\'' +
				", card_number='" + card_number + '\'' +
				", last_sync_device_id='" + last_sync_device_id + '\'' +
				", last_sync_device_id2='" + last_sync_device_id2 + '\'' +
				'}';
	}
}
