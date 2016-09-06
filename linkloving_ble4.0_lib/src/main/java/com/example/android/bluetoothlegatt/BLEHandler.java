package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.android.bluetoothlegatt.proltrol.LPUtil;
import com.example.android.bluetoothlegatt.proltrol.dto.LLTradeRecord;
import com.example.android.bluetoothlegatt.proltrol.dto.LLXianJinCard;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportData;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportRecorder;
import com.example.android.bluetoothlegatt.utils.OwnLog;

import java.util.List;

public abstract class BLEHandler extends Handler {
	private static final String TAG = BLEHandler.class.getSimpleName();
	protected Context mContext;
	public static final int REQUEST_ENABLE_BT = 0x10;

	private int index;
	private LPDeviceInfo deviceInfo;

	protected IBLEProviderObserver bleProviderObserver = null;

	public interface IBLEProviderObserver {
		public void updateFor_handleConnecting();

		public void updateFor_handleBoundDeviceFailed();

		public void updateFor_handleUserErrorMsg(int id);

		public void updateFor_handleScanTimeOutMsg();

		public void updateFor_handleSendDataError();

		public void updateFor_handleNotSupportMsg();

		public void updateFor_handleNotEnableMsg();

		public void updateFor_handleHaveNotConnectMsg();

		public void updateFor_handleConnectSuccessMsg();

		public void updateFor_handleConnectLostMsg();

		public void updateFor_handleConnectFailedMsg();

		// ** for 收到数据的通知方法
		public void updateFor_notifyForGetHistoryDayDatasSucess_D(
				List<LPSportRecorder> historyDayDatas);

		public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo);

