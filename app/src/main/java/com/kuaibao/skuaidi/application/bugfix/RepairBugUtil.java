package com.kuaibao.skuaidi.application.bugfix;

import android.net.Uri;
import android.text.TextUtils;

import com.kuaibao.skuaidi.BuildConfig;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.application.bugfix.model.PatchBean;
import com.kuaibao.skuaidi.manager.IDownloadManager;
import com.kuaibao.skuaidi.util.FileUtils;
import com.socks.library.KLog;
import com.thin.downloadmanager.DownloadRequest;

import java.io.File;
import java.io.IOException;


public class RepairBugUtil {
    private LocalPreferencesHelper mLocalPreferencesHelper;
    public RepairBugUtil(){
        if (mLocalPreferencesHelper == null) {
        mLocalPreferencesHelper = new LocalPreferencesHelper(SKuaidiApplication.getContext(), SPConst.SP_NAME);
        }
    }
    public void downloadAndLoad(final PatchBean bean, String downloadUrl) {
        Uri downloadUri = Uri.parse(downloadUrl);
        final String savePath=FileUtils.toRootPath() + bean.url;
        if(!FileUtils.fileExists(FileUtils.toRootPath()+bean.url.substring(0,bean.url.lastIndexOf("/")))){
            FileUtils.fileMkdirs(FileUtils.toRootPath()+bean.url.substring(0,bean.url.lastIndexOf("/")));
        }
        Uri destinationUri = Uri.parse(savePath);
        IDownloadManager.getInstance().startDownLoadTask(downloadUri, destinationUri, new IDownloadManager.IDownloadCallBack() {
            @Override
            public void onDownloadComplete(DownloadRequest downloadRequest) {
                // add patch at runtime
                try {
                    if(SKuaidiApplication.mPatchManager!=null && !BuildConfig.DEBUG){
                        SKuaidiApplication.mPatchManager.addPatch(savePath);
                        KLog.d("kb", "apatch:" + savePath + " added.");
                    }
                    //复制且加载补丁成功后，删除下载的补丁
                    File f = new File(savePath);
                    if (f.exists()) {
                        boolean result = new File(savePath).delete();
                        KLog.d("kb", savePath + " delete patch file"+(result?"成功":"失败"));
                    }
                    //mLocalPreferencesHelper.saveOrUpdate(SPConst.IsHavePathDownLoad, false);
                } catch (IOException e) {
                    KLog.e("kb",e.getMessage());
                } catch (Throwable throwable) {
                    KLog.e("kb",throwable.getMessage());
                }
            }

            @Override
            public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {

            }

            @Override
            public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {

            }
        });
    }

    public void comparePath(PatchBean RemoteBean) throws Exception {
        String pathInfo = mLocalPreferencesHelper.getString(SPConst.PATH_INFO);
        final PatchBean localBean = GsonUtils.getInstance().parseIfNull(PatchBean.class, pathInfo);
        KLog.i("kb","local_patch_version:--->"+SKuaidiApplication.VERSION_CODE+";remote_patch_version:--->"+RemoteBean.app_v);
        //远程的应用版本跟当前应用的版本比较
        if ((SKuaidiApplication.VERSION_CODE+"").equals(RemoteBean.app_v)) {
            //远程的应用版本跟本地保存的应用版本一样，但补丁不一样，则需要下载重新
            /**
             *第一种情况：当本地记录的Bean为空的时候（刚安装的时候可能为空）并且远程的Bean的path_v不为空的时候需要下载补丁。
             * 第二种情况：当本地记录的path_v和远程Bean的path_v不一样的时候需要下载补丁。
             */
            if (localBean == null && !TextUtils.isEmpty(RemoteBean.path_v)
                    || localBean.app_v.equals(RemoteBean.app_v) &&
                    !localBean.path_v.equals(RemoteBean.path_v)) {
                KLog.i("kb","符合下载补丁的条件,开始下载补丁并热修复");
                downloadAndLoad(RemoteBean, SPConst.URL_PREFIX + RemoteBean.url);
                String json = GsonUtils.getInstance().parse(RemoteBean);
                mLocalPreferencesHelper.saveOrUpdate(SPConst.PATH_INFO, json);
            }else {
                //mLocalPreferencesHelper.saveOrUpdate(SPConst.IsHavePathDownLoad, false);
            }
        }
    }

}
