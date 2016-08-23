package com.example.android.bluetoothlegatt;


import android.content.Context;
import android.util.Log;

import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.proltrol.LPException;
import com.example.android.bluetoothlegatt.proltrol.LepaoProtocalImpl;
import com.lnt.connectfactorylibrary.ConnectFactoryImpl;
import com.lnt.connectfactorylibrary.ConnectReturnImpl;

import java.util.Observable;
import java.util.Observer;

public class ConnectBLEFactoryImpl implements ConnectFactoryImpl{
	private Context context;
	private BLEProvider provider;
	private LepaoProtocalImpl lpi = new LepaoProtocalImpl();
	//判断是否是主动断开
	private boolean disconnect;
	
	private ConnectReturnImpl connectReturnImpl;
	/** */
	private Observer obsFor_BleConnect = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			if (data != null) {
				int ble_state = (Integer) data;
				if(ble_state == provider.STATE_DISCOVERED){
					
					if(connectReturnImpl!=null){
						Log.e("ConnectBLEFactoryImpl","蓝牙连接成功====MAC:"+provider);
						Log.e("ConnectBLEFactoryImpl","蓝牙连接成功====MAC:"+provider.getCurrentDeviceMac());
						connectReturnImpl.connectResult(true,provider.getCurrentDeviceMac());
					}
					
				} 
				else if(ble_state == provider.STATE_DISCONNECTED){
					if(connectReturnImpl!=null && !disconnect ){
						Log.e("ConnectBLEFactoryImpl","蓝牙连接失败");
						connectReturnImpl.connectResult(false,provider.getCurrentDeviceMac());
					}
					
				}
					
			}
		}
	};
	
	//singleton
	private static ConnectBLEFactoryImpl instance;
	
	public ConnectBLEFactoryImpl(Context context,BLEProvider bleprovider)
	{
		this.context = context;
		if(bleprovider!=null)
		{
			this.provider = bleprovider;
			if(provider.getObsFor_BleConnect()==null)
				provider.setObsFor_BleConnect(obsFor_BleConnect);
		}else{
			initBLEProvider();
		}
	}
	
	public static ConnectBLEFactoryImpl getInstance(Context context,BLEProvider bleprovider)
	{
		if(instance==null)
			// FIX BUG: 以下context不能直接传Activity及其子类的句柄，否则将发生内存泄漏！
			// Application为全局唯一，所以不存在不释放的问题！
			instance=new ConnectBLEFactoryImpl(context,bleprovider);
		
		return instance;		
	}
	
	public BLEProvider getBLEProvider()
	{
		return provider;		
	}


	@Override
	public Object closeCommunication() {
		// 打开通信通道	//暂无
		return null;
	}

	@Override
	public Object closeConnection() {
		// 关闭通信通道   //暂无
		provider.disConnect();
		provider.resetDefaultState();
		disconnect = true;
		return true;
		
	}

	@Override
	public Object getConnectState() {
		if(provider!=null)
			return provider.isConnectedAndDiscovered();
		else
			return false;
	}

	@Override
	public int getElectricQuantity() {
		//暂无
		return -2;
	}

	@Override
	public Object ledShow() {
		//暂无
		return null;
	}

	@Override
	public Object openCommunication() {
		//暂无
		return null;
	}

	@Override
	public Object powerOff() {
		try {
			return lpi.closeSmartCard();
		} catch (LPException e) {
			Log.e("ConnectBLEFactoryImpl","powerOff数据发送失败");
			return false;
		} catch (BLException e) {
			Log.e("ConnectBLEFactoryImpl","powerOff数据发送失败");
			return false;
		}
	}

	@Override
	public Object powerOn() {
		
		try {
			return lpi.openSmartCard();
		} catch (LPException e) {
			// TODO Auto-generated catch block
			Log.e("ConnectBLEFactoryImpl","powerOn数据发送失败");
			return false;
		} catch (BLException e) {
			// TODO Auto-generated catch block
			Log.e("ConnectBLEFactoryImpl","powerOn数据发送失败");
			return false;
		}
	}

	@Override
	public Object shake() {
		//暂无
		return null;
	}

	@Override
	public byte[] transmit(byte[] arg0) {
		try {
			byte[] data = null;
			data = trans(lpi.senddata(arg0));
			return data;
		} catch (BLException e) {
			Log.e("ConnectBLEFactoryImpl","数据发送失败");
			e.printStackTrace();
		} catch (LPException e) {
			Log.e("ConnectBLEFactoryImpl","数据发送失败");
			e.printStackTrace();
		}
		return arg0;
	}
	
	
	public static byte[] trans(byte[] data){
		byte[] new_data =null;
		if(data[1] != (byte)0x00){
				new_data = new byte[2];
				new_data[0] =(byte) 0xff;
				new_data[1] = data[2];
			}else{
				if(data.length == 4){
					new_data = new byte[2];
					System.arraycopy(data, 2, new_data, 0, 2); 
				}else{
					new_data = new byte[data.length-2];
					System.arraycopy(data, 4, new_data, 0, data.length-4); 
					new_data[new_data.length-2] = data[2];
					new_data[new_data.length-1] = data[3];
				}
			}
			
			
			return new_data;
		}

	@Override
	public void connection(Context context, String mac, ConnectReturnImpl callback) {
		disconnect = false ;
		// 连接操作
		try {
			provider.connectMacCallback(mac);
			connectReturnImpl = callback;
		} catch (BLException e) {
			e.printStackTrace();
		}
	}
	
	private BLEProvider initBLEProvider() {
		
		if(provider==null){
			
			provider = new BLEProvider(context);
		    
		    provider.setProviderHandler(new BLEHandler(context){
		    	
		    	@Override
		 		protected BLEProvider getProvider()
		 		{
		 			return provider;
		 		}
		 			
		    });
		}
		
		if(provider.getObsFor_BleConnect()==null)
			provider.setObsFor_BleConnect(this.obsFor_BleConnect);
		
		return provider;
	}

}