		public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo);

		public void updateFor_notifyForDeviceAloneSyncSucess_D();

		public void updateFor_notifyForFullSyncGetSportDetailDatasSucess_D(
				List<LPSportData> sportDetailDatas);

		public void updateFor_notifyForFullSyncGetDeviceIDSucess_D(String id);

		public void updateFor_notifyForDeviceFullSyncSucess_D(
				LPDeviceInfo deviceInfo);

		public void updateFor_notifyForDeviceUnboundSucess_D();

		public void updateFor_notifyForDeviceUnboundFaild_D();

		public void updateFor_notifyForRssi_D(int rssi);

		public void updateFor_notifyForBLEDevice_D(
				BluetoothDevice bluetoothDevice);

		public void updateFor_notifyCanOAD_D();

		public void updateFor_notifyNOTCanOAD_D();

		public void updateFor_notifyOADSuccess_D(); // //

		public void updateFor_notifyOADheadback(byte[] data);

		public void updateFor_notifyOADback(byte[] data);

		public void updateFor_BoundSucess();

		public void updateFor_BoundFail();

		public void updateFor_BoundContinue();

		public void updateFor_BoundNoCharge();

		public void updateFor_SportDataProcess(Integer obj);

		public void updateFor_FlashHeadSucess();

		// public void updateFor_handleSportDataErrorMsg(List<LPSportData>
		// sportData);
		// public void updateFor_handleSportDataRetryMsg(List<LPSportData>
		// sportData);

		public void updateFor_handleDataEnd();

		public void updateFor_SetDeviceIdSucess();

		public void updateFor_GetDeviceIdSucess(String id);

		/** 设置时间成功 */
		public void updateFor_handleSetTime();

		public void updateFor_handleSetTimeFail();

		/** 设置消息提醒成功失败 */
		public void updateFor_notify();

		public void notifyForSetNOTIFYFail();

		/** 设置闹钟提醒成功失败 */
		public void updateFor_notifyForSetClockSucess();

		public void updateFor_notifyForSetClockFail();

		/** 设置久坐提醒成功失败 */
		public void updateFor_notifyForLongSitSucess();

		public void updateFor_notifyForLongSitFail();

		/** 设置抬手成功失败 */
		public void updateFor_notifyForSetHandUpFail();

		public void updateFor_notifyForSetHandUpSucess();

		/** 设置身体信息成功失败 */
		public void updateFor_notifyForSetBodySucess();

		public void updateFor_notifyForSetBodyFail();

		/** 设置步数成功失败 */
		public void updateFor_notifyForSetStepSucess();

		public void updateFor_notifyForSetStepFail();

		/** 设置省电成功失败 */
		public void updateFor_notifyForSetPowerSucess();

		public void updateFor_notifyForSetPowerFail();

		public void updateFor_CardNumber(String id); // /**获取卡号成功*/

		public void updateFor_CardNumber_fail(); // /**获取卡号fail*/

		public void updateFor_notifyForSetNameSucess();// /**设置名称成功*/

		public void updateFor_notifyForSetNameFail(); // /**设置名称失败*/

		// ** for 一卡通相关的通知方法 **//
		public void updateFor_OpenSmc(boolean isSuccess);

		public void updateFor_AIDSmc(boolean isSuccess);

		public void notifyForAIDStep1(boolean isSuccess);

		public void notifyForAIDStep2(boolean isSuccess);

		public void updateFor_GetSmcBalance(Integer obj);

		public void updateFor_GetSchoolID(String obj);

		public void updateFor_GetSmcTradeRecord(List<LLTradeRecord> list);

		public void updateFor_GetSmcTradeRecordAsync(LLTradeRecord record);

		public void updateFor_checkPINSucess_D(); // 效验pin成功

		public void updateFor_checkPINFaild_D(); // 效验pin失败

		public void updateFor_GetXJTradeRecord(List<LLXianJinCard> list); // 恒宝现金所有记录

		public void updateFor_GetXJTradeRecordAsync(LLXianJinCard record); // 恒宝现金单条记录

		public void updateFor_response_ble(byte[] obj); // 透传

		public void updateFor_response_ble_card(byte[] obj); // 透传card

		public void updateFor_getDeviceId(String obj); // 获取设备ID
		
		public void notifyFor_close7816card(boolean isSuccess);
		public void notifyFor_open7816card(boolean isSuccess);

		// ** for 蓝牙流程中数据同步到服务端时要通知的方法（以下方法并不是必须要放置于蓝牙机机制中，但放在此处将更有利于子类中继承和使用）

		/** 通知：设备绑定信息同步到服务端完成 */
		public void updateFor_boundInfoSyncToServerFinish(
				Object resultFromServer);

		/** 通知：设备绑定信息写到设备固件中完成 */
		public void updateFor_boundInfoSetToDeviceOK();

		/** 通知：运动明细数据（1日N条）同步到服务端完成 */
		public void updateFor_sportDetailDatasSyncToServerFinish(
				Object resultFromServer, int syncCount);

		/** 通知：设备绑定信息同步到服务端完成 */
		public void updateFor_historyDayDatasSyncToServerFinish(
				Object resultFromServer, int syncCount);

	}

	public static abstract class BLEProviderObserverAdapter implements IBLEProviderObserver {
		protected abstract Activity getActivity();

		@Override
		public void updateFor_FlashHeadSucess() {
		}

		// TODO 运动数据中有异常数据！！！！！！！！！！！！！！！！！！！！
		public void updateFor_handleSportDataErrorMsg(
				List<LPSportData> sportData) {
		}

		// TODO 运动数据重新获取后返回数据！！！！！！！！！！！！！！！！！！
		public void updateFor_handleSportDataRetryMsg(
				List<LPSportData> sportData) {
		}

		@Override
		public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
		}

		public void updateFor_handleConnecting() {
		}

		public void updateFor_handleBoundDeviceFailed() {
		}

		public void updateFor_handleUserErrorMsg(int id) {
		}

		public void updateFor_handleScanTimeOutMsg() {
		}

		public void updateFor_handleNotSupportMsg() {
		}

		/**
		 * 当判定蓝牙未开启时会调用此方法通知上层. <b>注意：</b> 子类在重写此方法时，务必不要忘了调用父类的本方法，否则将错过蓝牙的开启！
		 */
		public void updateFor_handleNotEnableMsg() {
		}

		public void updateFor_handleHaveNotConnectMsg() {
		}

		public void updateFor_handleConnectSuccessMsg() {
		}

		public void updateFor_handleConnectLostMsg() {
		}

		public void updateFor_handleConnectFailedMsg() {
		}

		@Override
		public void updateFor_notifyOADheadback(byte[] data) {
		}

		@Override
		public void updateFor_notifyOADback(byte[] data) {
		}

		@Override
		public void updateFor_notifyForBLEDevice_D(
				BluetoothDevice bluetoothDevice) {
		}

		@Override
		public void updateFor_handleSendDataError() {
		}

		// ** for 收到数据的通知方法
		public void updateFor_notifyForGetHistoryDayDatasSucess_D(
				List<LPSportRecorder> historyDayDatas) {
		}

		public void updateFor_notifyFor0x13ExecSucess_D(
				LPDeviceInfo latestDeviceInfo) {
		}

		public void updateFor_notifyForDeviceAloneSyncSucess_D() {
		}

		public void updateFor_notifyForFullSyncGetSportDetailDatasSucess_D(
				List<LPSportData> sportDetailDatas) {
		}

		@Override
		public void updateFor_notifyForFullSyncGetDeviceIDSucess_D(String id) {
		}

		public void updateFor_notifyForDeviceFullSyncSucess_D(
				LPDeviceInfo deviceInfo) {
		}

		public void updateFor_notifyForDeviceUnboundSucess_D() {
		}

		public void updateFor_notifyForDeviceUnboundFaild_D() {
		}

		// ** for 蓝牙流程中数据同步到服务端时要通知的方法（以下方法并不是必须要放置于蓝牙机机制中，但放在此处将更有利于子类中继承和使用）
		@Override
		public void updateFor_boundInfoSyncToServerFinish(
				Object resultFromServer) {
		}

		@Override
		public void updateFor_sportDetailDatasSyncToServerFinish(
				Object resultFromServer, int syncCount) {
		}

		@Override
		public void updateFor_historyDayDatasSyncToServerFinish(
				Object resultFromServer, int syncCount) {
		}

		@Override
		public void updateFor_boundInfoSetToDeviceOK() {
		}

		@Override
		public void updateFor_SetDeviceIdSucess() {
		}

		@Override
		public void updateFor_GetDeviceIdSucess(String id) {
		}

		@Override
		public void updateFor_CardNumber(String id) {
		}

		@Override
		public void updateFor_CardNumber_fail() {
		}

		@Override
		public void updateFor_notifyForRssi_D(int rssi) {
		}

		@Override
		public void updateFor_SportDataProcess(Integer obj) {
		}

		@Override
		public void updateFor_handleDataEnd() {
		}

		@Override
		public void updateFor_notifyCanOAD_D() {
		}

		@Override
		public void updateFor_notifyNOTCanOAD_D() {
		}

		@Override
		public void updateFor_notifyOADSuccess_D() {
		}

		@Override
		public void updateFor_handleSetTime() {
		}

		@Override
		public void updateFor_handleSetTimeFail() {
		}

		@Override
		public void updateFor_BoundSucess() {
		}

		@Override
		public void updateFor_BoundContinue() {
		}

		@Override
		public void updateFor_BoundFail() {
		}

		@Override
		public void updateFor_notify() {
		}

		@Override
		public void updateFor_notifyForSetClockSucess() {
		}

		@Override
		public void updateFor_notifyForLongSitSucess() {
		}

		@Override
		public void updateFor_notifyForSetHandUpSucess() {
		}

		@Override
		public void updateFor_notifyForSetBodySucess() {
		}

		@Override
		public void notifyForSetNOTIFYFail() {
		}

		@Override
		public void updateFor_notifyForSetClockFail() {
		}

		@Override
		public void updateFor_notifyForLongSitFail() {
		}

		@Override
		public void updateFor_notifyForSetHandUpFail() {
		}

		@Override
		public void updateFor_notifyForSetBodyFail() {
		}

		@Override
		public void updateFor_notifyForSetStepSucess() {
		}

		@Override
		public void updateFor_notifyForSetStepFail() {
		}

		@Override
		public void updateFor_notifyForSetPowerSucess() {
		}

		@Override
		public void updateFor_notifyForSetPowerFail() {
		}

		@Override
		public void updateFor_notifyForSetNameSucess() {
		}

		@Override
		public void updateFor_notifyForSetNameFail() {
		}

		@Override
		public void updateFor_OpenSmc(boolean isSuccess) {
		}

		@Override
		public void updateFor_AIDSmc(boolean isSuccess) {
		}

		@Override
		public void notifyForAIDStep1(boolean isSuccess) {
		}

		@Override
		public void notifyForAIDStep2(boolean isSuccess) {
		}

		@Override
		public void updateFor_checkPINSucess_D() {
		}

		@Override
		public void updateFor_checkPINFaild_D() {
		}

		@Override
		public void updateFor_GetSmcBalance(Integer obj) {
		}

		@Override
		public void updateFor_GetSmcTradeRecord(List<LLTradeRecord> list) {
		}

		@Override
		public void updateFor_GetSmcTradeRecordAsync(LLTradeRecord record) {
		}

		@Override
		public void updateFor_GetSchoolID(String obj) {
		}

		@Override
		public void updateFor_GetXJTradeRecord(List<LLXianJinCard> list) {
		}

		@Override
		public void updateFor_GetXJTradeRecordAsync(LLXianJinCard record) {
		}

		@Override
		public void updateFor_BoundNoCharge() {
		}

		@Override
		public void updateFor_response_ble(byte[] obj) {
		}

		@Override
		public void updateFor_response_ble_card(byte[] obj) {
		}

		@Override
		public void updateFor_getDeviceId(String obj) {
			
		}

		@Override
		public void notifyFor_close7816card(boolean isSuccess) {
			
		}

		@Override
		public void notifyFor_open7816card(boolean isSuccess) {
			
		}
		
		

	}

	public BLEHandler(Context context) {
		mContext = context;
	}

	protected abstract BLEProvider getProvider();

	public IBLEProviderObserver getBleProviderObserver() {
		return bleProviderObserver;
	}

	public void setBleProviderObserver(IBLEProviderObserver bleProviderObserver) {
		if (bleProviderObserver == null) {
			OwnLog.e(TAG,"===============传入的bleProviderObserver为null哦===============");
		} else {
			this.bleProviderObserver = bleProviderObserver;
			OwnLog.i(TAG,"===============传入的bleProviderObserver不是null===============");
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public LPDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(LPDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	/***/
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case BLEProvider.MSG_BLE_CONNECT_FAILED:
			handleConnectFailedMsg();
			break;
		case BLEProvider.MSG_BLE_CONNECT_LOST:
			handleConnectLostMsg();
			break;
		case BLEProvider.MSG_BLE_CONNECT_SUCCESS:
			handleConnectSuccessMsg();
			break;
		case BLEProvider.MSG_BLE_NOT_CONNECT:
			handleHaveNotConnectMsg();
			break;
		case BLEProvider.MSG_BLE_NOT_ENABLED:
			handleNotEnableMsg();
			break;
		case BLEProvider.MSG_BLE_NOTSUPPORT:
			handleNotSupportMsg();
			break;
		case BLEProvider.MSG_BLE_SCAN_TIME_OUT:
			handleScanTimeOutMsg();
			break;
		case BLEProvider.MSG_DATA_ERROR:
			handleSendDataError();
			break;
		case BLEProvider.MSG_BLE_DATA:
			handleData(msg.obj, msg.arg1);
			break;
		case BLEProvider.MSG_USER_ERROR:
			handleUserErrorMsg(((Integer) msg.obj).intValue());
			break;
		case BLEProvider.MSG_BOUND_DEVICE_FAILED:
			handleBoundDeviceFailed();
			break;
		case BLEProvider.MSG_BLE_CONNECTING:
			handleConnectingMsg();
			break;
		case BLEProvider.MSG_BLE_RSSI:
			handleRssiMsg(((Integer) msg.obj).intValue());
			break;
		case BLEProvider.MSG_BLE_DEVICE:
			handleDeviceMsg((BluetoothDevice) msg.obj);
			break;
		// 本轮运动数据完全读取完成的消息（此消息不带任何数据，仅用于通知）
		case BLEProvider.MSG_BLE_DATA_END:
			handleDataEnd();
			break;
		case BLEProvider.MSG_BLE_CONNECTED:
			handleConnectSuccessMsg();
			break;
		case BLEProvider.INDEX_SEND_OAD_HEAD:
			notifyForDeviceCanOAD_D();
			break;
		case BLEProvider.INDEX_SEND_OAD_HEAD_BACK:
			notifyForDeviceOADBack((byte[]) msg.obj);
			break;
		case BLEProvider.INDEX_SEND_OAD_HEAD_FAIL:
			notifyForDeviceNOTCanOAD_D();
			break;
		case BLEProvider.INDEX_SEND_OAD:
			notifyForDeviceOADSuccess_D();
			break;
		case BLEProvider.INDEX_SEND_OAD_BACK:
			notifyForDeviceOADback((byte[]) msg.obj);
			break;
		case BLEProvider.INDEX_OPEN_SMC:
			notifyForOpenSmc(msg.obj);
			break;
		case BLEProvider.INDEX_AID_SMC:
			notifyForAIDSmc(msg.obj);
			break;
		case BLEProvider.INDEX_AID_SMC_YC1:
			notifyForAIDStep1(msg.obj);
			break;
		case BLEProvider.INDEX_AID_SMC_YC2:
			notifyForAIDStep2(msg.obj);
			break;
		case BLEProvider.INDEX_GET_SMC_BALANCE:
			notifyForGetSmcBalance(msg.obj);
			break;
		case BLEProvider.INDEX_SCHOOL_ID:
			notifyForGetSchoolID(msg.obj);
			break;
		case BLEProvider.MSG_GET_SMC_TRADE_RECORD:
			notifyForGetSmcTradeRecord(msg.obj);
			break;
		case BLEProvider.MSG_GET_SMC_TRADE_RECORD_ASYNC:
			notifyForGetSmcTradeRecordAsync(msg.obj);
			break;
		case BLEProvider.INDEX_XIANJIN_RECORD:
			notifyForGetXJTradeRecord(msg.obj);
			break;
		case BLEProvider.MSG_GET__XIANJIN_TRADE_RECORD_ASYNC:
			notifyForGetXJTradeRecordAsync(msg.obj);
			break;

		case BLEProvider.INDEX_SET_DEVICE_TIME:
			notifyForSetDeviceTimeSucess();
			break;

		default:
			break;
		}
	}

	private void handleData(Object obj, int typeIndex) {
		if (typeIndex == BLEProvider.INDEX_UNBOUND_DEVICE) {
			// 收到此指令的反馈即意味着设备解除绑定成功
			if (((Boolean) obj).booleanValue()) {
				OwnLog.d(TAG, "手环解绑成功！");
				notifyForDeviceUnboundSucess_D();
			} else {
				OwnLog.d(TAG, "手环解绑失败！");
				notifyForDeviceUnboundFaild_D();
			}
		} else if (typeIndex == BLEProvider.INDEX_SEND_OAD_HEAD) {

			if (obj instanceof Integer) {
				// 这里执行判断
				if ((Integer) obj == BLEProvider.INDEX_SEND_OAD_HEAD) {
					// 当判断为可以继续时
					notifyForDeviceCanOAD_D();
				} else if ((Integer) obj == BLEProvider.INDEX_SEND_OAD_HEAD_FAIL) {
					OwnLog.d(TAG, "更新OAD失败！");
					notifyForDeviceNOTCanOAD_D();
				}
			}
		} else if (typeIndex == BLEProvider.INDEX_SEND_OAD) {
			if (obj instanceof Integer) {
				// 这里执行判断
				if ((Integer) obj == BLEProvider.INDEX_SEND_OAD) {
					// 当判断为可以继续时
					notifyForDeviceOADSuccess_D();
				} else if ((Integer) obj == BLEProvider.INDEX_SEND_OAD_HEAD_FAIL) {
					OwnLog.d(TAG, "更新OAD过程中失败！");
					notifyForDeviceNOTCanOAD_D();
				}
			}
		} else if (typeIndex == BLEProvider.INDEX_REQUEST_BOUND) {
			if (obj instanceof Integer) {
				if ((Integer) obj == BLEProvider.INDEX_BOUND_SUCCESS) {
					notifyForBoundSucess();
				} else if ((Integer) obj == BLEProvider.INDEX_BOUND_FAIL) {
					notifyForBoundFail();
				} else if ((Integer) obj == BLEProvider.INDEX_BOUND_GOON) {
					notifyForBoundContinue();
				} else if ((Integer) obj == BLEProvider.INDEX_BOUND_NOCHARGE) {
					notifyForBoundNoCharge();
				}
			}
		} else if (typeIndex == BLEProvider.INDEX_GET_CARD_NUMBER) {
			if (obj instanceof String) {
				String card = (String) obj;
				card = card.substring(0, 4);
				if (card.equals(BLEProvider.INDEX_GET_CARD_NUMBER_FAIL + "")) {
					notifyForCardNumberfail();
				} else {
					notifyForCardNumber((String) obj);
				}

			}
			;
		} else if (typeIndex == BLEProvider.MSG_BLE_SPORT_DATA_PROESS) {
			OwnLog.i(TAG, "剩余指令条数...");
			if (obj instanceof Integer) {
				notifyForSportDataProcess((Integer) obj);
			}
		}
		// 收到此指令的反馈即意味着设备配置信息同步（设备）成功
		else if (typeIndex == BLEProvider.INDEX_SETTING_ALL_DEVICE_INFO) {
			notifyForDeviceAloneSyncSucess_D();
		}
		// 收到此反馈即意味着执行0x13指令成功
		else if (typeIndex == BLEProvider.INDEX_GAT_ALL_INFO_NEW) {
			OwnLog.i(TAG, ".................0x13 0x13.................");
			notifyFor0x13ExecSucess_D((LPDeviceInfo) obj);
		}
		// 收到此反馈即意味着执行0x13指令成功
		else if (typeIndex == BLEProvider.INDEX_GET_MODEL) {
			notifyForModelName((LPDeviceInfo) obj);
		}
		// 收到此反馈即意味着单独执行获取设备明细运动数据成功
		else if (typeIndex == BLEProvider.INDEX_SYNC_SPORT_DATA_NEW) {
			notifyForFullSyncGetSportDetailDatasSucess_D((List<LPSportData>) obj);
		} else if (typeIndex == BLEProvider.INDEX_SET_DEVICEID_NEW) {
			notifyForSetDeviceIdSucess();
		} else if (typeIndex == BLEProvider.INDEX_SEND_FLASH_HEAD) {
			notifyForSendFlashSuc();
		}
		// 时间设置成功
		else if (typeIndex == BLEProvider.INDEX_SET_DEVICE_TIME) {
			notifyForSetDeviceTimeSucess();
		}
		// 时间设置失败
		else if (typeIndex == BLEProvider.INDEX_SET_DEVICE_TIME_FAIL) {
			OwnLog.i(TAG, "时间设置失败");
			notifyForSetDeviceTimeFail();
		} else if (typeIndex == BLEProvider.INDEX_SEND_NOTIFICATION) {
			OwnLog.i(TAG, "消息提醒设置成功");
			notifyForSetNOTIFYSucess();
		} else if (typeIndex == BLEProvider.INDEX_SEND_NOTIFICATION_FAIL) {
			OwnLog.i(TAG, "消息提醒设置失败");
			notifyForSetNOTIFYFail();
		} else if (typeIndex == BLEProvider.INDEX_SET_DEVICE_CLOCK) {
			OwnLog.i(TAG, "闹钟提醒设置成功");
			notifyForSetClockSucess();
		} else if (typeIndex == BLEProvider.INDEX_SET_DEVICE_CLOCK_FAIL) {
			OwnLog.i(TAG, "闹钟提醒设置失败");
			notifyForSetClockFail();
		} else if (typeIndex == BLEProvider.INDEX_SET_DEVICE_LONGSIT) {
			OwnLog.i(TAG, "久坐提醒设置成功");
			notifyForLongSitSucess();
		} else if (typeIndex == BLEProvider.INDEX_SET_DEVICE_LONGSIT_FAIL) {
			OwnLog.i(TAG, "久坐提醒设置失败");
			notifyForLongSitFail();
		} else if (typeIndex == BLEProvider.INDEX_SET_HAND_UP) {
			OwnLog.i(TAG, "抬手设置成功");
			notifyForSetHandUpSucess();
		} else if (typeIndex == BLEProvider.INDEX_SET_HAND_UP_FAIL) {
			OwnLog.i(TAG, "抬手设置fail");
			notifyForSetHandUpFail();
		} 
		else if (typeIndex == BLEProvider.INDEX_REGIESTER_INFO_NEW) 
		{
			OwnLog.i(TAG, "身体信息设置成功");
			notifyForSetBodySucess();
		} 
		else if (typeIndex == BLEProvider.INDEX_REGIESTER_INFO_NEW_FAIL) 
		{
			OwnLog.i(TAG, "身体信息设置fail");
			notifyForSetBodyFail();
		} 
		else if (typeIndex == BLEProvider.INDEX_SEND_STEP) 
		{
			OwnLog.i(TAG, "重置步数设置成功");
			notifyForSetStepSucess();
		} 
		else if (typeIndex == BLEProvider.INDEX_SEND_STEP_FAIL) 
		{
			OwnLog.i(TAG, "重置步数设置失败");
			notifyForSetStepFail();
		} 
		else if (typeIndex == BLEProvider.INDEX_POWER) 
		{
			OwnLog.i(TAG, "省电模式设置成功");
			notifyForSetPowerSucess();
		} 
		else if (typeIndex == BLEProvider.INDEX_POWER_FAIL) 
		{
			OwnLog.i(TAG, "省电模式设置失败");
			notifyForSetPowerFail();
		} 
		else if (typeIndex == BLEProvider.INDEX_SET_NAME) 
		{
			OwnLog.i(TAG, "名称设置成功");
			notifyForSetNameSucess();
		} 
		else if (typeIndex == BLEProvider.INDEX_SET_NAME_FAIL) 
		{
			OwnLog.i(TAG, "名称设置失败");
			notifyForSetNameFail();
		} 
		else if (typeIndex == BLEProvider.INDEX_SET_SMC_TRANSFER) 
		{
			if (((Boolean) obj).booleanValue()) {
				// pin验证成功
				notifyForcheckpinSucess_D();
			} else {
				// pin验证失败
				nnotifyForcheckpinFaild_D();
			}
		} 
		else if (typeIndex == BLEProvider.INDEX_SEND_DATA) 
		{
			notifyFor_response_ble((byte[]) obj);
		} 
		else if (typeIndex == BLEProvider.INDEX_SEND_DATA_CARD) 
		{
			notifyFor_response_ble_card(LPUtil.trans((byte[]) obj));
		} 
		else if (typeIndex == BLEProvider.INDEX_GET_DEVICEID) 
		{
			if (obj instanceof String) {
				// pin验证成功
				notifyForgetDeviceId_D((String) obj);
			}
		}
		else if (typeIndex == BLEProvider.INDEX_ClOSE_FEIJIE) 
		{
			notifyForClose7816card((Boolean) obj);
		}
		else if (typeIndex == BLEProvider.INDEX_OPEN_FEIJIE) 
		{
			notifyForOpen7816card((Boolean) obj);
		} 
		else 
		{
			// TODO
			OwnLog.w(TAG, "未知的指令反馈！！！！！！！！！！！！！！！！" + typeIndex);
		}
	}

	/**
	 * 本轮运动数据完全读取完成的消息（此消息不带任何数据，仅用于通知）。
	 */
	protected void handleDataEnd() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleDataEnd();
	}

	private void handleRssiMsg(int rssi) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForRssi_D(rssi);
	}

	protected void handleDeviceMsg(BluetoothDevice bluetoothDevice) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForBLEDevice_D(bluetoothDevice);
	}

	protected void handleConnectFailedMsg() {
		OwnLog.e(TAG, "连接失败！！！！！！！！！！！！！！！！");
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleConnectFailedMsg();
	}

	protected void handleConnectLostMsg() {
		OwnLog.e(TAG, "连接断开！！！！！！！！！！！！！！！");

		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleConnectLostMsg();
	}

	protected void handleConnectSuccessMsg() {
		// Toast.makeText(mContext, "连接成功！！！！！", Toast.LENGTH_LONG).show();
		OwnLog.i(TAG, "---连接成功---");
		if (getProvider() != null)
			getProvider().runProessAgain(); // 清空线程池

		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleConnectSuccessMsg();
	}

	private void handleHaveNotConnectMsg() {
		// Toast.makeText(mContext, "未连接！！！！！", Toast.LENGTH_LONG).show();
		OwnLog.e(TAG, "未连接！！！！！！！！！！！！！！");
		getProvider().scanForConnnecteAndDiscovery();// this;
		// mProvider.reConnect();

		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleHaveNotConnectMsg();
	}

	// protected void handleNotScanMsg()
	// {
	// Toast.makeText(mContext, "未扫描！！！！！", Toast.LENGTH_LONG).show();
	// OwnLog.e(TAG, "未扫描！！！！！！！！！！！！！！");
	// mProvider.scan();
	// }

	protected void handleNotEnableMsg() {
		// Intent enableBtIntent = new
		// Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		// if(mContext != null)
		// ((Activity)mContext).startActivityForResult(enableBtIntent,
		// REQUEST_ENABLE_BT);111222

		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleNotEnableMsg();
	}

	private void handleNotSupportMsg() {
		// Toast.makeText(mContext, "设备不支持BLE！！！！！", Toast.LENGTH_LONG).show();
		OwnLog.e(TAG, "设备不支持BLE！！！！！");
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleNotSupportMsg();
	}

	private void handleScanTimeOutMsg() {
		// Toast.makeText(mContext, "扫描超时！！！！", Toast.LENGTH_LONG).show();

		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleScanTimeOutMsg();
	}

	protected void handleSendDataError() {
		OwnLog.e(TAG, "发送指令失败！！！！");

		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleSendDataError();
	}

	private void handleUserErrorMsg(int id) {
		OwnLog.e(TAG, "用户ID不同！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
		// mProvider.connectNextDevice();

		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleUserErrorMsg(id);
	}

	private void handleBoundDeviceFailed() {
		// Toast.makeText(mContext, "绑定设备失败！！！！！！！！！！！！！！！！！！！！！",
		// Toast.LENGTH_LONG).show();
		OwnLog.e(TAG, "绑定设备失败！！！！！！！！！！！！！！！！！！！！！！！");

		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleBoundDeviceFailed();
	}

	private void handleConnectingMsg() {
		OwnLog.e(TAG, "正在连接！！！！！！！！！！！！！！！！！！！！！！！");

		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleConnecting();
	}

	private void notifyForSetDeviceIdSucess() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_SetDeviceIdSucess();
	}

	private void notifyForGetDeviceIdSucess(String id) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_GetDeviceIdSucess(id);
	}

	public void notifyForCardNumberfail() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_CardNumber_fail();

	}

	public void notifyForCardNumber(String obj) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_CardNumber((String) obj);
	}

	// 绑定成功
	private void notifyForBoundSucess() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_BoundSucess();
	}

	// 绑定失败
	private void notifyForBoundFail() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_BoundFail();
	}

	// 继续绑定
	private void notifyForBoundContinue() {
		OwnLog.i(TAG, "notifyForBoundContinue");
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_BoundContinue();
	}

	// 没充电
	private void notifyForBoundNoCharge() {
		OwnLog.i(TAG, "notifyForBoundContinue");
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_BoundNoCharge();
	}

	// 没充电
	private void notifyForSportDataProcess(Object object) {
		OwnLog.i(TAG, "notifyForSportDataProcess");
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_SportDataProcess((Integer) object);
	}

	private void notifyForSendFlashSuc() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_FlashHeadSucess();
	}

	/**
	 * 0x13指令执行成功后会调用此方法（应），以便通知上层应用刷新ui等.
	 * 
	 * @param latestDeviceInfo
	 *            最新设备信息对象
	 */
	protected void notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
		OwnLog.i(TAG, "0x13bleProviderObserver:" + bleProviderObserver == null ? "null" : "not null");
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyFor0x13ExecSucess_D(latestDeviceInfo);
	}

	/**
	 * 获取modelName
	 * 
	 * @param latestDeviceInfo
	 *            最新设备信息对象
	 */
	protected void notifyForModelName(LPDeviceInfo latestDeviceInfo) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForModelName(latestDeviceInfo);
	}

	protected void notifyForSetNOTIFYSucess() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notify();
	}

	protected void notifyForSetNOTIFYFail() {
		if (bleProviderObserver != null)
			bleProviderObserver.notifyForSetNOTIFYFail();
	}

	protected void notifyForSetClockSucess() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetClockSucess();
	}

	protected void notifyForSetClockFail() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetClockFail();
	}

	protected void notifyForLongSitSucess() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForLongSitSucess();
	}

	protected void notifyForLongSitFail() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForLongSitFail();
	}

	protected void notifyForSetHandUpSucess() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetHandUpSucess();
	}

	protected void notifyForSetHandUpFail() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetHandUpFail();
	}

	protected void notifyForSetBodySucess() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetBodySucess();
	}

	protected void notifyForSetBodyFail() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetBodyFail();
	}

	protected void notifyForSetDeviceTimeSucess() {
		if (bleProviderObserver != null) {
			OwnLog.d(TAG,
					"notifyForSetDeviceTimeSucess  bleProviderObserver != null");
			bleProviderObserver.updateFor_handleSetTime();
		}

	}

	protected void notifyForSetDeviceTimeFail() {
		OwnLog.e(TAG, "notifyForSetDeviceTimeFail");
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_handleSetTimeFail();
	}

	protected void notifyForSetStepSucess() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetStepSucess();
	}

	protected void notifyForSetStepFail() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetStepFail();
	}

	protected void notifyForSetPowerSucess() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetPowerSucess();
	}

	protected void notifyForSetPowerFail() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetPowerFail();
	}

	protected void notifyForSetNameSucess() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetNameSucess();
	}

	protected void notifyForSetNameFail() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForSetNameFail();
	}

	/**
	 * 单独执行设备同步指令成功后会调用此方法（应），以便通知上层应用刷新ui等.
	 */
	private void notifyForDeviceAloneSyncSucess_D() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForDeviceAloneSyncSucess_D();
	}

	/**
	 * 全流程（设备信息）同步成功后且读取到运动明细数据时会调用此方法，以便通知上层应用刷新ui等.
	 * 
	 * @param sportDetailDatas
	 *            全流程同步成功后返回的运动明细数据集合对象
	 * @deprecated 2014-08-07日起，为了保证数据保存的可靠性，使用的是同步保存方式，本异步方式随之被废弃！
	 */
	private void notifyForFullSyncGetSportDetailDatasSucess_D(
			List<LPSportData> sportDetailDatas) {
		if (bleProviderObserver != null)
			bleProviderObserver
					.updateFor_notifyForFullSyncGetSportDetailDatasSucess_D(sportDetailDatas);
	}

	/**
	 * 收到获取设备id数据反馈时会调用此方法，以便通知上层应用刷新ui等.
	 * 
	 * @param id
	 */
	protected void notifyForFullSyncGetDeviceIDSucess_D(String id) {
		if (bleProviderObserver != null)
			bleProviderObserver
					.updateFor_notifyForFullSyncGetDeviceIDSucess_D(id);
	}

	/**
	 * 全流程（设备信息）同步成功后会调用此方法，以便通知上层应用刷新ui等.
	 * 
	 * @param deviceInfo
	 *            同步成功后返回的最新设备信息对象
	 */
	protected void notifyForDeviceFullSyncSucess_D(LPDeviceInfo deviceInfo) {
		if (bleProviderObserver != null)
			bleProviderObserver
					.updateFor_notifyForDeviceFullSyncSucess_D(deviceInfo);
	}

	/**
	 * 设备解绑成功后，将会调用此方法，以便通知上层应用刷新ui等.
	 */
	protected void notifyForDeviceUnboundSucess_D() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForDeviceUnboundSucess_D();
	}

	/**
	 * 设备解绑失败后，将会调用此方法，以便通知上层应用刷新ui等.
	 */
	private void notifyForDeviceUnboundFaild_D() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyForDeviceUnboundFaild_D();
	}

	/**
	 * 当可以继续OAD后，将会调用此方法，以便通知上层应用等.
	 */
	private void notifyForDeviceCanOAD_D() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyCanOAD_D();
	}

	/**
	 */
	private void notifyForDeviceOADBack(byte[] data) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyOADheadback(data);
	}

	/**
	 * 当不可以继续OAD后，将会调用此方法，以便通知上层应用等.
	 */
	private void notifyForDeviceNOTCanOAD_D() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyNOTCanOAD_D();
	}

	/**
	 * 当OAD完成后，将会调用此方法，以便通知上层应用等.
	 */
	private void notifyForDeviceOADSuccess_D() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyOADSuccess_D();
	}

	/**
	 * 当OAD完成后，将会调用此方法，以便通知上层应用等.
	 */
	private void notifyForDeviceOADback(byte[] data) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_notifyOADback(data);
	}

	/******************* 卡指令 ********************/
	protected void notifyForOpenSmc(Object obj) {
		if (bleProviderObserver != null) {
			bleProviderObserver.updateFor_OpenSmc((Boolean) obj);
		} else {
		}

	}

	protected void notifyForAIDSmc(Object obj) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_AIDSmc((Boolean) obj);
	}

	private void notifyForAIDStep1(Object obj) {
		if (bleProviderObserver != null)
			bleProviderObserver.notifyForAIDStep1((Boolean) obj);
	}

	private void notifyForAIDStep2(Object obj) {
		if (bleProviderObserver != null)
			bleProviderObserver.notifyForAIDStep2((Boolean) obj);
	}

	protected void notifyForcheckpinSucess_D() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_checkPINSucess_D();
	}

	protected void nnotifyForcheckpinFaild_D() {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_checkPINFaild_D();
	}

	private void notifyForGetSmcBalance(Object obj) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_GetSmcBalance((Integer) obj);
	}

	protected void notifyForGetSchoolID(Object obj) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_GetSchoolID((String) obj);
	}

	private void notifyForGetSmcTradeRecord(Object obj) {
		if (bleProviderObserver != null)
			bleProviderObserver
					.updateFor_GetSmcTradeRecord((List<LLTradeRecord>) obj);
	}

	private void notifyForGetSmcTradeRecordAsync(Object obj) {
		if (bleProviderObserver != null)
			bleProviderObserver
					.updateFor_GetSmcTradeRecordAsync((LLTradeRecord) obj);
	}

	private void notifyForGetXJTradeRecord(Object obj) {
		if (bleProviderObserver != null)
			bleProviderObserver
					.updateFor_GetXJTradeRecord((List<LLXianJinCard>) obj);
	}

	private void notifyForGetXJTradeRecordAsync(Object obj) {
		if (bleProviderObserver != null)
			bleProviderObserver
					.updateFor_GetXJTradeRecordAsync((LLXianJinCard) obj);
	}

	protected void notifyFor_response_ble(byte[] data) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_response_ble(data);
	}

	protected void notifyFor_response_ble_card(byte[] data) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_response_ble_card(data);
	}

	protected void notifyForgetDeviceId_D(String obj) {
		if (bleProviderObserver != null)
			bleProviderObserver.updateFor_getDeviceId(obj);
	}
	
	protected void notifyForClose7816card(boolean success) {
		if (bleProviderObserver != null)
			bleProviderObserver.notifyFor_close7816card(success);
	}
	
	protected void notifyForOpen7816card(boolean success) {
		if (bleProviderObserver != null)
			bleProviderObserver.notifyFor_open7816card(success);
	}
	
	

}
