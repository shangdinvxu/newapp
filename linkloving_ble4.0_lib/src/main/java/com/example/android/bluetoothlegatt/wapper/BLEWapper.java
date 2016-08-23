package com.example.android.bluetoothlegatt.wapper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.example.android.bluetoothlegatt.exception.BLENotBounded;
import com.example.android.bluetoothlegatt.exception.BLENotEnabledException;
import com.example.android.bluetoothlegatt.exception.BLENotSupportException;
import com.example.android.bluetoothlegatt.exception.BLESendTimeOutException;
import com.example.android.bluetoothlegatt.exception.BLErrCode;
import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.proltrol.LPUtil;
import com.example.android.bluetoothlegatt.utils.OwnLog;
import com.example.android.bluetoothlegatt.wapper.CmdFinder.OnCmdFindedListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 为了拿到中央BluetoothGatt 首先拿到BluetoothManager 再拿到BluetoothAdapt
 * 开始扫描：btAdapter.startLeScan
 * */
public class BLEWapper  implements BLEInterface {

	private static final String TAG = BLEWapper.class.getSimpleName();

	private static BLEWapper instence = null;

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private BluetoothLeScanner mLEScanner;
	private ScanSettings settings;
	private List<ScanFilter> filters;
	private List<BluetoothGattCharacteristic> list = new ArrayList<BluetoothGattCharacteristic>();

	/** 蓝牙的service包含的特征 */
	public BluetoothGattCharacteristic writeCharacteristic;
	private BluetoothGattCharacteristic readCharacteristic;
	
	public BluetoothGattCharacteristic mOad_IDENTIFY;
	public BluetoothGattCharacteristic mOad_BLOCK_REQUEST;

	private boolean sendBusy = false;
	public boolean notsendbroad = false;
	
	private long start;

	private int reciveDataLen = -1;
	private int headDataLen = -1;
	public byte[] tempData;
	public byte[] headData;
	
	private int time=1;
	private boolean BLESendTimeOutException=false;
	private static int REC_CMD= 0;
	private static boolean LENTHMORE20=false;

	private BluetoothAdapter.LeScanCallback mCallback;
//	private ScanCallback mCallback_Lollipop;
	public  byte[] boundData;
	public byte[] getBoundData() {
		return boundData;
	}
	/**
	 * 手环特定UUID
	 */
//	public static UUID serviceUUID = UUID.fromString("0000FEE7-0000-1000-8000-00805F9B34FB");  // 手环设备的id
	public static UUID serviceUUID = UUID.fromString("0000FEE1-0000-1000-8000-00805F9B34FB");  // 手表设备的id
	public static UUID writeUUID   = UUID.fromString("0000FEC7-0000-1000-8000-00805F9B34FB");  
	public static UUID readUUID    = UUID.fromString("0000FEC8-0000-1000-8000-00805F9B34FB");
	public static UUID confUUID    = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");  
	
	public static UUID OAD_BLOCK_REQUEST_UUID    = UUID.fromString("0000FEC9-0000-1000-8000-00805F9B34FB");
//	public static UUID OAD_BLOCK_REQUEST_UUID    = UUID.fromString("f000ffc2-0451-4000-b000-000000000000"); 
	
	public static int OAD_HEAD= 1000;
	public static int OAD_ALL= OAD_HEAD+1;
	public static int NOT_OAD= OAD_ALL+1;
	public static int BOUND= NOT_OAD+1;
	public static int TEST= BOUND+1;
	
	private Context mContext;

	private boolean mScanning;  //false

