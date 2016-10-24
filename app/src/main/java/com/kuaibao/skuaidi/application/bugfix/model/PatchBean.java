package com.kuaibao.skuaidi.application.bugfix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatchBean implements Serializable {
    private static final long serialVersionUID = -4110715426207739836L;
    public String app_v;    //应用的版本
    public String path_v;   //补丁的版本
    public String url;  //补丁的下载地址
}
