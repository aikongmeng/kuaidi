package com.kuaibao.skuaidi.camara;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.kuaibao.skuaidi.application.SKuaidiApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 图片管理器
 *
 * @author xy
 */
public class ImageUtil {

    private static Context context = SKuaidiApplication.getInstance();

    /**
     * 旋转Bitmap
     *
     * @param b
     * @param rotateDegree
     * @return
     */
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        if (null == b || rotateDegree == 0) {
            return b;
        }

        try {
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.setRotate(rotateDegree, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                    b.getHeight(), matrix, true);
            b.recycle();
            b = null;
            return rotaBitmap;
        } catch (java.lang.OutOfMemoryError e) {
            return b;
        }

    }

    /**
     * 根据图片路径获取缩略图
     *
     * @param pathName
     * @return
     */
    public static Bitmap decodeFile(String pathName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[32 * 1024];
        options.outWidth = 480;
        options.outHeight = 800;

        File file = null;
        FileInputStream fs = null;
        try {
            file = new File(pathName);
            fs = new FileInputStream(file);
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
        Bitmap bitmap = null;
        if (fs != null) {
            try {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
                        options);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }
        return bitmap;
    }

    public static Bitmap decodeFile(String pathName, int width, int height, int size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[size * 1024];
        options.outWidth = width;
        options.outHeight = height;
        File file = new File(pathName);
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
        Bitmap bitmap = null;
        if (fs != null) {
            try {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
                        options);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }
        return bitmap;
    }

    /**
     * 动态获取图片缩放比
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;

    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }

    }
}
