package com.linkloving.rtring_new.logic.UI.ranking.rankinglistitem;

public class RankListDTO {
	
	private String date_str, ent_id, start_datetime_utc, end_datetime_utc;
	private Integer user_id, page,pageBegin,pageEnd;

	public String getDate_str() {
		return date_str;
	}

	public void setDate_str(String date_str) {
		this.date_str = date_str;
	}

	public String getEnt_id() {
		return ent_id;
	}

	public void setEnt_id(String ent_id) {
		this.ent_id = ent_id;
	}

	public String getStart_datetime_utc() {
		return start_datetime_utc;
	}

	public void setStart_datetime_utc(String start_datetime_utc) {
		this.start_datetime_utc = start_datetime_utc;
	}

	public String getEnd_datetime_utc() {
		return end_datetime_utc;
	}

	public void setEnd_datetime_utc(String end_datetime_utc) {
		this.end_datetime_utc = end_datetime_utc;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageBegin() {
		return pageBegin;
	}

	public void setPageBegin(Integer pageBegin) {
		this.pageBegin = pageBegin;
	}

	public Integer getPageEnd() {
		return pageEnd;
	}

	public void setPageEnd(Integer pageEnd) {
		this.pageEnd = pageEnd;
	}
	

}
