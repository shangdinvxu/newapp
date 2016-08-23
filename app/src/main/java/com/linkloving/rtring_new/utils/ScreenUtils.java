package com.linkloving.rtring_new.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jaeger.library.StatusBarUtil;

//获得屏幕相关的辅助类
public class ScreenUtils
{
	private ScreenUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 获得状态栏的高度
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context)
	{

		int statusHeight = -1;
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return statusHeight;
	}

	/**
	 * 获取当前屏幕截图，包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * Apply KitKat specific translucency.
	 */
	public static void applyKitKatTranslucency(Activity activity) {
		// KitKat translucent navigation/status bar.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(true,activity);
			StatusBarUtil.setTransparent(activity);
//			SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
//			mTintManager.setStatusBarTintEnabled(true);
//			mTintManager.setNavigationBarTintEnabled(true);
////			mTintManager.setTintColor(R.color.progress_color);
//			mTintManager.setStatusBarTintColor(activity.getResources().getColor(R.color.transparent));
//			mTintManager.setTintDrawable(UIElementsHelper.getGeneralActionBarBackground(this));

		}

	}

	@TargetApi(19)
	private static void setTranslucentStatus(boolean on,Activity activity) {
		Window win = activity.getWindow();
//		WindowManager.LayoutParams winParams = win.getAttributes();
//		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//		win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		if (on) {
//			winParams.flags |= bits;
//		} else {
//			winParams.flags &= ~bits;
//		}
//		win.setAttributes(winParams);
		// 透明状态栏
		win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	}

	public static void setMargins (View v, int l, int t, int r, int b) {
		if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			p.setMargins(l, t, r, b);
			v.requestLayout();
		}
	}

	public static Dimension getAreaOne(Activity activity){
		Dimension dimen = new Dimension();
		Display disp = activity.getWindowManager().getDefaultDisplay();
		Point outP = new Point();
		disp.getSize(outP);
		dimen.mWidth = outP.x ;
		dimen.mHeight = outP.y;
		return dimen;
	}
	public static Dimension getAreaTwo(Activity activity){
		Dimension dimen = new Dimension();
		Rect outRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
		System.out.println("top:"+outRect.top +" ; left: "+outRect.left) ;
		dimen.mWidth = outRect.width() ;
		dimen.mHeight = outRect.height();
		return dimen;
	}
	public static Dimension getAreaThree(Activity activity){
		Dimension dimen = new Dimension();
		// 用户绘制区域
		Rect outRect = new Rect();
		activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
		dimen.mWidth = outRect.width() ;
		dimen.mHeight = outRect.height();
		// end
		return dimen;
	}

	public static class Dimension {
		public int mWidth ;
		public int mHeight ;
		public Dimension(){}
	}

}
