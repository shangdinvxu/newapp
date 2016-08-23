package com.example.android.bluetoothlegatt.utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public final class LogX extends Thread
{
	public static final String DEFAULT_LOG_PATH =  Environment.getExternalStorageDirectory() + "com.salelife.store/Log/";
	private static final String TAG = "===LogX===";
	public static final String FILENAME = "log.txt";
	private static final long MAXLOGSIZE = 3145728L;
	private static LogX instance;
	private String logPath = DEFAULT_LOG_PATH;
	private Queue<String> lstStorageTask;
	private boolean isRunnig;
	private RandomAccessFile randomAccessFile = null;

	private File file = null;

	public static boolean infoFlag = false;
	
	// true表示启用日志记录，否则停用
	public static boolean enabled = true;
	
	public static final SimpleDateFormat SDF_DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS
		= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");  

	private LogX()
	{
		this(DEFAULT_LOG_PATH);
	}

	private LogX(String path)
	{
		this.isRunnig = true;
		this.logPath = path;
		this.lstStorageTask = new LinkedList();
		openFile();
	}

	private LogX(boolean flag)
	{
		this.isRunnig = flag;
	}

	public static LogX getInstance()
	{
		LogX inst = null;
		if (infoFlag)
		{
			inst = getNewInstance();
		}
		else
		{
			inst = new LogX(infoFlag);
		}
		return inst;
	}

	public static LogX getNewInstance()
	{
		if (instance != null)
		{
			return instance;
		}

		synchronized (LogX.class)
		{
			if (instance == null)
			{
				instance = new LogX();
				if (enabled)//GlobalVariable.isAllowWriteLogFile)
				{
					instance.start();
				}
			}

			if (enabled
					//(GlobalVariable.isAllowWriteLogFile) 
					&& (!instance.isAlive()))
			{
				instance.interrupt();
				instance = new LogX();
				instance.start();
			}
		}

		return instance;
	}

	public void stopLog()
	{
		try
		{
			this.isRunnig = false;
			if (instance != null)
			{
				instance.interrupt();
			}
			closeFile();
		}
		catch (Exception e)
		{
			Log.i("===LogX===", "stop the write log thread.");
		}
	}

	public void i(String tag, String message)
	{
		message = formatMessage(message);
		Log.i(tag, message);
		trace(tag, message);
	}

	public void e(String tag, String message)
	{
		message = formatMessage(message);
		Log.e(tag, message);
		trace(tag, message);
	}

	public void d(String tag, String message)
	{
		message = formatMessage(message);
		Log.d(tag, message);
		trace(tag, message);
	}

	public void v(String tag, String message)
	{
		message = formatMessage(message);
		Log.v(tag, message);
		trace(tag, message);
	}

	public void w(String tag, String message)
	{
		message = formatMessage(message);
		Log.w(tag, message);
		trace(tag, message);
	}

	private String formatMessage(String message)
	{
		if (message == null)
		{
			message = "java.lang.NullPointerException.";
		}
		return message;
	}

	private void trace(String tag, String message)
	{
		if(enabled)
		{
			if (this.isRunnig)
			{
				synchronized (this.lstStorageTask)
				{
					if (needClearLogs())
					{
						deleteFile();
						closeFile();
						openFile();
					}

					this.lstStorageTask.add(tag + "---||  " + message + "\n");
					this.lstStorageTask.notify();
				}
			}
		}
	}

//	public void run()
//	{
//		while (this.isRunnig)
//		{
//			try
//			{
//				if (this.lstStorageTask == null)
//				{
//					Log.i("===LogX===", "In storage thread the lstStorageTask is null.");
//					break;
//				}
//
//				String dataBlock = null;
//				synchronized (this.lstStorageTask)
//				{
//					if (this.lstStorageTask.isEmpty())
//					{
//						this.lstStorageTask.wait();
//					}
//
//					if (!this.lstStorageTask.isEmpty())
//					{
//						dataBlock = (String)this.lstStorageTask.poll();
//					}
//
//				}
//
//				if (dataBlock == null)
//					continue;
//				writeFile(dataBlock.getBytes());
//			}
//			catch (InterruptedException e)
//			{
//				Log.i("===LogX===", "The write file thread is closed.");
//				this.isRunnig = false;
//				break;
//			}
//		}
//	}

	private void writeLogError()
	{
		this.isRunnig = false;

		closeFile();

		clearLogTaskList();
	}

	private void closeFile()
	{
		try
		{
			if (this.randomAccessFile != null)
			{
				this.randomAccessFile.close();
			}
		}
		catch (IOException e)
		{
			Log.e("===LogX===", e.getMessage());
		}
		finally
		{
			this.randomAccessFile = null;
		}
	}

	private void clearLogTaskList()
	{
		synchronized (this.lstStorageTask)
		{
			Iterator tasks = this.lstStorageTask.iterator();
			while (tasks.hasNext())
			{
				String task = (String)tasks.next();
				if (task == null) {
					continue;
				}
				task = null;
			}

			this.lstStorageTask.clear();
		}
	}

	private boolean needClearLogs()
	{
		return this.file.length() >= 3145728L;
	}

	private void openFile()
	{
		File dir = new File(this.logPath);
		if (!dir.exists())
		{
			dir.mkdirs();
		}

		createFile();
	}

	private void deleteFile()
	{
		if (this.file.exists())
		{
			try
			{
				if (this.file.delete())
				{
					Log.i("===LogX===", "Delete log file success");
				}
				else
				{
					Log.e("===LogX===", "Delete log file failed");
				}
			}
			catch (Exception e)
			{
				Log.e("===LogX===", e.getMessage());
			}
		}
	}

	private void createFile()
	{
		if (this.file == null)
		{
			this.file = new File(this.logPath + "log.txt");
		}

		if (!this.file.exists())
		{
			try
			{
				this.file.createNewFile();
				closeFile();
			}
			catch (IOException e)
			{
				Log.e("===LogX===", e.getMessage());
			}

			createRandomAccessFile();
		}
		else if (this.randomAccessFile == null)
		{
			createRandomAccessFile();
		}
	}

	private void createRandomAccessFile()
	{
		try
		{
			this.randomAccessFile = new RandomAccessFile(this.file, "rw");

			if (this.randomAccessFile == null)
			{
				Log.e("===LogX===", "initial LogX file error.");
			}
		}
		catch (Exception e)
		{
			closeFile();
			Log.e("===LogX===", e.getMessage());
		}
	}

	private void writeFile(byte[] io)
	{
		if (io != null)
		{
			if (this.randomAccessFile != null)
			{
				try
				{
					createFile();
					this.randomAccessFile.seek(this.randomAccessFile.length());
					this.randomAccessFile.write(io);
				}
				catch (IOException e)
				{
					Log.e("===LogX===", e.getMessage());
				} 
			}
			else
			{
				String state = Environment.getExternalStorageState();
				if ((!state.equals("mounted")) || (io.length >= getAvailableStore()))
				{
					writeLogError();
				}
			}
		}
	}
	
	public String getLogPath()
	{
		return logPath;
	}

	private long getAvailableStore()
	{
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory()
				.getAbsolutePath());

		long blocSize = statFs.getBlockSize();

		long availaBlock = statFs.getAvailableBlocks();

		long availableSpare = availaBlock * blocSize;

		return availableSpare;
	}
}