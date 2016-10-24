package com.kuaibao.skuaidi.business.nettelephone.calllog.utils.net.download;

public interface DownloadProgressListener {
	void onDownloadSize(int size, int totalSize, String filePath);
	void onDownloadComplete(String filePath);
}
