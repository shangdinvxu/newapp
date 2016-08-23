package com.linkloving.rtring_new.logic.UI.device;

public class FirmwareDTO {
	private String model_name,version_int;
	private Integer device_type, firmware_type;

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public Integer getDevice_type() {
		return device_type;
	}

	public void setDevice_type(Integer device_type) {
		this.device_type = device_type;
	}

	public Integer getFirmware_type() {
		return firmware_type;
	}

	public void setFirmware_type(Integer firmware_type) {
		this.firmware_type = firmware_type;
	}

	public String getVersion_int() {
		return version_int;
	}

	public void setVersion_int(String version_int) {
		this.version_int = version_int;
	}


}
