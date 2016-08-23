package com.linkloving.rtring_new.logic.UI.personal.util;

/**
 * Created by leo.wang on 2016/4/6.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BitmapHelper
{
    private static final String TAG = BitmapHelper.class.getSimpleName();

    public static Bitmap decreaseBitmapSize(Bitmap originalBm, float decreaseToPercent, boolean recycleOriginalAfterProcess)
            throws OutOfMemoryError
    {
        return decreaseBitmapSize(originalBm, decreaseToPercent, recycleOriginalAfterProcess, null, false);
    }

    public static Bitmap decreaseBitmapSize(Bitmap originalBm, float decreaseToPercent, boolean recycleOriginalAfterProcess, Matrix matrix, boolean filter)
            throws OutOfMemoryError
    {
        Bitmap bmAfterProcessed = null;

        if (originalBm == null)
        {
            Log.e(TAG, "originalBm is null!");
            return null;
        }

        if ((decreaseToPercent < 0.0F) || (decreaseToPercent > 1.0F))
        {
            Log.e(TAG, "decreaseToPercent is < 0 or > 1, decreaseToPercent=" + decreaseToPercent);
            return null;
        }

        try
        {
            bmAfterProcessed = Bitmap.createBitmap(originalBm,
                    0, 0,
                    (int)(originalBm.getWidth() * decreaseToPercent),
                    (int)(originalBm.getHeight() * decreaseToPercent),
                    matrix, filter);
        }
        catch (OutOfMemoryError e)
        {
            if ((bmAfterProcessed != null) && (!bmAfterProcessed.isRecycled()))
                bmAfterProcessed.recycle();
            bmAfterProcessed = null;
            throw e;
        }
        finally
        {
            if ((recycleOriginalAfterProcess) && (originalBm != null) && (!originalBm.isRecycled())) {
                originalBm.recycle();
            }
        }

        return bmAfterProcessed;
    }

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri)
            throws OutOfMemoryError
    {
        return decodeUriAsBitmap(context, uri, null);
    }

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri, BitmapFactory.Options opts)
            throws OutOfMemoryError
    {
        Bitmap bitmap = null;
        try
        {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, opts);
        }
        catch (OutOfMemoryError e)
        {
            Log.e(TAG, "将图片decodeUriAsBitmap到内存时内存溢出了，执行没有继续！", e);
            throw e;
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, "将图片decodeUriAsBitmap时出错了，" + e.getMessage(), e);
            return null;
        }
        return bitmap;
    }

    public static Drawable loadHttpDrawable(String httpURL)
            throws Exception
    {
        try
        {
            return Drawable.createFromStream(new URL(httpURL).openStream(), "test");
        }
        catch (IOException e)
        {
            throw e;
        }

    }

    public static Drawable loadDrawble(String filePath)
    {
        return Drawable.createFromPath(filePath);
    }

    public static Bitmap loadLocalBitmap(String localUrl)
            throws Exception
    {
        return loadLocalBitmap(localUrl, null);
    }

    public static Bitmap loadLocalBitmap(String localUrl, BitmapFactory.Options opts)
            throws Exception
    {
        try
        {
            return BitmapFactory.decodeFile(localUrl, opts);
        }
        catch (Exception e)
        {
            throw e;
        }


    }

    public static Bitmap loadHttpBitmap(String httpURL)
            throws Exception
    {
        Bitmap bitmap = null;
        try
        {
            URL myFileUrl = new URL(httpURL);
            HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (MalformedURLException e)
        {
            throw e;
        }
        catch (IOException e)
        {
            throw e;
        }
        return bitmap;
    }

    public static boolean saveBitmapToFile(Bitmap bp, int qaulity, File outputDestFile)
            throws Exception
    {
        if ((bp == null) || (outputDestFile == null))
            return false;
        try
        {
            FileOutputStream outputStream = new FileOutputStream(outputDestFile);

            bp.compress(Bitmap.CompressFormat.JPEG,
                    qaulity,
                    outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        }
        catch (Exception e) {
            throw e;
        }

    }

    public static BitmapFactory.Options computeSampleSize2(String filePath, int reqWidth, int reqHeight)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try
        {
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);

            opts.inSampleSize = computeSampleSize2(opts, reqWidth, reqHeight);
        }
        catch (Exception e)
        {
            Log.e("computeSampleSize", "计算图片1的inSampleSize时出错.", e.getCause());
        }
        finally
        {
            opts.inJustDecodeBounds = false;
        }

        Log.d("computeSampleSize", ">> inSampleSize算法[2]计算完成，计算结果是【" + opts.inSampleSize + "】，reqWidth=" +
                reqWidth + ", reqHeight=" + reqHeight + ", filePath=" + filePath);

        return opts;
    }

    public static int computeSampleSize2(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        int height = options.outHeight;
        int width = options.outWidth;

        Log.d("computeSampleSize", ">> inSampleSize算法[2]计算中，[原始options.outWidth=" + options.outWidth +
                ", o原始ptions.outHeight=" + options.outHeight +
                "]，目标reqWidth=" + reqWidth + ", 目标reqHeight=" + reqHeight + ", options=" + options);

        int inSampleSize = 1;

        if ((height > reqHeight) || (width > reqWidth))
        {
            int heightRatio = Math.round(height / reqHeight);
            int widthRatio = Math.round(width / reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            float totalPixels = width * height;

            float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap)
            {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /** @deprecated */
    public static BitmapFactory.Options computeSampleSize(String filePath, int minSideLength, int maxNumOfPixels)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try
        {
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);

            opts.inSampleSize = computeSampleSize(opts, minSideLength, maxNumOfPixels);
        }
        catch (Exception e)
        {
            Log.e("computeSampleSize", "计算图片1的inSampleSize时出错.", e.getCause());
        }
        finally
        {
            opts.inJustDecodeBounds = false;
        }

        Log.d("computeSampleSize", ">> inSampleSize算法[1]计算完成，计算结果是【" + opts.inSampleSize + "】，minSideLength=" +
                minSideLength + ", maxNumOfPixels=" + maxNumOfPixels + ", filePath=" + filePath);

        return opts;
    }

    /** @deprecated */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels)
    {
        int inSampleSizeForRet = 1;
        try
        {
            int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

            if (initialSize <= 8)
            {
                inSampleSizeForRet = 1;
                while (inSampleSizeForRet < initialSize)
                    inSampleSizeForRet <<= 1;
            }
            else {
                inSampleSizeForRet = (initialSize + 7) / 8 * 8;
            }
        }
        catch (Exception e) {
            Log.e("computeSampleSize", "计算图片2的inSampleSize时出错.", e.getCause());
        }
        return inSampleSizeForRet;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        Log.d("computeSampleSize", ">> inSampleSize算法[1]计算中，[原始options.outWidth=" + options.outWidth +
                ", o原始ptions.outHeight=" + options.outHeight +
                "]，目标minSideLength=" + minSideLength + ", 目标maxNumOfPixels=" + maxNumOfPixels + ", options=" + options);
        int lowerBound = maxNumOfPixels == -1 ? 1 :
                (int)Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = minSideLength == -1 ? 128 :
                (int)Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound)
        {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1))
            return 1;
        if (minSideLength == -1) {
            return lowerBound;
        }
        return upperBound;
    }

    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        if (drawable == null)
            return null;
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 :
                        Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