	// 超时
	private static final int TIMEOUT = 5000;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";  
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_GATT_CONNECTED_FAILED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED_FAILED";
	public final static String ACTION_GATT_CONNECTING = "com.example.bluetooth.le.ACTION_GATT_CONNECTING";
	public final static String EXTRA_STATUS = "com.example.bluetooth.le.EXTRA_STATUS";
	public final static String ACTION_DATA_NOTIFY  = "com.example.bluetooth.le.ACTION_DATA_NOTIFY";
	public final static String EXTRA_DATA  = "com.example.bluetooth.le.EXTRA_DATA";  //
	public final static String EXTRA_GATT  = "com.example.bluetooth.le.EXTRA_GATT";
	public final static String BOUND_STATUS  = BluetoothDevice.ACTION_BOND_STATE_CHANGED;
	
	public BluetoothManager getmBluetoothManager() {
		return mBluetoothManager;
	}
	
	

	public BluetoothAdapter getmBluetoothAdapter() {
		return mBluetoothAdapter;
	}

	public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
		this.mBluetoothAdapter = mBluetoothAdapter;
	}


	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		//连接状态改变
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status , int newState) {
			   String intentAction;
			  OwnLog.i(TAG, "onConnectionStateChange  status:"+status);
			   if(status==133 || status ==129){
				    intentAction = ACTION_GATT_DISCONNECTED;
					broadcastUpdate(intentAction, mContext);
				   if(mBluetoothGatt==null)
					   return;
			   }else{
				   if (newState == BluetoothProfile.STATE_CONNECTED) {           //连接 2
						intentAction = ACTION_GATT_CONNECTED;
						broadcastUpdate(intentAction, mContext);
						Log.i(TAG, "mGattCallback Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
					} else if (newState == BluetoothProfile.STATE_DISCONNECTED) { //断开 0 
						intentAction = ACTION_GATT_DISCONNECTED;
						Log.i(TAG, "mGattCallback Disconnected from GATT server.");
						broadcastUpdate(intentAction, mContext);
					}
			   }
			
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.i(TAG, "onServicesDiscovered  status:"+status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
				if (service != null) 
				{
					Log.e(TAG, "service--uuid......"+ service.getUuid().toString());
					// 获取读写管道
					mOad_BLOCK_REQUEST=service.getCharacteristic(OAD_BLOCK_REQUEST_UUID); 
					readCharacteristic = service.getCharacteristic(readUUID);   
					writeCharacteristic = service.getCharacteristic(writeUUID);

//					 设置通知(read)
					mBluetoothGatt.setCharacteristicNotification(readCharacteristic, true); 
					BluetoothGattDescriptor readdescriptor = readCharacteristic . getDescriptor(confUUID);
					readdescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);// 设置INDICATION模式 
					Log.i(TAG, "mBluetoothGatt.writeDescriptor(readdescriptor)......"+ mBluetoothGatt.writeDescriptor(readdescriptor)); 
					notsendbroad=false;
					broadcastUpdate( ACTION_GATT_SERVICES_DISCOVERED, mContext,status,notsendbroad);	
				}
			} 
			else {
				// 当找设备不包含我们的服务时通知连接失败状态
				broadcastUpdate( ACTION_GATT_CONNECTED_FAILED , mContext);
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		public void onDescriptorWrite(BluetoothGatt gatt,BluetoothGattDescriptor descriptor, int status)
		{
//			   OwnLog.i(TAG, "status是："+status+"--准备发送广播！");
			    if(status==0){
			    }
			    else
			    	broadcastUpdate( ACTION_GATT_CONNECTED_FAILED , mContext);
		};
		

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
			Log.i(TAG, "onCharacteristicRead  status:"+status);
		}
		
		
        @Override
		public void onCharacteristicWrite(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
        	
        	REC_CMD=status;
        	
        	if( status==REC_CMD &&  LENTHMORE20 == true){
        		OwnLog.e(TAG, "onCharacteristicWrite  回调发送成功！");
				synchronized (BLEWapper.class) {
					BLEWapper.class.notify();
//					Log.e(TAG, "...................notifynotifynotify........................");
				}
			}
		}

		//当客户端有数据发过来的时候，onCharacteristicChanged 该回调方法会被调用
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic) {
			
			String uuid = characteristic.getUuid().toString(); 
			
			if(uuid.equals(OAD_BLOCK_REQUEST_UUID.toString())) // OAD_BLOCK_REQUEST_UUID管道
			{
				headData = characteristic.getValue();
				if(headData!=null){
					LPUtil.printData(headData,"Revice  Data");
					headDataLen = headData.length ;
					synchronized (BLEWapper.class) {
						BLEWapper.class.notify();
					}
				}else{
					headDataLen=-1;
				}
			}
			else
			{
		    	    final byte[] data = characteristic.getValue();
					if (data != null && data.length > 0) {
						// 将获得  命令过滤
						CmdFinder.getInstence(new OnCmdFindedListener() {
							@Override
							public void onCmdFinded(byte[] cmd) {
								reciveDataLen = cmd.length ;
								tempData = cmd ;
								synchronized (BLEWapper.class) {
									BLEWapper.class.notify();
								}
							}
						}).appendData(data);
					} else {
						reciveDataLen = -1;
					}
		    	    
				}
		      }
	};
	
	private BLEWapper() {

	}

	public synchronized static BLEWapper getInstence() {
		if (instence == null) {
			instence = new BLEWapper();
		}
		return instence;
	}
	 public List<BluetoothGattCharacteristic> getSupportedGattServices() {
		    if (mBluetoothGatt == null)
		      return null;
		    return instence.list;
		  }
     /**
      * 初始化
     * @throws BLENotBounded 
      */
	@Override
	public boolean init(Context context) throws BLENotSupportException,
			BLENotEnabledException, BLENotBounded {
		if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			throw new BLENotSupportException();
		}

		mContext = context;
		/**先拿到BluetoothManager*/
		mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		/**
		 * 再拿到BluetoothAdapt
		 * 获得本地Android设备的蓝牙模块
		 */
		mBluetoothAdapter = mBluetoothManager.getAdapter();

		if (mBluetoothAdapter == null) {
			throw new BLENotSupportException(); //设备不支持异常
		}

		if (!mBluetoothAdapter.isEnabled()) {
				Log.e(TAG, "BLENotEnabledException() 设备不可用异常");
				throw new BLENotEnabledException(); //设备不可用异常
		}

