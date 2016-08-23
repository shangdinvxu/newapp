package com.example.android.bluetoothlegatt.proltrol;

public class WatchRequset {

	public static final int  DATA_LEN = 300;  // 命令数据长度限制
	public static final byte VERSION = 1;
	public static final byte HEAD = (byte) 0xAF;
	public static final byte HEAD_LONG = (byte) 0xAE;
	public static final int  TOTAL_LEN = DATA_LEN ;  // 每个命令的总长度


	private byte[]  data;    // 命令数据
	private byte[]  newdata; // 组装成功的数据
	private int     len;     // 当前组装长度

	public  WatchRequset() {
		data = new byte[TOTAL_LEN];
		data[len++] = HEAD;          //加入的头文件 
		data[len++] = (byte) DATA_LEN;
	}

	public  WatchRequset(int length) {
		if( length + 6 > 255){//长度过长
			data = new byte[length+7];
			data[len++] = HEAD_LONG;
			data[len++] = (byte)( (data.length >> 8) & 0x00FF );
			data[len++] = (byte)( data.length & 0x00FF );
		}else{
			data = new byte[length+6];
			data[len++] = HEAD;          //加入的头文件 
			data[len++] = (byte)( data.length & 0x00FF);
		}
	}

	public  WatchRequset  appendByte( byte x ) throws LPException {
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

	public  WatchRequset  appendByte( byte[] x ) throws LPException {
		for(int j = 0;j < x.length ; j++){
			data[len++] = x[j];
		}
		return  this;
	}

	public  WatchRequset  appendShort( short x ) throws LPException {
		return  this.appendByte( (byte)( x & 0xFF ) )
				.appendByte( (byte)( (x >> 8) & 0xFF ) );
	}

	public  WatchRequset  appendInt( int x ) throws LPException {
		return  this.appendByte( (byte)( x & 0xFF ) )
				.appendByte( (byte)( (x >> 8) & 0xFF ) )
				.appendByte( (byte)( (x >> 16) & 0xFF ) )
				.appendByte( (byte)( (x >> 24) & 0xFF ) );
	}

	public  WatchRequset  appendLong( long x ) throws LPException {
		return this.appendByte( (byte)( x & 0xFF ) )
				.appendByte( (byte)( (x >> 8) & 0xFF ) )
				.appendByte( (byte)( (x >> 16) & 0xFF ) )
				.appendByte( (byte)( (x >> 24) & 0xFF ) )
				.appendByte( (byte)( (x >> 32) & 0xFF ) )
				.appendByte( (byte)( (x >> 40) & 0xFF ) )
				.appendByte( (byte)( (x >> 48) & 0xFF ) )
				.appendByte( (byte)( (x >> 56) & 0xFF ) );
	}

	public  WatchRequset  makeCheckSum() {
		if(data[0] == HEAD_LONG){
			int  sum = 0;
			for( int i = 0; i < data.length; i++ ){
				sum += ( data[i] & 0xFF );
			}
			data[len++] = (byte)( (sum >> 8) & 0x00FF );
			data[len++] = (byte)( sum & 0x00FF );
		}
		else
		{
			data[1] =(byte) (len+2);
			int  sum = 0;
			for( int i = 0; i < data.length; ++i )
				sum += ( data[i] & 0xFF );
			data[len++] = (byte)( (sum >> 8) & 0x00FF );
			data[len++] = (byte)( sum & 0x00FF );
		}

		return this;
	}

	public  byte[]  getData() {
		newdata = new byte[len];
		//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
		System.arraycopy(data, 0, newdata, 0, len);
		return  newdata;
	}
}
