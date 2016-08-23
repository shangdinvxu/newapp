package com.example.android.bluetoothlegatt.proltrol.dto;

public class LLXianJinCard {
	/** 是否有效 */
	private boolean isVaild;
	/** 是否有记录 */
	private boolean hasRecord;
	/** 交易日期 */
	private String data_3;
	/** 交易时间 */
	private String time_3;
	/** 交易金额 */
	private String xianjinAmount_6;
	/** 其他金额 */
	private String xianjinAmount_other_6;
	/** 终端国家代码 */
	private String country_2;
	/** 交易货币代码 */
	private String tredmoney_2;
	/** 商户名称 */
	private String store_name_20;
	/** 交易类型 */
	private String tred_type_1;
	/** 交易计数器 */
	private String tred_count_2;
	/** 交易余额 */
	private String tradeBalance;
	/** 交易卡号 */
	private String tradeCard;

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

	public String getData_3() {
		return data_3;
	}

	public void setData_3(String data_3) {
		this.data_3 = data_3;
	}

	public String getTime_3() {
		return time_3;
	}

	public void setTime_3(String time_3) {
		this.time_3 = time_3;
	}

	public String getXianjinAmount_6() {
		return xianjinAmount_6;
	}

	public void setXianjinAmount_6(String xianjinAmount_6) {
		this.xianjinAmount_6 = xianjinAmount_6;
	}

	public String getXianjinAmount_other_6() {
		return xianjinAmount_other_6;
	}

	public void setXianjinAmount_other_6(String xianjinAmount_other_6) {
		this.xianjinAmount_other_6 = xianjinAmount_other_6;
	}

	public String getCountry_2() {
		return country_2;
	}

	public void setCountry_2(String country_2) {
		this.country_2 = country_2;
	}

	public String getTredmoney_2() {
		return tredmoney_2;
	}

	public void setTredmoney_2(String tredmoney_2) {
		this.tredmoney_2 = tredmoney_2;
	}

	public String getStore_name_20() {
		return store_name_20;
	}

	public void setStore_name_20(String store_name_20) {
		this.store_name_20 = store_name_20;
	}

	public String getTred_type_1() {
		return tred_type_1;
	}

	public void setTred_type_1(String tred_type_1) {
		this.tred_type_1 = tred_type_1;
	}

	public String getTred_count_2() {
		return tred_count_2;
	}

	public void setTred_count_2(String tred_count_2) {
		this.tred_count_2 = tred_count_2;
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
}
