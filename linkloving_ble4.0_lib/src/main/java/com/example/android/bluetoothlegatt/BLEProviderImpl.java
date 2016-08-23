package com.example.android.bluetoothlegatt;

import android.content.Context;

import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.proltrol.LepaoProtocalImpl;
import com.example.android.bluetoothlegatt.utils.shenzhenInterface;

public class BLEProviderImpl implements shenzhenInterface{
	private Context context;
	private BLEProvider provider;
	private LepaoProtocalImpl lpi = new LepaoProtocalImpl();
	
	//singleton
	private static BLEProviderImpl instance;
	
	public BLEProviderImpl(Context context,BLEProvider bleprovider)
	{
		this.context = context;
		
//		ServiceUtils.CLOSE_LINK_BLE(context);
		if(bleprovider!=null)
		{
			this.provider = bleprovider;
		}else
		{
			initBLEProvider();
		}
	}
	
	private BLEProvider initBLEProvider() {
		
		if(provider==null){
			
			provider = new BLEProvider(context){
		    	
		    };
		    
		    provider.setProviderHandler(new BLEHandler(context){
		    	
		    	@Override
		 		protected BLEProvider getProvider()
		 		{
		 			return provider;
		 		}
		 			
		    });
		}
		
		return provider;
	}
	
	public static BLEProviderImpl getInstance(Context context,BLEProvider bleprovider)
	{
		if(instance==null)
			// FIX BUG: 以下context不能直接传Activity及其子类的句柄，否则将发生内存泄漏！
			// Application为全局唯一，所以不存在不释放的问题！
			instance=new BLEProviderImpl(context,bleprovider);
		
		return instance;		
	}
	
	public BLEProvider getBLEProvider()
	{
		return provider;		
	}
	
	public void setBleProviderObserver(BLEProviderObserverAdapter observer)
	{
		if(provider.getBleProviderObserver()==null)
				provider.setBleProviderObserver(observer);
	}

	@Override
	public void connection(String mac) {
		// 连接操作
		try {
			provider.connectMacCallback(mac);
		} catch (BLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Object closeConnection() {
		// 关闭通信通道   //暂无
		provider.disConnect();
		provider.resetDefaultState();
//		ServiceUtils.OPEN_LINK_BLE(context);
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
	public void transmit(byte[] arg0) {
		provider.send_data2ble_card(context, arg0);
	}

	@Override
	public void powerOn() {
		provider.openSmartCard(context);
	}

	@Override
	public void powerOff() {
		// TODO Auto-generated method stub
		provider.closeSmartCard(context);
		
	}
	
	

}

