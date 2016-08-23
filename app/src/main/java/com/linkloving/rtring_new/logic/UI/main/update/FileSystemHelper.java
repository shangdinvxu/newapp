package com.linkloving.rtring_new.logic.UI.main.update;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import java.io.File;

/**
 * Created by leo.wang on 2016/5/18.
 */
public class FileSystemHelper {

    public static void viewFile(File f, Activity activity)
    {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        String type = getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        activity.startActivity(intent);
    }

    public static String getMIMEType(File f)
    {
        String type = "";
        String fName = f.getName();
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        if ((end.equals("m4a")) ||
                (end.equals("mp3")) ||
                (end.equals("mid")) ||
                (end.equals("xmf")) ||
                (end.equals("ogg")) ||
                (end.equals("wav")))
            type = "audio";
        else if ((end.equals("3gp")) ||
                (end.equals("mp4")))
            type = "video";
        else if ((end.equals("jpg")) ||
                (end.equals("gif")) ||
                (end.equals("png")) ||
                (end.equals("jpeg")) ||
                (end.equals("bmp")))
            type = "image";
        else if (end.equals("apk"))
            type = "application/vnd.android.package-archive";
        else {
            type = "*";
        }
        if (!end.equals("apk"))
        {
            type = type + "/*";
        }return type;
    }

}
