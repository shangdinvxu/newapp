package com.linkloving.rtring_new.logic.UI.friend.group.groupmodel;

public class Group {
	private String ent_url;
	  private String ent_name;
	  private String ent_count;
	  private String join_psw;
	private String ent_id;

	public String getEnt_id() {
		return ent_id;
	}

	public void setEnt_id(String ent_id) {
		this.ent_id = ent_id;
	}

	public String getEnt_url()
	  {
	    return this.ent_url;
	  }

	  public void setEnt_url(String ent_url)
	  {
	    this.ent_url = ent_url;
	  }

	public String getEnt_name() {
		return ent_name;
	}

	public void setEnt_name(String ent_name) {
		this.ent_name = ent_name;
	}

	public String getEnt_count() {
		return ent_count;
	}

	public void setEnt_count(String ent_count) {
		this.ent_count = ent_count;
	}

	public String getJoin_psw()
	  {
	    return this.join_psw;
	  }

	  public void setJoin_psw(String join_psw)
	  {
	    this.join_psw = join_psw;
	  }
}