//		if (Build.VERSION.SDK_INT >= 21) {
//			mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
//			settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
//			filters = new ArrayList<>();
//		}

		// if(mBluetoothAdapter != null && mBluetoothManager != null &&
		// mBluetoothAdapter.isEnabled())
		if (isInit()) {
			return true;
		}
		return false;
	}
     /**一定要保证代码的严谨性*/
	public boolean isInit() {
		return mBluetoothAdapter != null  &&  mBluetoothManager != null && mBluetoothAdapter.isEnabled();
	}
	// FOR TEST!!!!!!!!!!!!
		public List<BluetoothDevice> getConnectedDevices()
		{
			List<BluetoothDevice> ls = null;
			if(mBluetoothManager != null)
				ls = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
			
			return ls;
		}
		

/**
 * 扫描
 */
	@Override
	public void scan(Handler handler , final BluetoothAdapter.LeScanCallback mLeScanCallback) throws BLException {
        	stopScan();
    		mCallback = mLeScanCallback;
    		mScanning = true;
//    		UUID[] uuid={serviceUUID};
//    		mBluetoothAdapter.startLeScan(uuid,mLeScanCallback);  // 开始扫描  此方法部分设备无法搜索到
    		mBluetoothAdapter.startLeScan(mLeScanCallback);
	}

	@Override
	public void stopScan() throws BLException {
		if (!mScanning)
			return;
		mScanning = false;
		if (!isInit()) {
			throw new BLException(BLErrCode.BLE_INIT_ERR);
		}
		if (mBluetoothAdapter != null){
			mBluetoothAdapter.stopLeScan(mCallback);
		}
		OwnLog.i(TAG, "....................stopScan......................");
	}

