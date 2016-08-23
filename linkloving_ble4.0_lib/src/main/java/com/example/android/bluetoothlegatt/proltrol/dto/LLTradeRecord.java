package com.example.android.bluetoothlegatt.proltrol.dto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class LLTradeRecord 
{
	/**是否有效*/
	private boolean isVaild;
	
	/**是否有记录*/
	private boolean hasRecord;
	
	/** 联机或脱机交易序号*/
	private String tradeNum;
	
	/** 透支限额*/
//	private int overdraft;
	
	/**交易金额*/
	private String tradeAmount;
	
	/**交易余额*/
	private String tradeBalance;
	
	/**交易卡号*/
	private String tradeCard;
	
	/** 交易类型*/
	private String tradeType;
	
	/**终端机编号*/
	private String terminalNum;
	
	/**交易时间戳*/
	private String tradeTime;

	

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getTradeBalance() {
		return tradeBalance;
	}

	public void setTradeBalance(String tradeBalance) {
		this.tradeBalance = tradeBalance;
	}

	public String getTradeCard() {
		return tradeCard;
	}

	public void setTradeCard(String tradeCard) {
		this.tradeCard = tradeCard;
	}

	public boolean isVaild() {
		return isVaild;
	}

	public void setVaild(boolean isVaild) {
		this.isVaild = isVaild;
	}

	public boolean isHasRecord() {
		return hasRecord;
	}

	public void setHasRecord(boolean hasRecord) {
		this.hasRecord = hasRecord;
	}

	public String getTradeNum() {
		return tradeNum;
	}

	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}

	public String getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(String tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public String getTerminalNum() {
		return terminalNum;
	}

	public void setTerminalNum(String terminalNum) {
		this.terminalNum = terminalNum;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	@Override
	public boolean equals(Object o)
	{
	    if(o instanceof LLTradeRecord)
	    {
	    	if((tradeTime == ((LLTradeRecord)o).getTradeTime()) && (tradeAmount ==  ((LLTradeRecord)o).getTradeAmount()))
	    	{
	    		return true;
	    	}
	    }
		return false;
	}
	
	@Override
	public String toString()
	{
		String result = "【 交易时间:"+tradeTime+"  交易序号:"+tradeNum+"交易金额:"+tradeAmount+"终端机编号:"+terminalNum+"】";
		return result;
	}
	
}
