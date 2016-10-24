package com.kuaibao.skuaidi.dispatch.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.CMRangePoint;
import com.kuaibao.skuaidi.activity.sendmsg.SendMSGActivity;
import com.kuaibao.skuaidi.camara.DisplayUtil;
import com.kuaibao.skuaidi.dispatch.activity.adapter.PackegeAddressAdapter;
import com.kuaibao.skuaidi.dispatch.bean.NumberPhonePair;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.sto.etrhee.activity.EThreeCameraActivity;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;


public class PackageMapActivity extends RxRetrofitBaseActivity implements LocationSource, AMapLocationListener, AMap.InfoWindowAdapter, AMap.OnMarkerClickListener, AMap.OnMapClickListener, PackegeAddressAdapter.OnClickWindowIconEvent, AMap.OnMapLoadedListener {

    public static final String CM_RANG_POINT_NAME = "cm_rang_point";
    private List<CMRangePoint> mCMRangePoints;
    @BindView(R.id.tv_title_des)
    TextView tv_title_des;
    @BindView(R.id.map)
    MapView mMapView;
    private AMap aMap;
    private OnLocationChangedListener mLocationChangedListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LatLng locaticonLatLng;
    private List<Marker> pickedMarkerList = new ArrayList<>();
    private UserInfo mUserInfo;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_map);
        mMapView.onCreate(savedInstanceState);
        mCMRangePoints = (List<CMRangePoint>) getIntent().getSerializableExtra(CM_RANG_POINT_NAME);
        //KLog.i("kb","mCMRangePoints:--->"+mCMRangePoints.size());
        mUserInfo = SkuaidiSpf.getLoginUser();
        initView();
        initAmap();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void initAmap() {
        if (null == aMap) {
            aMap = mMapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.parseColor("#39B54A"));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(50, 57, 181, 74));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setOnMapLoadedListener(this);
        addMarkers();
    }

    private void initView() {
        tv_title_des.setText("包裹地图");
    }

    @OnClick({R.id.iv_title_back, R.id.iv_locate_now})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.iv_locate_now:
                moveCenterPoint(locaticonLatLng);
                break;
        }
    }

    private void moveCenterPoint(LatLng latLng) {
        if (latLng != null) aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mLocationChangedListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mLocationChangedListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                locaticonLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
            }
        } else {
            String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
            KLog.e("kb", errText);
            UtilToolkit.showToast(errText);
        }
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.pop_package_map_layout, null);
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.poi_list_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
        List<CMRangePoint> list = querySamePoint(marker.getPosition());
        if (list != null && list.size() >= 2) {
            ViewGroup.LayoutParams params = recycler.getLayoutParams();
            params.height = DisplayUtil.dip2px(360);
            recycler.setLayoutParams(params);
        }
        PackegeAddressAdapter addressAdapter = new PackegeAddressAdapter(list);
        addressAdapter.setOnClickWindowIconEvent(this);
        recycler.setAdapter(addressAdapter);
        ImageView iv_package_window_close = (ImageView) view.findViewById(R.id.iv_package_window_close);
        iv_package_window_close.bringToFront();
        iv_package_window_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker != null && marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                }
            }
        });
        return view;
    }

    private List<CMRangePoint> querySamePoint(LatLng latLng) {
        List<CMRangePoint> points = new ArrayList<>();
        for (CMRangePoint cmRangePoint : mCMRangePoints) {
            LatLng latLng1 = new LatLng(Double.parseDouble(cmRangePoint.getAmap_lat()), Double.parseDouble(cmRangePoint.getAmap_lng()));
            if (latLng.equals(latLng1)) {
                points.add(cmRangePoint);
            }
        }
        return points;
    }

    private NotifyInfo buildInfo(String expressNo) {
        NotifyInfo info = new NotifyInfo();
        info.setExpress_number(expressNo);
        info.setScanTime(E3SysManager.getTimeBrandIndentify());
        info.setCourierJobNO(E3SysManager.getCourierNO());
        return info;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void addMarkers() {
        if (mCMRangePoints != null && mCMRangePoints.size() > 0) {
            for (int i = 0; i < mCMRangePoints.size(); i++) {
                //KLog.i("kb","point:--->"+mCMRangePoints.get(i).getAmap_lat()+";"+mCMRangePoints.get(i).getAmap_lng());
                LatLng latLng = new LatLng(Double.parseDouble(mCMRangePoints.get(i).getAmap_lat()), Double.parseDouble(mCMRangePoints.get(i).getAmap_lng()));
                if (!isAdded(latLng)) {
                    MarkerOptions markerOption = new MarkerOptions();
                    markerOption.position(latLng);
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.takerange_mark_recommend));
                    Marker pickedMarker = aMap.addMarker(markerOption);
                    pickedMarker.setTitle(mCMRangePoints.get(i).getId());
                    pickedMarker.setSnippet(mCMRangePoints.get(i).getAddress());
                    pickedMarkerList.add(pickedMarker);
                    //KLog.i("kb","pickedMarkerList:--->"+pickedMarker.getPosition().latitude+";"+pickedMarker.getPosition().longitude);
                }
            }
        }
    }

    private boolean isAdded(LatLng latLng) {
        for (Marker pickedMarker : pickedMarkerList) {
            if (latLng.equals(pickedMarker.getPosition())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        dismissAllAnotherInfoWindow(marker);
        if (!marker.isInfoWindowShown()) marker.showInfoWindow();
        moveCenterPoint(marker.getPosition());
        return false;
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mLocationChangedListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(false);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mLocationChangedListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        mLocationOption = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissAllAnotherInfoWindow(null);
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        deactivate();
        this.aMap = null;
        pickedMarkerList.clear();
        locaticonLatLng = null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        dismissAllAnotherInfoWindow(null);
    }

    private void dismissAllAnotherInfoWindow(Marker currentMarker) {
        for (Marker marker : pickedMarkerList) {
            if (marker.equals(currentMarker)) {
                continue;
            }
            if (marker.isInfoWindowShown())
                marker.hideInfoWindow();
        }
    }

    @Override
    public void onClickSign(String waybillNo) {
        if (E3SysManager.BRAND_STO.equals(mUserInfo.getExpressNo())) {
            mIntent = new Intent(PackageMapActivity.this, SignActivity.class);
            List<NotifyInfo> wayBills = new ArrayList<>();
            wayBills.add(buildInfo(waybillNo));
            mIntent.putExtra("dataList", (Serializable) wayBills);
            startActivity(mIntent);
        } else if (E3SysManager.BRAND_ZT.equals(mUserInfo.getExpressNo())) {
            mIntent = new Intent(PackageMapActivity.this, EThreeCameraActivity.class);
            List<NotifyInfo> wayBills = new ArrayList<>();
            wayBills.add(buildInfo(waybillNo));
            mIntent.putExtra("wayBills", (Serializable) wayBills);
            mIntent.putExtra(EThreeCameraActivity.ZT_COME_NAME, "single");
            startActivity(mIntent);
        }
    }

    @Override
    public void onClickProblem(String waybillNo) {
        mIntent = new Intent(PackageMapActivity.this, ProblemActivity.class);
        List<NotifyInfo> wayBills = new ArrayList<>();
        wayBills.add(buildInfo(waybillNo));
        mIntent.putExtra("dataList", (Serializable) wayBills);
        startActivity(mIntent);
    }

    @Override
    public void onClickSMS(String waybillNo) {
        com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
        array.add(waybillNo);
        getPhoneState(array.toJSONString());
    }

    @Override
    public void onMapLoaded() {
        if (pickedMarkerList.size() > 0) {
            // 设置所有maker显示在当前可视区域地图中
            mMapView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                    for (Marker pickedMarker : pickedMarkerList) {
                        bounds.include(pickedMarker.getPosition());
                    }
                    aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 10));
                }
            }, 500);
        } else {
            moveCenterPoint(locaticonLatLng);
        }
    }


    /**
     * 查询单号是否存在对应的手机号
     *
     * @param dhs 单号
     */
    protected void getPhoneState(String dhs) {
        final ApiWrapper wrapper = new ApiWrapper();
        Subscription subscription = wrapper.getPhoneState(dhs, String.valueOf(Utility.getVersionCode()))
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        String msg = "";
                        if (e instanceof RetrofitUtil.APIException) {
                            RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;//可以获取到服务器返回的错误码像上抛让Activity进一步处理
                            msg = exception.msg;
                        }
                        com.alibaba.fastjson.JSONObject data_fail = JSON.parseObject(msg);
                        if (data_fail != null) {
                            com.alibaba.fastjson.JSONObject dhs = data_fail.getJSONObject("dhs");
                            startSendMSGActivity(dhs.getJSONArray("failDh"),dhs.getJSONArray("passDh"),data_fail.getString("ident"));
                        }
                    }
                })
                .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject result) {
                        if (result != null) {
                            com.alibaba.fastjson.JSONObject dhs = result.getJSONObject("dhs");
                            startSendMSGActivity(dhs.getJSONArray("failDh"),dhs.getJSONArray("passDh"),result.getString("ident"));
                        }
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    /**
     * 跳转到发短信界面
     */
    private void startSendMSGActivity(JSONArray failDh,JSONArray passDh,String ident) {
        NumberPhonePair mNumberPhonePair = new NumberPhonePair();
        if (passDh != null && passDh.size() > 0) {
            mNumberPhonePair.setDh(passDh.get(0).toString());
            mNumberPhonePair.setPhone("1**-****-****");
        } else if (failDh != null && failDh.size() > 0) {
            mNumberPhonePair.setDh(failDh.get(0).toString());
        }

        mIntent = new Intent(this, SendMSGActivity.class);
        List<NumberPhonePair> list = new ArrayList<>();
        list.add(mNumberPhonePair);
        mIntent.putExtra("numberPhonePairs", (Serializable) list);
        mIntent.putExtra("ident", ident);
        startActivity(mIntent);
    }
}
