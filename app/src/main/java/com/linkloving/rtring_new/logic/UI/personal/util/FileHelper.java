package com.linkloving.rtring_new.logic.UI.personal.util;
import com.linkloving.utils.CommonUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.security.MessageDigest;
/**
 * Created by leo.wang on 2016/4/5.
 */
public class FileHelper {
    public static String getFileMD5(byte[] fileData)
            throws Exception
    {
        if (fileData == null) {
            return null;
        }
        String md5 = null;
        try
        {
            md5 = CommonUtils.byteToHexStringForMD5(MessageDigest.getInstance("MD5").digest(fileData));
        }
        catch (Exception e)
        {
            throw e;
        }

        return md5;
    }

    public static String getFileMD5(String filePath)
            throws Exception
    {
        String md5 = null;
        try
        {
            if (filePath != null)
            {
                File f = new File(filePath);
                if ((f != null) && (f.exists()))
                {
                    byte[] fileda = readFileWithBytes(f);

                    md5 = getFileMD5(fileda);
                }
            }
        }
        catch (Exception e)
        {
            throw e;
        }

        return md5;
    }

    public static Object readObjectFromFile(String filePath)
            throws Exception
    {
        if (CommonUtils.isStringEmpty(filePath, true))
            throw new Exception("要读取的文件路径是无效参数.");
        Object obj = null;
        try
        {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj = ois.readObject();
        }
        catch (FileNotFoundException e)
        {
            throw new Exception("要读取的文件是个目录或不存或其它原因," + e.getMessage());
        }
        catch (Exception ee)
        {
            throw ee;
        }
        return obj;
    }

    public static Object readObjectFromFile(File file)
            throws Exception
    {
        if ((file == null) || (file.isDirectory()) || (!file.exists())) {
            return null;
        }
        return readObjectFromFile(file.getAbsolutePath());
    }

    public static boolean writeObjectToFile(String filePath, Object obj)
            throws Exception
    {
        boolean isSuccess = false;
        if ((obj == null) || (CommonUtils.isStringEmpty(filePath, true))) {
            throw new Exception("要存放的文件路径或要写入的对象是无效参数.");
        }
        if (!(obj instanceof Serializable)) {
            throw new RuntimeException("要写入的对象没有序列化！");
        }
        try
        {
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
            fos.flush();
            oos.close();
            fos.close();
            isSuccess = true;
        }
        catch (FileNotFoundException e)
        {
            throw new Exception("不能打开要写入的语言件，可能的原因是该路径指明的是一个目录或文件不存在且不能建立，或其它原因," +
                    e.getMessage());
        }
        catch (Exception ee)
        {
            throw ee;
        }

        return isSuccess;
    }

    public static void writeStringToFile(String content, String filePath)
            throws Exception
    {
        writeStringToFile(content, filePath, true);
    }

    public static void writeStringToFile(String content, String filePath, boolean overideIfExists)
            throws Exception
    {
        if ((content == null) || (filePath == null)) {
            throw new Exception("无效参数异常,content=" + content + ",filePath=" + filePath);
        }
        File file = new File(filePath);
        if ((file.exists()) && (!overideIfExists)) {
            throw new Exception("要保存的文件名：" + file.getAbsolutePath() + "已经存在，不允许覆盖，写入失败！");
        }
        file.createNewFile();
        FileWriter fw = new FileWriter(file.getAbsolutePath(), false);
        fw.write(content);
        fw.close();
    }

    public static String readStringFromFile(String path)
            throws Exception
    {
        return readStringFromFile(new File(path));
    }

    public static String readStringFromFile(URI uri)
            throws Exception
    {
        return readStringFromFile(new File(uri));
    }

