/**
 * DataFromServer.java
 * @author Jason Lu
 * @date 2014-2-27
 * @version 1.0
 */
package com.linkloving.rtring_new.http.data;

import java.io.Serializable;

/**
 * @author Jason
 * 
 */
public class DataFromClientNew implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3905937108823456695L;

	
	private Object data;

	private boolean doInput = true;

	private int actionId = -9999;

	private int jobDispatchId = -9999;

	private String token;

	public boolean isDoInput()
	{
		return doInput;
	}

	public void setDoInput(boolean doInput)
	{
		this.doInput = doInput;
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public int getActionId()
	{
		return actionId;
	}

	public void setActionId(int actionId)
	{
		this.actionId = actionId;
	}

	public int getJobDispatchId()
	{
		return jobDispatchId;
	}

	public void setJobDispatchId(int jobDispatchId)
	{
		this.jobDispatchId = jobDispatchId;
	}

}
