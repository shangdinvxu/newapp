package com.linkloving.rtring_new.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class ObservableHorizontalScrollView extends HorizontalScrollView
{
	private Runnable scrollerTask;
	private int intitPosition;
	private int newCheck = 50;
	private OnScrollStopListner onScrollstopListner;
	private Context context;

	public ObservableHorizontalScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		scrollerTask = new Runnable()
		{
			@Override
			public void run()
			{
				// 获取Scroll 在 X轴的位置
				int newPosition = getScrollX();
				if (intitPosition - newPosition == 0)
				{
					if (onScrollstopListner == null)
					{
						return;
					}
					onScrollstopListner.onScrollChange(getScrollX());
				}
				else
				{
					intitPosition = getScrollX();
					postDelayed(scrollerTask, newCheck);
				}
			}
		};
	}

	public ObservableHorizontalScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public ObservableHorizontalScrollView(Context context)
	{
		super(context);
		this.context = context;

	}

	public interface OnScrollStopListner
	{
		void onScrollChange(int index);
	}



	public void setOnScrollStopListner(OnScrollStopListner listner)
	{
		onScrollstopListner = listner;
	}

	public void startScrollerTask()
	{
		intitPosition = getScrollX();
		postDelayed(scrollerTask, newCheck);
	}

	public int px2dip(float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		// 这是获取手机屏幕参数，后面的density就是屏幕的密度
		return (int) (pxValue / scale + 0.5f);
	}

	public int int2int(int getviewtext)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) ((getviewtext - 0.5f) * scale);

	}
}
