package com.example.android.bluetoothlegatt.proltrol;


import com.example.android.bluetoothlegatt.utils.OwnLog;

import java.io.UnsupportedEncodingException;

public class LPUtil {
	private static final String TAG = LPUtil.class.getSimpleName();
	
	
	// 将无符号byte数组转换为String，用逗号分隔
	public static String ubytesToString(byte[] data, int offset, int len) {
		StringBuilder sb = new StringBuilder();
		if (len > 0) {
			int p = offset;
			sb.append(data[p++] & 0xFF);
			for (int i = 1; i < len; ++i) {
				sb.append(',').append(data[p++] & 0xFF);
			}
		}
		return sb.toString();
	}

	// 将无符号byte数组转换为String，用逗号分隔，16进制表示
	public static String ubytesToString16(byte[] data, int offset, int len) {
		StringBuilder sb = new StringBuilder();
		if (len > 0) {
			int p = offset;
			sb.append(Integer.toHexString(data[p++] & 0xFF));
			for (int i = 1; i < len; ++i) {
				sb.append(',').append(Integer.toHexString(data[p++] & 0xFF));
			}
		}
		return sb.toString();
	}

	// 将short数组转换为String，用逗号分隔
	public static String shortsToString(short[] data, int offset, int len) {
		StringBuilder sb = new StringBuilder();
		if (len > 0) {
			int p = offset;
			sb.append(data[p++]);
			for (int i = 1; i < len; ++i) {
				sb.append(',').append(data[p++]);
			}
		}
		return sb.toString();
	}

	// 判断一个无符号byte和一个整数是否相等
	public static boolean unsignedEqual(byte b0, int val) {
		return (b0 & 0xFF) == val;
	}

	// 对无符号byte数组求和
	public static int accumulate(byte[] data, int offset, int len) {
		int res = 0;
		for (int i = offset; i < offset + len; ++i)
			res += (data[i] & 0xFF);
		return res;
	}

	public static int makeShort(byte b1, byte b2) {
		return (int) (((b1 & 0xFF) << 8) | (b2 & 0xFF));
	}

