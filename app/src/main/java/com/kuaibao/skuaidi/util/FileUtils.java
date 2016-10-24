package com.kuaibao.skuaidi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.socks.library.KLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static final String AUDIO_ROOT_DIR="";
    /*
    *  判断文件是否存在
    *  filePath的参数格式：/storage/emulated/0/.ToDayNote/UserInfo/28017616_1459134625049.jpg
    */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /*
    *    文件删除
    */
    public static boolean fileDelete(String filePath) {
        File file = new File(filePath);
        if (file.exists() == false) {
            return false;
        }
        return file.delete();
    }

    public static void recursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                recursionDeleteFile(f);
            }
            file.delete();
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean fileMkdirs(String filePath) {
        File file = new File(filePath);
        return file.mkdirs();
    }

    /**
     * 主目录地址。在SD卡中，创建一个项目的根目录，根目录的包名为 ToDayNote
     * 获取项目主目录的地址
     */
    public static String toRootPath() {
        String dir;
        if (checkSDcard()) {
            dir = Environment.getExternalStorageDirectory().getPath();
        } else {
            dir = Environment.getDataDirectory().getPath();
        }
        return dir + "/skuaidi";
    }

    /**
     * 检测是否存在Sdcard
     *
     * @return 存在返回true，不存在返回false
     */
    public static boolean checkSDcard() {
        boolean flag = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        return flag;
    }


    /**
     * 在主目录下，再添加一层子目录。获取子目录的名称
     */
    public static String toVoipAudioResources() {
        return toRootPath() + "/voip";
    }


    /**
     * 获取SD卡路径
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取指定文件大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists() && file.isFile()) {
            size = file.length();
        } else {
            KLog.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    public static void copyAssetFileToFiles(Context context, String filepath,String filename)
            throws IOException {
        InputStream is = context.getAssets().open(filename);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();

        //File of = new File(context.getFilesDir() + "/" + filename);
        File of = new File(filepath+filename);
        of.createNewFile();
        FileOutputStream os = new FileOutputStream(of);
        os.write(buffer);
        os.close();
    }


    public static void saveBitmap(Bitmap bm, String picName) {
        try {
            File f = new File(toRootPath(), picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}