//	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//	@Override
//	public void scanAPI21(Handler handler, ScanCallback scanCallback) throws BLException {
//		stopScannAPI21();
//		mCallback_Lollipop = scanCallback;
//		mScanning = true;
//		mLEScanner.startScan(filters, settings, mCallback_Lollipop);
//	}

//	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//	@Override
//	public void stopScannAPI21() throws BLException {
//		if (!mScanning)
//			return;
//		mScanning = false;
//		if (!isInit()) {
//			throw new BLException(BLErrCode.BLE_INIT_ERR);
//		}
//		if (mLEScanner != null){
//			mLEScanner.stopScan(mCallback_Lollipop);
//		}
//		OwnLog.i(TAG, "....................stopScan......................");
//	}

	@Override
	public boolean connect(String address) throws BLException {
		if (mBluetoothAdapter == null || mBluetoothManager == null
				|| !mBluetoothAdapter.isEnabled()) {
			throw new BLException(BLErrCode.BLE_INIT_ERR);
		}
		// Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
           OwnLog.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }
        address = address.toUpperCase();
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
        	throw new BLException(BLErrCode.BLE_CONNECT_ERR);
        }
        OwnLog.i(TAG, "..........................connect......................"); 
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        mBluetoothDeviceAddress = address;
        if(mBluetoothGatt != null)
            return true;
        else
        	return false;
	}

	/**
	 *  断开连接
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "disconnect() BluetoothAdapter not initialized");
			return;
		}
		OwnLog.d(TAG, "disconnect ble!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		if (mBluetoothGatt != null)
			mBluetoothGatt.disconnect();
	}

	public void close() {
		OwnLog.d(TAG, "执行close方法");
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "close() BluetoothAdapter not initialized");
			return;
		}
		OwnLog.d(TAG, "close ble!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		if (mBluetoothGatt != null) {
			mBluetoothGatt.close();
			mBluetoothGatt = null;
		}
	}
   /**
    * 发送数据
    */
	@Override
	public byte[] send(byte[] data,int status) throws BLException, BLESendTimeOutException {
		if (mBluetoothAdapter == null || mBluetoothManager == null
				|| !mBluetoothAdapter.isEnabled()) {
			throw new BLException(BLErrCode.BLE_INIT_ERR);
		}

		if (mBluetoothGatt == null) {
			throw new BLException(BLErrCode.BLE_CONNECT_ERR);
		}

		if (sendBusy) {
			throw new BLException(BLErrCode.BLE_SEND_BUSY);
		}
		
		if (writeCharacteristic == null) {
			throw new BLException(BLErrCode.BLE_SEND_ERR);
		}
		sendBusy = true;
		 if(data.length>20){
			 
			 LENTHMORE20 = true;
			 for(int i=0;i<(data.length+19)/20;i++){
				 byte[] send_data = new byte[data.length-(i*20)<20?data.length-(i*20):20];
				 System.arraycopy(data, i*20, send_data, 0, data.length-(i*20)<20?data.length-(i*20):20);
				 if(REC_CMD==0){
					 writeCharacteristic.setValue(send_data);
				     mBluetoothGatt.writeCharacteristic(writeCharacteristic);
				     synchronized (BLEWapper.class) {
							try {
								OwnLog.d(TAG, "...................waiting.........................");
								BLEWapper.class.wait(TIMEOUT);
							} catch (InterruptedException e) {
								OwnLog.d(TAG, "................InterruptedException................");
								e.printStackTrace();
							}
				     }
				 }
//			   
			 }
		 }
		 else
		 {
				 LENTHMORE20 = false;
			     writeCharacteristic.setValue(data);
		         mBluetoothGatt.writeCharacteristic(writeCharacteristic); 
		 }
		 synchronized (BLEWapper.class) {
				try {
					OwnLog.d(TAG, "...................waiting.........................");
					BLEWapper.class.wait(TIMEOUT);
				} catch (InterruptedException e) {
					OwnLog.d(TAG, "................InterruptedException................");
					e.printStackTrace();
				}
		}
		BLESendTimeOutException=false;
		sendBusy = false;
		
		if (reciveDataLen <= 0) {
			CmdFinder.getInstence(new OnCmdFindedListener() {
				
				@Override
				public void onCmdFinded(byte[] cmd) {
					Log.e(TAG, "接收到错误的数据！删除之前的cmd");
				}
			}).clearData(true);
			
			throw new BLESendTimeOutException();
		}
		LPUtil.printData(tempData,"Revice  DataAll");
		byte[] revData = new byte[reciveDataLen];
		//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
		System.arraycopy(tempData, 0, revData, 0, reciveDataLen);  
		
		if(!checksum(revData)){
             CmdFinder.getInstence(new OnCmdFindedListener() {
				
				@Override
				public void onCmdFinded(byte[] cmd) {
					Log.e(TAG, "接收到错误的数据！删除之前的cmd");
				}
			}).clearData(true);
             throw new BLESendTimeOutException();
		}
		reciveDataLen = -1;
		return revData;
	}

   /**
    * 检查checksum 合法性
    * @param revData
    * @return
    */
    private boolean checksum(byte[] revData) {
	      int  sum = 0;
	      for( int i = 0; i < revData.length-2; ++i )
		      sum += ( revData[i] & 0xFF );
	      if(sum ==makeShort(revData[revData.length-2],revData[revData.length-1]) )
		      return true;
	      return false;
	
     }
    
    public static int makeShort(byte b1, byte b2) {
		return (int) (((b1 & 0xFF) << 8) | (b2 & 0xFF));
	}


	/**
	 *            true表示关闭GATT Client端，false表示不关闭.推荐只在退出app进程时设true，不则设false。
	 *            场景：
	 *            比如在 app 中切换重新登陆时，如果本参数为true ，则再登陆进来时显示会是已连接（当然也就不再能触发再连蓝牙过程）。估计
	 *            原因在于关闭是个异步过程
	 *            ，而此时直接close掉GATT客户端将不能及时收到异步的蓝牙关闭事件，也就不能重置Provider里的state状 态。
	 */
	@Override
	public boolean release() {
		Log.e(TAG,  "执行release了");
		try {
			disconnect();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			close();
			return true;
		} catch (Exception e) {
			Log.w(TAG, e.getMessage(), e);
		} finally {
			mBluetoothAdapter = null;
			mBluetoothManager = null;
			mBluetoothGatt = null;
		}
		return false;
	}
	
	/**
	 *
	 *蓝牙缓存清除
	 *
	 **/
	public boolean refreshDeviceCache(){
		if (mBluetoothGatt != null)
		{
			try {
				BluetoothGatt localBluetoothGatt = mBluetoothGatt;
				Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
				if (localMethod != null) {
					boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
					return bool;
				}
			}
			catch (Exception localException) {
				OwnLog.i("","An exception occured while refreshing device");
			}			
		}
		return false;
	}

	private void broadcastUpdate(final String action, Context context) {
		final Intent intent = new Intent(action);
		context.sendBroadcast(intent);
	}
	private void broadcastUpdate(final String action, Context context,int status) {
		final Intent intent = new Intent(action);
		intent.putExtra(EXTRA_STATUS, status);
		context.sendBroadcast(intent);
	}
	private void broadcastUpdate(final String action, Context context,int status,boolean notsendbroad) {
		final Intent intent = new Intent(action);
		intent.putExtra(EXTRA_STATUS, status);
		intent.putExtra(EXTRA_DATA, notsendbroad);
		context.sendBroadcast(intent);
	}
	
	 public byte[] writeCharacteristic(BluetoothGattCharacteristic characteristic,byte[] data,int stauts) throws com.example.android.bluetoothlegatt.exception.BLESendTimeOutException
	  {
		 byte[] error = {0x0,0x0};
	    if ((mBluetoothDeviceAddress!=null) && (this.mBluetoothGatt != null))
	    {
//	    	Log.i(TAG, "当前线程是："+Thread.currentThread().getName());
//	    	if(stauts==OAD_HEAD){
//	    		notsendbroad=true;
//		    	mBluetoothGatt.setCharacteristicNotification(mOad_BLOCK_REQUEST, true);
//				BluetoothGattDescriptor oaddescriptor = mOad_BLOCK_REQUEST.getDescriptor(confUUID);
//				oaddescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);// 设置NOTIFY模式
//				mBluetoothGatt.writeDescriptor(oaddescriptor);   
//				waitIdle(100);
//	    	}
	    	 if(stauts==OAD_ALL){
	    		    characteristic.setValue(data);
			        mBluetoothGatt.writeCharacteristic(characteristic);
			        if(time%4==0){
			            synchronized (BLEWapper.class) {
			        	try {
							BLEWapper.class.wait(TIMEOUT);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
			        }
			       }
			        time++;
		        }
	    	 else if(stauts==OAD_HEAD){
	    		 notsendbroad=true;
			    mBluetoothGatt.setCharacteristicNotification(mOad_BLOCK_REQUEST, true);
				BluetoothGattDescriptor oaddescriptor = mOad_BLOCK_REQUEST.getDescriptor(confUUID);
				oaddescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);// 设置NOTIFY模式
				mBluetoothGatt.writeDescriptor(oaddescriptor);  
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
//				waitIdle(100);
				OwnLog.i(TAG, ".................characteristic.setValue(oad head)................");
		        	characteristic.setValue(data);
			        mBluetoothGatt.writeCharacteristic(characteristic);
			        synchronized (BLEWapper.class) {
						try {
							OwnLog.i(TAG, "...................waiting.........................");
							BLEWapper.class.wait(10000);
						} catch (InterruptedException e) {
							OwnLog.i(TAG, "................InterruptedException................");
							e.printStackTrace();
						}
		        }
			}
	    	 if(headDataLen<=0){
	    		 
	    		 byte[] oad_rcv = new byte[2];
	    		 System.arraycopy(error, 0, oad_rcv, 0, 2);
	    		 return oad_rcv;
			   }else{
				   byte[] oad_rcv = new byte[headDataLen];
			 		//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
			 		System.arraycopy(headData, 0, oad_rcv, 0, headDataLen);
			 		headDataLen=-1;
			 		return oad_rcv;
			   }
	 		
	    }
	    return null;
	  }
	 
	 public byte[] writeCharacteroftest(byte[] data) throws BLESendTimeOutException{
		 int send_time=(data.length+15)/16;
		 byte[] send_data = new byte[20];
		 for(int i=0;i<send_time;i++){
			 System.arraycopy(data, i*20, send_data, 0, data.length-(i*20)<20?data.length-(i*20):20);
			 writeCharacteristic.setValue(send_data);
		     mBluetoothGatt.writeCharacteristic(writeCharacteristic);
		     send_data = new byte[20];
		 }
		 synchronized (BLEWapper.class) {
				try {
					OwnLog.d(TAG, "...................waiting.........................");
					BLEWapper.class.wait(TIMEOUT);
				} catch (InterruptedException e) {
					OwnLog.d(TAG, "................InterruptedException................");
					e.printStackTrace();
				}
         }
		 if (reciveDataLen <= 0) {
				throw new BLESendTimeOutException();
			}
			byte[] revData = new byte[reciveDataLen];
			//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
			System.arraycopy(tempData, 0, revData, 0, reciveDataLen);  
			reciveDataLen = -1;
		return revData;
	 }
	 

	 public boolean waitIdle(int i) {
		    i /= 10;
		    while (--i > 0) {
		        try {
		          Thread.sleep(3000);
		        } catch (InterruptedException e) {
		          e.printStackTrace();
		        }
		    }

		    return i > 0;
		  }
}