	public static int makeInt(byte b1, byte b2, byte b3, byte b4) {
		return (int) (((b1 & 0xFF) << 24) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 8) | (b4 & 0xFF));
	}

	public static long makeLong(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
		int high = makeInt(b1, b2, b3, b4);
		int low = makeInt(b5, b6, b7, b8);
		return ((long) high << 32) | (long) low;
	}
	
	public static String makeDeviceID(byte[] bytes)
	{
		if(bytes.length < 12)
		   return "";
		StringBuffer sb = new StringBuffer();
		for(byte b:bytes)
		{
			 sb.append(Integer.toHexString((b & 0xFF) | 0xFFFFFF00).substring(6).toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 32位int 0000xxxxxxxxxxxxxxxxxxxxxxxxxxxx 28位int
	 * 00xxxxxxxxxxxxxx00xxxxxxxxxxxxxx
	 */
	public static int parse32bitTo28bit(int _32bit) {
		return (((_32bit << 2) & 0x3fff0000) | (_32bit & 0x3fff));
	}

	/**
	 * 32位int 0000xxxxxxxxxxxxxxxxxxxxxxxxxxxx 28位int
	 * 00xxxxxxxxxxxxxx00xxxxxxxxxxxxxx
	 */
	public static int parse28bitTo32bit(int _28bit) {
		return ((_28bit >> 2) & 0xfffc000) | (_28bit & 0x3fff);
	}

	/**
	 * 将设备时间转换为标准24小时时间
	 */
	public static String deviceTime2String(int time) {
		return "" + (time / 120) + ":" + (time % 120 / 2);
	}

	/**
	 * 将设备时长转换为标准时长 xxx m xxx s
	 */
	public static String deviceDuration2String(int duration) {
		return "" + (duration / 2) + "m" + (duration % 2 * 30) + "s";
	}

	/**
	 * 检查checksum 合法性
	 * @param revData
	 * @return
	 */
	public static boolean checksum(byte[] revData) {
		int  sum = 0;
		for( int i = 0; i < revData.length-2; ++i )
			sum += ( revData[i] & 0xFF );
		if(sum ==LPUtil.makeShort(revData[revData.length-2],revData[revData.length-1]) )
			return true;
		return false;
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

//	/**
//	 * 因为手环存储的dayIndex，都是结束时间的dayIndex，有时候会有加1的误差 断电后再充电，手环的dayIndex固定为15
//	 * 需要修正dayIndex为起始时间的dayIndex，这样好做数据分析 特殊情况：1 开始dayIndex为1 （跨天） 2
//	 * 开始dayIndex为15 3 多次断电
//	 */
//	public static void fixSportData(long syncMillis, List<LPSportData> datas0) {
//		ArrayList<LPSportData> datas = (ArrayList<LPSportData>) datas0;
//
//		// 过滤前10条异常数据
//		int end = Math.min(datas.size(), 10);
//		for (int i = 0; i < end; ++i) {
//			LPSportData data = datas.get(i);
//			if (data.steps / (data.duration * 30) > 1000) {
//				datas.remove(i);
//				--i;
//				--end;
//			}
//		}
//
//		long zoneOffset = TimeZone.getTimeZone("Asia/Shanghai").getOffset(0);
//		long baseDayIndex = (syncMillis + zoneOffset) / 1000 / 30 / 2880;
//		long nowUnit = (System.currentTimeMillis() + zoneOffset) / 1000 / 30;
//
//		int pos1 = -1; // 从前向后推，找到的断电数据或不连续数据的起始点
//		int pos2 = -1; // 从后向前推，找到的第一个不连续的数据的开始
//		int tot = datas.size();
//		int firstDayIndex = -1;
//		if (tot > 0) {
//			firstDayIndex = datas.get(0).dayIndex;
//			if (15 == firstDayIndex)
//				pos1 = 0;
//			else {
//				for (int i = 1; i < tot; ++i) {
//					int day1 = datas.get(i - 1).dayIndex;
//					int day2 = datas.get(i).dayIndex;
//					int start1 = datas.get(i - 1).begin;
//					int start2 = datas.get(i).begin;
//					int duration1 = datas.get(i - 1).duration;
//					int duration2 = datas.get(i).duration;
//
//					if ((start1 + duration1) >= 2880 && (start1 + duration1) % 2880 == start2) {
//						if (day2 == day1) {
//							--day1;
//							datas.get(i - 1).dayIndex = day1;
//						}
//					}
//
//					if ((start2 + duration2) >= 2880 && (start1 + duration1) % 2880 == start2) {
//						if (day2 == day1 + 1) {
//							--day2;
//							datas.get(i).dayIndex = day2;
//						}
//					}
//
//					if (day1 * 2880 + start1 + duration1 != day2 * 2880 + start2) {
//						pos1 = i;
//						break;
//					}
//				}
//			}
//		}
//		if (pos1 >= 0) {
//
//			for (int i = tot - 1; i >= pos1; --i) {
//				pos2 = i;
//				if (datas.get(i).dayIndex != 15)
//					break;
//				int start1 = (i > 0 ? datas.get(i - 1).begin : 0);
//				int start2 = datas.get(i).begin;
//				int duration1 = (i > 0 ? datas.get(i - 1).duration : 0);
//				int duration2 = datas.get(i).duration;
//
//				nowUnit -= duration2;
//				datas.get(i).dayIndex = (int) (nowUnit / 2880 - baseDayIndex);
//				datas.get(i).begin = (int) (nowUnit % 2880);
//
//				if ((start1 + duration1) % 2880 != start2) {
//					break;
//				}
//			}
//
//			for (int i = pos1; i < pos2; ++i) {
//				datas.remove(pos1);
//			}
//		}
//
//		for (int i = 0; i < datas.size(); ++i) {
//			datas.get(i).dayIndex += baseDayIndex;
//		}
//	}
//
//	/**
//	 * 去除脏数据
//	 * 
//	 * @return
//	 */
//	public static ArrayList<LPSportData> filterOriginalData(List<LPSportData> lpSportDatas) {
//		if (lpSportDatas == null || lpSportDatas.size() == 0) {
//			return new ArrayList<LPSportData>();
//		}
//		Collections.sort(lpSportDatas, new LPSportDataComparator());
//		ArrayList<LPSportData> filteredDataList = new ArrayList<LPSportData>();
//		filteredDataList.add(lpSportDatas.get(0));
//		int dayIndex1 = lpSportDatas.get(0).getDayIndex();
//		int begin1 = lpSportDatas.get(0).getBegin();
//		int duration1 = lpSportDatas.get(0).getDuration();
//		int dayIndex2;
//		int begin2;
//		int duration2;
//
//		for (int i = 1; i < lpSportDatas.size(); i++) {
//			dayIndex2 = lpSportDatas.get(i).getDayIndex();
//			begin2 = lpSportDatas.get(i).getBegin();
//			duration2 = lpSportDatas.get(i).getDuration();
//			if (dayIndex1 * 2880 + begin1 + duration1 <= dayIndex2 * 2880 + begin2) {
//				filteredDataList.add(lpSportDatas.get(i));
//				dayIndex1 = dayIndex2;
//				begin1 = begin2;
//				duration1 = duration2;
//			}
//		}
//		return filteredDataList;
//	}
//
//	private static class LPSportDataComparator implements Comparator<LPSportData> {
//		@Override
//		public int compare(LPSportData data1, LPSportData data2) {
//			int result = 1;
//			if (data1.getDayIndex() < data2.getDayIndex()) {
//				result = -1;
//			} else if (data1.getDayIndex() == data2.getDayIndex()) {
//				if (data1.getBegin() < data2.getBegin()) {
//					result = -1;
//				} else if (data1.getBegin() == data2.getBegin()) {
//					result = 0;
//				}
//			}
//			return result;
//		}
//	}

//	public static void main(String[] args) {
//		List<LPSportData> datas = new ArrayList<LPSportData>();
//		datas.add(new LPSportData(1, true, LPSportData.STATE_IDLE, 2860, 10, 1000, 1000, 100, 100));
//		datas.add(new LPSportData(2, true, LPSportData.STATE_IDLE, 2870, 20, 1000, 1000, 100, 100));
//		datas.add(new LPSportData(15, true, LPSportData.STATE_IDLE, 10, 10, 1000, 1000, 100, 100));
//		datas.add(new LPSportData(15, true, LPSportData.STATE_IDLE, 20, 10, 1000, 1000, 100, 100));
//
//		long syncTime = System.currentTimeMillis() - 86400000 * 2;
//
//		LPUtil.fixSportData(syncTime, datas);
//
//		for (int i = 0; i < datas.size(); ++i) {
//			LPSportData data = datas.get(i);
//			System.out.println(data.dayIndex + " " + data.begin + " " + data.duration);
//		}
//	}

	public static byte[] stringtobyte(String orignal, int size)
			throws UnsupportedEncodingException {
		// 原始字符不为null，也不是空字符串
		if (orignal != null && !"".equals(orignal)) {
			// 将原始字符串转换为utf-8编码格式
			orignal = new String(orignal.getBytes(), "utf-8");
//	           // 要截取的字节数大于0，且小于原始字符串的字节数
			if (size > 0 && size < orignal.getBytes("utf-8").length) {
				char c;
				int k =0;
				for (int i = 0; i < orignal.length(); i++) {
					//遇到的是英文或者数字
					if(k+1>size){
						orignal=orignal.substring(0,i);
						System.out.println(orignal);
						break;
					}
					else
						k++;
				}
			}
		}
		return orignal.getBytes("utf-8");
	}

	public static byte[] intto2byte(int a) {
		byte[] m = new byte[2];
		m[0] = (byte) ((0xff & a));
		m[1] = (byte) (0xff & (a >> 8));
		return m;
	}

	public static void  printData(byte[] data, String label) {
		StringBuffer sb = new StringBuffer();
		sb.append(label + "[");
		for (int i = 0; i < data.length; i++) {
			String msg = Integer.toHexString((data[i] & 0xff));
			if(msg.length()<2){
				msg = "0"+msg;
			}
			msg = "0x"+msg.toUpperCase();
			sb.append(msg + " ");
		}
		sb.append("]");
		OwnLog.w(TAG, sb.toString());
	}

}
