package com.example.android.bluetoothlegatt.proltrol;

/**
 * @author guanxin@hoolai.com
 * 请求数据，是一个byte数组
 */

public class LPRequest {
	public static final int  DATA_LEN = 20;       // 命令数据长度
	public static final byte VERSION = 1;
	public static final byte HEAD = (byte) 0xAF;
	public static final int  TOTAL_LEN = DATA_LEN ;  // 每个命令的总长度
	
	
	private byte[]  data;    // 命令数据
	private int     len;     // 当前组装长度
	
	public  LPRequest() {
		data = new byte[TOTAL_LEN];
		data[len++] = HEAD;
		data[len++] = VERSION;
		data[len++] = DATA_LEN;
	}
	
	public  LPRequest  appendByte( byte x ) throws LPException {
		if( len >= DATA_LEN ) {
			LPException  ex = new LPException( LPErrCode.LP_ILLEGAL_CMD );
			String  strData = String.valueOf( data[0] );
			for( int i = 1; i < len; ++i ) strData = strData + "," + data[i];
			strData = strData + "," + x;
			ex.addMsg( "LPRequest Append", strData );
			throw ex;
		}
		data[len++] = x;
		return  this;
	}
	
	public  LPRequest  appendShort( short x ) throws LPException {
		return  this.appendByte( (byte)( (x >> 8) & 0xFF ) ).
				appendByte( (byte)( x & 0xFF ) );
	}
	
	public  LPRequest  appendInt( int x ) throws LPException {
		return  this.appendByte( (byte)( (x >> 24) & 0xFF ) ).
				appendByte( (byte)( (x >> 16) & 0xFF ) ).
				appendByte( (byte)( (x >> 8) & 0xFF ) ).
				appendByte( (byte)( x & 0xFF ) );
	}
	
	public  LPRequest  appendLong( long x ) throws LPException {
		return  this.appendByte( (byte)( (x >> 56) & 0xFF ) ).
				appendByte( (byte)( (x >> 48) & 0xFF ) ).
				appendByte( (byte)( (x >> 40) & 0xFF ) ).
				appendByte( (byte)( (x >> 32) & 0xFF ) ).
				appendByte( (byte)( (x >> 24) & 0xFF ) ).
				appendByte( (byte)( (x >> 16) & 0xFF ) ).
				appendByte( (byte)( (x >> 8) & 0xFF ) ).
				appendByte( (byte)( x & 0xFF ) );
	}
	
	public  byte[]  getData() {
		return  data;
	}
	
//	public LPRequest end()
//	{
//		data[len++] = seq++;
//		return this;
//	}
	
//	public  LPRequest  makeCheckSum() {
//		int  sum = 0;
//		for( int i = 0; i < DATA_LEN; ++i ) sum += ( data[i] & 0xFF );
//		data[DATA_LEN] = (byte)( (sum >> 8) & 0xFF );
//		data[DATA_LEN + 1] = (byte)( sum & 0xFF );
//		return this;
//	}
}
