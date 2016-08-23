/**
 * UserEntity.java
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
public class UserEntity implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 8522560539287878264L;

	protected transient Observer propertyChangedObserver = null;

	private int user_id;

	private UserBase userBase;

	private SportDeviceEntity deviceEntity;

	private EntEntity entEntity;

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

	public UserBase getUserBase()
	{
		return userBase;
	}

	public void setUserBase(UserBase userBase)
	{
		this.userBase = userBase;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "UserBase()");
	}

	public SportDeviceEntity getDeviceEntity()
	{
		return deviceEntity;
	}

	public void setDeviceEntity(SportDeviceEntity deviceEntity)
	{
		this.deviceEntity = deviceEntity;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "deviceEntity()");
	}

	public EntEntity getEntEntity()
	{
		return entEntity;
	}

	public void setEntEntity(EntEntity entEntity)
	{
		this.entEntity = entEntity;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "EntEntity()");
	}

	@Override
	public String toString() {
		return "UserEntity{" +
				"propertyChangedObserver=" + propertyChangedObserver +
				", user_id=" + user_id +
				", userBase=" + userBase.toString() +
				", deviceEntity=" + deviceEntity.toString() +
				", entEntity=" + entEntity.toString() +
				'}';
	}
}
