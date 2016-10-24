package com.kuaibao.skuaidi.manager;

import android.net.Uri;

import com.socks.library.KLog;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

/**
 * Created by lgg on 2016/9/20 10:07.
 * Copyright (c) 2016, gangyu79@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #                     no bug forever                #
 * #                                                   #
 */

public class IDownloadManager {
    private static final int THREAD_COUNT = 3;  //下载的线程数
    private ThinDownloadManager mDownloadManager;

    private volatile  static IDownloadManager sIDownloadManager;
    public static IDownloadManager getInstance() {
        if (sIDownloadManager == null) {
            synchronized (IDownloadManager.class) {
                if (sIDownloadManager == null) {
                    sIDownloadManager=new IDownloadManager();
                }
            }
        }
        return sIDownloadManager;
    }

    private IDownloadManager(){
        if(mDownloadManager==null){
            mDownloadManager = new ThinDownloadManager(THREAD_COUNT);
        }
    }

    public void startDownLoadTask(Uri downloadUri, Uri destinationUri, final IDownloadCallBack iDownloadCallBack){
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri)
                .setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        KLog.i("kb", "onDownloadComplete:");
                        if(iDownloadCallBack!=null){
                            iDownloadCallBack.onDownloadComplete(downloadRequest);
                        }
                    }
                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        KLog.i("kb","errorCode:--->"+errorCode+";errorMessage:--->"+errorMessage);
                        if(iDownloadCallBack!=null){
                            iDownloadCallBack.onDownloadFailed(downloadRequest,errorCode,errorMessage);
                        }
                    }
                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        if(iDownloadCallBack!=null){
                            iDownloadCallBack.onProgress(downloadRequest,totalBytes,downloadedBytes,progress);
                        }
                    }
                });
        mDownloadManager.add(downloadRequest);
    }

    public interface IDownloadCallBack{
        void onDownloadComplete(DownloadRequest downloadRequest);
        void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage);
        void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress);
    }

}