    public static String readStringFromFile(File f)
            throws Exception
    {
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null)
        {

            sb.append(s);
        }fr.close();
        return sb.toString();
    }

    public static boolean isFileExist(String filePath)
    {
        File f = null;
        try {
            f = new File(filePath);
        }
        catch (Exception localException)
        {
        }
        return f.exists();
    }

    public static byte[] readFileWithBytes(File f)
            throws Exception
    {
        try
        {
            return readFileWithBytes(new FileInputStream(f));
        }
        catch (IOException ex)
        {
        }
        return null;
    }

    public static byte[] readFileWithBytes(InputStream is)
    {
        try
        {
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();
            return data;
        }
        catch (IOException ex)
        {

        }return null;
    }

    public static boolean writeFileWithBytes(byte[] bytes, String saveToPath, boolean renameIfExist)
    {
        boolean sucess = false;
        try
        {
            File f = new File(saveToPath);

            if (f.exists())
            {
                if (renameIfExist)
                {
                    String fileNameWithoutExt = getFileNameWithoutExt(saveToPath);

                    String fileName = getFileName(saveToPath);

                    int index = saveToPath.lastIndexOf(fileName);

                    String dir = saveToPath.substring(0, index);

                    f = new File(dir + fileNameWithoutExt + "(" +
                            CommonUtils.getDateWithStr() + "+" +
                            CommonUtils.getTimeWithStr("-") + ")." +
                            getFileExName(fileName));
                }

            }

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bytes);
            fos.close();

            sucess = true;
        }
        catch (IOException e)
        {

        }

        return sucess;
    }

    public static String getFileName(String filePath)
    {
        return filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
    }

    public static String getFileNameWithoutExt(String filePath)
    {
        String fileName = getFileName(filePath);
        String ext = getFileExName(fileName);
        int index = fileName.lastIndexOf(ext);
        if (index != -1)
            return fileName.substring(0, index - 1);
        return fileName;
    }

    public static String getFileExName(File f)
    {
        return getFileExName(f.getName());
    }

    public static String getFileExName(String fileName)
    {
        String exName = "";
        int pos = fileName.lastIndexOf(".");
        if (pos == -1)
        {

        }
        else
            exName = fileName.substring(pos + 1, fileName.length());
        return exName;
    }

    public static boolean deleteFile(String filePath)
    {
        boolean isSucess = true;
        try
        {
            File f = new File(filePath);
            if (f.exists())
                f.delete();
        }
        catch (Exception e)
        {
            isSucess = false;
        }
        return isSucess;
    }

    public static void deleteDir(String path, boolean deleteThisDir)
    {
        File file = new File(path);
        if (file.exists())
        {
            if (file.isDirectory())
            {
                File[] files = file.listFiles();
                for (File subFile : files)
                {
                    if (subFile.isDirectory())
                        deleteDir(subFile.getPath(), true);
                    else {
                        subFile.delete();
                    }
                }
            }
            if (deleteThisDir)
                file.delete();
        }
    }

    public static boolean copyFiles(String srcDir, String destDir)
            throws Exception
    {
        boolean sucess = false;
        if (srcDir != null)
        {
            File srcDirFile = new File(srcDir);
            if (srcDirFile.isDirectory())
            {
                File[] subFiles = srcDirFile.listFiles();
                for (File subFile : subFiles)
                {
                    if (subFile.isDirectory())
                    {
                        copyFiles(subFile.getAbsolutePath(), destDir + subFile.getName() + "/");
                    }
                    else {
                        File destDirFile = new File(destDir);
                        if (!destDirFile.exists()) {
                            destDirFile.mkdirs();
                        }
                        File destFile = new File(destDir + subFile.getName());
                        copyFile(subFile, destFile);
                    }
                }
            }
            else {
                throw new IllegalArgumentException("srcDir=" + srcDir + "不是一个目录.");
            }
        }
        return sucess;
    }

    public static boolean copyFile(String srcFilePath, String destFilePath)
            throws Exception
    {
        return copyFile(new File(srcFilePath), new File(destFilePath));
    }

    public static boolean copyFile(File src, File dest)
            throws Exception
    {
        InputStream inStream = null;
        OutputStream outStream = null;
        boolean sucess = false;
        File afile = src;
        File bfile = dest;

        if (!bfile.getParentFile().exists()) {
            bfile.getParentFile().mkdirs();
        }
        inStream = new FileInputStream(afile);
        outStream = new FileOutputStream(bfile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inStream.read(buffer)) > 0)
        {
            outStream.write(buffer, 0, length);
        }
        inStream.close();
        outStream.close();
        sucess = true;

        return sucess;
    }
}

