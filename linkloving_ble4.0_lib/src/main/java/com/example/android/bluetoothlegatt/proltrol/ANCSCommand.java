package com.example.android.bluetoothlegatt.proltrol;

public class ANCSCommand {
    /**
     *   ANCS_CategoryID
     * */
	public static final byte CategoryIDOther = 0x00;
	public static final byte CategoryIDIncomingCall = 0x01;
	public static final byte CategoryIDMissedCall = 0x02;
	public static final byte CategoryIDVoicemail = 0x03;
	public static final byte CategoryIDSocial = 0x04;
	public static final byte CategoryIDSchedule = 0x05;
	public static final byte CategoryIDEmail = 0x06;
	public static final byte CategoryIDNews = 0x07;
	public static final byte CategoryIDHealthAndFitness = 0x08;
	public static final byte CategoryIDBusinessAndFinance = 0x09;
	public static final byte CategoryIDLocation = 0x0a;
	public static final byte CategoryIDEntertainment = 0x0b;
	public static final byte CategoryIDLinkloving = (byte) 0xA0;
	public static final byte CategoryIDWeather = (byte) 0xB0;
	
	/**
     *  ANCS_AppID
     * */
	public static final byte   ANCS_APPNameID_LINKLOVING = 0x00;
	public static final byte   ANCS_APPNameID_Phone = 0x01;
	public static final byte   ANCS_APPNameID_SMS = 0x02;
	public static final byte   ANCS_APPNameID_WEIXIN = 0x03;
	public  static final byte   ANCS_APPNameID_QQ = 0x04;
	public static final byte   ANCS_APPNameID_UNKNOW = (byte) 0xFF;
    
    /**
     *  ANCS_EventID
     * */
	public static final byte   EventIDNotificationAdded = 0x00;
	public static final byte   EventIDNotificationModified = 0x01;
	public static final byte   EventIDNotificationRemoved = 0x02;
    
    /**
     *  ANCS_Notification AttributeID
     * */
	public static final byte   NotificationAttributeIDAppIdentifier = 0x00;
	public static final byte   NotificationAttributeIDTitle = 0x01;
	public static final byte   NotificationAttributeIDSubtitle = 0x02;
	public static final byte   NotificationAttributeIDMessage = 0x03;
	public static final byte   NotificationAttributeIDMessageSize = 0x04;
	public static final byte   NotificationAttributeIDDate = 0x05;
	public static final byte   NotificationAttributeIDPositiveActionLabel = 0x06;
	public static final byte   NotificationAttributeIDNegativeActionLabel = 0x07;
    /**以下是天气预报*/
	public static final byte   NotificationAttributeIDAddress = (byte) 0xE0;
	public static final byte   NotificationAttributeIDWeather = (byte) 0xE1;
	public static final byte   NotificationAttributeIDTemperature = (byte) 0xE2;
	public static final byte   NotificationAttributeIDWind = (byte) 0xE3;
	public static final byte   NotificationAttributeIDAir = (byte) 0xE4;
	public static final byte   NotificationAttributeIDHumidity = (byte) 0xE5;
	public static final byte   NotificationAttributeIDUltraviolet = (byte) 0xE6;
	public static final byte   NotificationAttributeIDEnd = (byte) 0xEF;

    
    
}
