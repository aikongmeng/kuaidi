package com.kuaibao.skuaidi.application;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kuaibao.skuaidi.R;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lgg on 15/12/15.
 */
public class DexActivity extends Activity implements Handler.Callback, Runnable {
    private ImageView mImageView;
    private ExecutorService mPool;
    private Handler mHandler;
    private boolean mFinish = false;

    private static final int mFrame = 100;

    private int mCurrent = 0;
    private String[] mFiles = new String[]{"gif/s1.jpg", "gif/s2.jpg", "gif/s3.jpg", "gif/s4.jpg", "gif/s5.jpg",
            "gif/s6.jpg", "gif/s7.jpg", "gif/s8.jpg", "gif/s9.jpg", "gif/s10.jpg", "gif/s11.jpg", "gif/s12.jpg",
            "gif/s13.jpg", "gif/s14.jpg", "gif/s15.jpg", "gif/s16.jpg", "gif/s17.jpg", "gif/s18.jpg",
            "gif/s19.jpg", "gif/s20.jpg", "gif/s21.jpg", "gif/s22.jpg", "gif/s23.jpg", "gif/s24.jpg",
            "gif/s25.jpg", "gif/s26.jpg", "gif/s27.jpg", "gif/s28.jpg", "gif/s29.jpg", "gif/s30.jpg"};
    private int mTotalFilesNumber = mFiles.length;

    private static final int CACHE_SIZE = 5;
    private Bitmap[] mBitmaps = new Bitmap[CACHE_SIZE];
    private BitmapFactory.Options[] mOptionses = new BitmapFactory.Options[CACHE_SIZE];
    private AssetManager mAssetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | 128);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
        setContentView(R.layout.activity_dex);
        mImageView = (ImageView) findViewById(R.id.img_dex);
        mPool = Executors.newFixedThreadPool(5);
        mHandler = new Handler(this);
        mAssetManager = getResources().getAssets();
        try {
            mBitmaps[0] = mPool.submit(new BitmapCallable(null, mFiles[0])).get();
            mBitmaps[1] = mPool.submit(new BitmapCallable(null, mFiles[1])).get();
            mBitmaps[2] = mPool.submit(new BitmapCallable(null, mFiles[2])).get();
            mBitmaps[3] = mPool.submit(new BitmapCallable(null, mFiles[3])).get();
            mBitmaps[4] = mPool.submit(new BitmapCallable(null, mFiles[4])).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
        new LoadDexTask().execute();
    }

    private void doShow(int number) throws ExecutionException, InterruptedException {

        if (mOptionses[number % 5] == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inBitmap = mBitmaps[number % 5];
            options.inMutable = true;
            options.inSampleSize = 1;
            mOptionses[number % 5] = options;
        }
        mBitmaps[number % 5] = mPool.submit(new BitmapCallable(mOptionses[number % 5], mFiles[number])).get();
        mImageView.setImageBitmap(mBitmaps[number % 5]);
    }

    class BitmapCallable implements Callable<Bitmap> {
        private BitmapFactory.Options mOptions;
        private String mPath;

        public BitmapCallable(BitmapFactory.Options options, String path) {
            mOptions = options;
            mPath = path;
        }

        @Override
        public Bitmap call() throws Exception {
            Bitmap bitmap;
            InputStream inputStream = mAssetManager.open(mPath);
            if (mOptions == null) {
                bitmap = BitmapFactory.decodeStream(inputStream);
            } else {
                bitmap = BitmapFactory.decodeStream(inputStream, null, mOptions);
            }
            return bitmap;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        try {
            doShow(msg.what);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        while (!mFinish) {
            mHandler.sendEmptyMessage(mCurrent++ % mTotalFilesNumber);
            try {
                Thread.sleep(mFrame);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class LoadDexTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                MultiDex.install(getApplication());
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //delete file
            mFinish = true;
            deleteFile();
            mPool.shutdown();
            mAssetManager.close();
            overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
            finish();
            overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_no_animation);
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {
        //cannot backPressed
    }

    private void deleteFile() {
        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "kuaibao.skuaidi";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
