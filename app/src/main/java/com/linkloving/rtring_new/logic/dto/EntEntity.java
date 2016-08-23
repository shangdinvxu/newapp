/**
 * EntEntity.java
 * @author Jason Lu
 * @date 2015-11-2
 * @version 1.0
 */
package com.linkloving.rtring_new.logic.dto;

import java.io.Serializable;
import java.util.Observer;

/**
 * @author Lz
 * 
 */
public class EntEntity implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1178002495405468303L;

	protected transient Observer propertyChangedObserver = null;

	private int user_id;
	public void setPropertyChangedObserver(Observer propertyChangedObserver)
	{
		this.propertyChangedObserver = propertyChangedObserver;
	}
private String ent_id , ent_full_name , ent_name , ent_status , portal_logo_file_name , background_file_name, splash_screen_file_name, bz, create_time,
		login_name, login_psw, ent_portal_url, ranking_1_file_name, ranking_2_file_name, ranking_3_file_name, ranking_4_file_name, ent_id_hex, firmware,
		ent_authority, company_id, join_psw;
//
//	private String ent_id="", ent_full_name="", ent_name="", ent_status="", portal_logo_file_name="", background_file_name="", splash_screen_file_name="", bz="", create_time="",
//			login_name="", login_psw="", ent_portal_url="", ranking_1_file_name="", ranking_2_file_name="", ranking_3_file_name="", ranking_4_file_name="", ent_id_hex="", firmware="",
//			ent_authority="", company_id="", join_psw="";

	public String getEnt_id()
	{
		return ent_id;
	}

	public void setEnt_id(String ent_id)
	{
		this.ent_id = ent_id;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ent_id");
	}

	public String getEnt_full_name()
	{
		return ent_full_name;
	}

	public void setEnt_full_name(String ent_full_name)
	{
		this.ent_full_name = ent_full_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ent_full_name");
	}

	public String getEnt_name()
	{
		return ent_name;
	}

	public void setEnt_name(String ent_name)
	{
		this.ent_name = ent_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ent_name");
	}

	public String getEnt_status()
	{
		return ent_status;
	}

	public void setEnt_status(String ent_status)
	{
		this.ent_status = ent_status;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ent_status");
	}

	public String getPortal_logo_file_name()
	{
		return portal_logo_file_name;
	}

	public void setPortal_logo_file_name(String portal_logo_file_name)
	{
		this.portal_logo_file_name = portal_logo_file_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "portal_logo_file_name");
	}

	public String getBackground_file_name()
	{
		return background_file_name;
	}

	public void setBackground_file_name(String background_file_name)
	{
		this.background_file_name = background_file_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "background_file_name");
	}

	public String getSplash_screen_file_name()
	{
		return splash_screen_file_name;
	}

	public void setSplash_screen_file_name(String splash_screen_file_name)
	{
		this.splash_screen_file_name = splash_screen_file_name;
		if (this.propertyChangedObserver != null)
		this.propertyChangedObserver.update(null, "splash_screen_file_name");

	}

	public String getBz()
	{
		return bz;
	}

	public void setBz(String bz)
	{
		this.bz = bz;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "bz");
	}

	public String getCreate_time()
	{
		return create_time;
	}

	public void setCreate_time(String create_time)
	{
		this.create_time = create_time;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "create_time");
	}

	public String getLogin_name()
	{
		return login_name;
	}

	public void setLogin_name(String login_name)
	{
		this.login_name = login_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "login_name");
	}

	public String getLogin_psw()
	{
		return login_psw;
	}

	public void setLogin_psw(String login_psw)
	{
		this.login_psw = login_psw;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "login_psw");
	}

	public String getEnt_portal_url()
	{
		return ent_portal_url;
	}

	public void setEnt_portal_url(String ent_portal_url)
	{
		this.ent_portal_url = ent_portal_url;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ent_portal_url");
	}

	public String getRanking_1_file_name()
	{
		return ranking_1_file_name;
	}

	public void setRanking_1_file_name(String ranking_1_file_name)
	{
		this.ranking_1_file_name = ranking_1_file_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ranking_1_file_name");
	}

	public String getRanking_2_file_name()
	{
		return ranking_2_file_name;
	}

	public void setRanking_2_file_name(String ranking_2_file_name)
	{
		this.ranking_2_file_name = ranking_2_file_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ranking_2_file_name");
	}

	public String getRanking_3_file_name()
	{
		return ranking_3_file_name;
	}

	public void setRanking_3_file_name(String ranking_3_file_name)
	{
		this.ranking_3_file_name = ranking_3_file_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ranking_3_file_name");
	}

	public String getRanking_4_file_name()
	{
		return ranking_4_file_name;
	}

	public void setRanking_4_file_name(String ranking_4_file_name)
	{
		this.ranking_4_file_name = ranking_4_file_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ranking_4_file_name");
	}

	public String getEnt_id_hex()
	{
		return ent_id_hex;
	}

	public void setEnt_id_hex(String ent_id_hex)
	{
		this.ent_id_hex = ent_id_hex;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ent_id_hex");
	}

	public String getFirmware()
	{
		return firmware;
	}

	public void setFirmware(String firmware)
	{
		this.firmware = firmware;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "firmware");

	}

	public String getEnt_authority()
	{
		return ent_authority;
	}

	public void setEnt_authority(String ent_authority)
	{
		this.ent_authority = ent_authority;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "ent_authority");
	}

	public String getCompany_id()
	{
		return company_id;
	}

	public void setCompany_id(String company_id)
	{
		this.company_id = company_id;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "company_id");
	}

	public String getJoin_psw()
	{
		return join_psw;
	}

	public void setJoin_psw(String join_psw)
	{
		this.join_psw = join_psw;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "join_psw");
	}

	@Override
	public String toString() {
		return "EntEntity{" +
				"propertyChangedObserver=" + propertyChangedObserver +
				", user_id=" + user_id +
				", ent_id='" + ent_id + '\'' +
				", ent_full_name='" + ent_full_name + '\'' +
				", ent_name='" + ent_name + '\'' +
				", ent_status='" + ent_status + '\'' +
				", portal_logo_file_name='" + portal_logo_file_name + '\'' +
				", background_file_name='" + background_file_name + '\'' +
				", splash_screen_file_name='" + splash_screen_file_name + '\'' +
				", bz='" + bz + '\'' +
				", create_time='" + create_time + '\'' +
				", login_name='" + login_name + '\'' +
				", login_psw='" + login_psw + '\'' +
				", ent_portal_url='" + ent_portal_url + '\'' +
				", ranking_1_file_name='" + ranking_1_file_name + '\'' +
				", ranking_2_file_name='" + ranking_2_file_name + '\'' +
				", ranking_3_file_name='" + ranking_3_file_name + '\'' +
				", ranking_4_file_name='" + ranking_4_file_name + '\'' +
				", ent_id_hex='" + ent_id_hex + '\'' +
				", firmware='" + firmware + '\'' +
				", ent_authority='" + ent_authority + '\'' +
				", company_id='" + company_id + '\'' +
				", join_psw='" + join_psw + '\'' +
				'}';
	}
}
