package com.kuaibao.skuaidi.activity.picksendmapmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.blog.www.guideview.Guide;
import com.blog.www.guideview.GuideBuilder;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.picksendmapmanager.adapter.InputTipsSearchAdapter;
import com.kuaibao.skuaidi.activity.picksendmapmanager.adapter.PickSendPointAdapter;
import com.kuaibao.skuaidi.activity.picksendmapmanager.adapter.PoiListAdapter;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.CMRangePoint;
import com.kuaibao.skuaidi.activity.picksendmapmanager.entity.PointPoiItem;
import com.kuaibao.skuaidi.activity.picksendmapmanager.guidcomponent.MapGuideComponent;
import com.kuaibao.skuaidi.activity.picksendmapmanager.utils.MapUtils;
import com.kuaibao.skuaidi.activity.picksendmapmanager.utils.ToastUtil;
import com.kuaibao.skuaidi.camara.DisplayUtil;
import com.kuaibao.skuaidi.main.MainActivity;
import com.kuaibao.skuaidi.popup.baselib.BasePopupWindow;
import com.kuaibao.skuaidi.retrofit.RetrofitUtil;
import com.kuaibao.skuaidi.retrofit.api.ApiWrapper;
import com.kuaibao.skuaidi.retrofit.base.RxRetrofitBaseActivity;
import com.kuaibao.skuaidi.searchview.GlobalSearchView;
import com.kuaibao.skuaidi.util.AMapUtil;
import com.kuaibao.skuaidi.util.AdUrlBuildUtil;
import com.kuaibao.skuaidi.util.DensityUtil;
import com.kuaibao.skuaidi.util.IsGuid;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;
import com.vlonjatg.progressactivity.ProgressActivity;
import com.wangjie.shadowviewhelper.ShadowProperty;
import com.wangjie.shadowviewhelper.ShadowViewHelper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

import static com.kuaibao.skuaidi.personal.personinfo.RegisterOrModifyInfoActivity.FROM_WHERE_NAME;
import static com.kuaibao.skuaidi.personal.personinfo.RegisterOrModifyInfoActivity.REGISTR_TYPE;

public class PickAndSendMapActivity extends RxRetrofitBaseActivity implements LocationSource, AMapLocationListener, AMap.OnMapClickListener,
        AMap.OnMapLongClickListener,AMap.InfoWindowAdapter,AMap.OnMarkerClickListener,AMap.OnMapLoadedListener,GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnCameraChangeListener,Inputtips.InputtipsListener {

    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.rl_pick_map_control)
    RelativeLayout rl_pick_map_control;
    @BindView(R.id.tv_review_all_range)
    TextView tv_review_all_range;
    @BindView(R.id.tv_description_rang_count)
    TextView tv_description_rang_count;
    @BindView(R.id.tv_control_switch)
    TextView tv_control_switch;
    @BindView(R.id.ll_search_result_parent)
    LinearLayout ll_search_result_parent;
    @BindView(R.id.iv_center_marker)
    ImageView iv_center_marker;
    @BindView(R.id.poi_recycler)
    RecyclerView poi_recycler;
    @BindView(R.id.progressLayout_search_result)
    ProgressActivity progress_search_result;
    @BindView(R.id.ll_goto_search)
    LinearLayout ll_goto_search;
    @BindView(R.id.iv_search_return)
    ImageView iv_search_return;
    private AMap aMap;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private OnLocationChangedListener mLocationChangedListener;
    private LatLng locaticonLatLng;
    private Vibrator mVibrator;
    private GeocodeSearch mgGeocodeSearch;
    private boolean canMapEdit=true;
    private List<Marker> pickedMarkerList=new ArrayList<>();
    private Map<Integer,Marker> addMarkerMap=new HashMap<>();
    private LatLng lastCenterPosition;
    private PickSendPointAdapter pointAdapter;
    private View infoContent;
    private View infoContent_Add;
    private GlobalSearchView mGlobalSearchView;
    private String currentCity="";
    private BaseQuickAdapter mBaseQuickAdapter;
    private String fromWhere;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_and_send_map);
        if(getIntent().hasExtra(FROM_WHERE_NAME)){
            fromWhere=getIntent().getStringExtra(FROM_WHERE_NAME);
        }
        mMapView.onCreate(savedInstanceState);
        initAmap();
        initView();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected  void setStatusBar(){
        StatusBarUtil.setColor(PickAndSendMapActivity.this, ContextCompat.getColor(PickAndSendMapActivity.this, R.color.white));
    }

    private void initView(){
        //添加阴影效果
        ShadowProperty shadowProperty = new ShadowProperty()
                .setShadowColor(0x77000000)
                .setShadowDy(DisplayUtil.dip2px(0.5f))
                .setShadowRadius(DisplayUtil.dip2px(4f));
        ShadowViewHelper.bindShadowHelper(shadowProperty, ll_goto_search);

        poi_recycler.setLayoutManager(new LinearLayoutManager(this));
        poi_recycler.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.gray_4))
                .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                .build());  //添加分割线
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @OnClick({R.id.rl_pick_map_control,R.id.tv_review_all_range,R.id.iv_locate_now,R.id.iv_search_return,R.id.ll_search_map_click})
    public void onClick(View view){
        switch(view.getId()){
            case R.id.rl_pick_map_control:
                switchControlAndRecycler();
                break;
            case R.id.tv_review_all_range:
                Intent intent=new Intent(PickAndSendMapActivity.this,ReviewAllPointActivity.class);
                ArrayList<PointPoiItem> allPoints=new ArrayList<>();
                for(Marker marker:pickedMarkerList){
                    PointPoiItem pointPoiItem=(PointPoiItem) marker.getObject();
                    if(pointPoiItem!=null) allPoints.add(pointPoiItem);
                }
                intent.putParcelableArrayListExtra(ReviewAllPointActivity.PONITS_NAME,allPoints);
                startActivityForResult(intent,0xFF);
                break;
            case R.id.iv_locate_now:
                if(canMapEdit) dismissAllAnotherInfoWindow(null);
                moveCenterPoint(locaticonLatLng);
                break;
            case R.id.iv_search_return:
                if(!canMapEdit){
                    switchControlAndRecycler();
                }else{
                    finishActivity();
                }
                break;
            case R.id.ll_search_map_click:
                if(mGlobalSearchView==null){
                    int offSet=ll_goto_search.getMeasuredHeight()+ll_goto_search.getTop();
                    mGlobalSearchView=new GlobalSearchView(PickAndSendMapActivity.this,ll_goto_search,offSet);
                    mGlobalSearchView.setSearchViewHint("输入地点");
                    mGlobalSearchView.setSearchViewAdapter(getSearchViewAdapter(new ArrayList<Tip>()));
                    mGlobalSearchView.setSearchClickViewVisible(true);
                    mGlobalSearchView.setAutoShowInputMethod(true,false,250);
                    mGlobalSearchView.setSearchViewBuilder(new GlobalSearchView.OnBuildSearchViewListener() {
                        @Override
                        public void onClickSearch(String inputText) {
                            if(!TextUtils.isEmpty(inputText)){
                                searchInputTips(inputText);
                            }else{
                                clearAdapterData();
                            }
                        }
                        @Override
                        public void onInputSearchText(String inputText) {
                            if(!TextUtils.isEmpty(inputText)){
                                searchInputTips(inputText);
                            }else{
                                clearAdapterData();
                            }
                        }
                    });
                    mGlobalSearchView.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            mGlobalSearchView.onShowOrHideSearchView(1);
                        }
                    });
                }
                mGlobalSearchView.onShowOrHideSearchView(0);
                break;
        }
    }

    private void clearAdapterData(){
        if(mBaseQuickAdapter!=null){
            mBaseQuickAdapter.getData().clear();
            mBaseQuickAdapter.notifyDataSetChanged();
        }
    }

    private void searchInputTips(String newText){
        InputtipsQuery inputquery = new InputtipsQuery(newText, currentCity);
        Inputtips inputTips = new Inputtips(PickAndSendMapActivity.this, inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
    }

    private BaseQuickAdapter getSearchViewAdapter(List<Tip> listData){
        if(mBaseQuickAdapter==null){
            mBaseQuickAdapter=new InputTipsSearchAdapter(PickAndSendMapActivity.this,listData);
            mBaseQuickAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    final Tip tip=(Tip)mBaseQuickAdapter.getData().get(position);
                    if(tip.getPoint()!=null){
                        mGlobalSearchView.dismiss();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(canMapEdit){
                                    locaticonLatLng=new LatLng(tip.getPoint().getLatitude(),tip.getPoint().getLongitude());
                                    switchControlAndRecycler();
                                }else{
                                    moveCenterPoint(new LatLng(tip.getPoint().getLatitude(),tip.getPoint().getLongitude()));
                                }
                            }
                        },250);
                    }else{
                        UtilToolkit.showToast("该点坐标未知");
                    }
                }
            });
        }
        return mBaseQuickAdapter;
    }
    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
            KLog.i("kb","rCode:--->"+rCode);
            if(mBaseQuickAdapter!=null){
                mBaseQuickAdapter.getData().clear();
                if(rCode==1000){
                    List<Tip> tips=new ArrayList<>();
                    for(Tip tip:tipList){
                        if(tip.getPoint()!=null) tips.add(tip);
                    }
                    mBaseQuickAdapter.getData().addAll(tips);
                }
                mBaseQuickAdapter.notifyDataSetChanged();
                mGlobalSearchView.smoothRecyclerToTop();
            }
    }

    private Marker getMarkerByServerId(String serverId){
        for(Marker marker:pickedMarkerList){
            final PointPoiItem pointPoiItem=(PointPoiItem)marker.getObject();
            if(pointPoiItem!=null){
                if(serverId.equals(pointPoiItem.getServerId())){
                    return marker;
                }
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0XFF){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    List<String> deleteServerIds=data.getStringArrayListExtra(ReviewAllPointActivity.DELETE_SERVERID_NAME);
                    if(deleteServerIds!=null){
                        for(String serverId:deleteServerIds){
                            Marker marker=getMarkerByServerId(serverId);
                            if(marker!=null){
                                pickedMarkerList.remove(marker);
                                marker.remove();
                                aMap.invalidate();
                            }
                        }
                        if(lastCenterPosition!=null){
                            progress_search_result.showLoading();
                            doRegecodeQuery(new LatLonPoint(lastCenterPosition.latitude,lastCenterPosition.longitude));
                            jumpCenterMarker();
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void switchControlAndRecycler(){
        if(canMapEdit) {
            dismissAllAnotherInfoWindow(null);
        }
        hideOrShowAllMarkers();
        tv_description_rang_count.setVisibility(tv_description_rang_count.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
        tv_review_all_range.setVisibility(tv_review_all_range.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
        tv_control_switch.setVisibility(tv_control_switch.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
        ll_search_result_parent.setVisibility(ll_search_result_parent.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
        canMapEdit= !canMapEdit;
        if(!canMapEdit){
            moveCenterPoint(locaticonLatLng);
        }else{
            showAllMarkersInBound(false);
            lastCenterPosition=null;
        }

        iv_center_marker.setVisibility(canMapEdit?View.GONE:View.VISIBLE);
        if(iv_center_marker.getVisibility()==View.VISIBLE){
            iv_center_marker.bringToFront();
        }
    }

    private void hideOrShowAllMarkers(){
        for(Marker marker:pickedMarkerList){
            marker.setVisible(marker.isVisible()?false:true);
        }
        Iterator<Integer> iterator=addMarkerMap.keySet().iterator();
        while(iterator.hasNext()){
            int key=iterator.next();
            Marker marker=addMarkerMap.get(key);
            if(marker==null){
                continue;
            }
            marker.setVisible(marker.isVisible()?false:true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!canMapEdit){
                switchControlAndRecycler();
                return true;
            }else{
                finishActivity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void finishActivity(){
        if(REGISTR_TYPE.equals(fromWhere)){
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void initAmap(){
        if (null == aMap) {
            aMap = mMapView.getMap();
            setUpMap();
            if(mgGeocodeSearch==null){
                mgGeocodeSearch = new GeocodeSearch(this);
                mgGeocodeSearch.setOnGeocodeSearchListener(this);
            }
        }
    }

    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.parseColor("#39B54A"));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(50, 57, 181, 74));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMapLongClickListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapLoadedListener(this);
        aMap.setOnCameraChangeListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showGuidViewStep(final int index){
        GuideBuilder builder = new GuideBuilder();
        if(index==1){
            builder.setTargetView(ll_goto_search);
            builder.setOverlayTarget(true);
            builder.addComponent(new MapGuideComponent(1));
        }else if(index==2){
            builder.setTargetView(rl_pick_map_control);
            builder.setOverlayTarget(false);
            builder.addComponent(new MapGuideComponent(2));
        }else if(index==3){
            builder.setTargetView(progress_search_result);
            builder.setOverlayTarget(false);
            builder.addComponent(new MapGuideComponent(3));
        }else if(index==4){
            builder.setTargetView(iv_search_return);
            builder.setOverlayTarget(false);
            builder.addComponent(new MapGuideComponent(4));
        }
        builder.setAlpha(150);//.setHighTargetCorner(20);
        builder.setOutsideTouchable(false);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {}
            @Override
            public void onDismiss() {
                if(index==1){
                    showGuidViewDelayed(2);
                }else if(index==2){
                    switchControlAndRecycler();
                }else if(index==3){
                    showGuidViewDelayed(4);
                }else if(index==4){
                    switchControlAndRecycler();
                    IsGuid.setIsGuided(getApplicationContext(),PickAndSendMapActivity.this.getClass().getName());
                }
            }
        });
        Guide guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(PickAndSendMapActivity.this);
    }

    private void showGuidViewDelayed(final int index){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showGuidViewStep(index);
            }
        },200);
    }

    private void getMyPoint(){
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.getCMRangePoint("cm_take_range", SkuaidiSpf.getLoginUser().getPhoneNumber())
                .subscribe(newSubscriber(new Action1<List<CMRangePoint>>() {
                    @Override
                    public void call(List<CMRangePoint> cmRangePoints) {
                        initControlView(cmRangePoints);
                        if(!IsGuid.activityIsGuided(getApplicationContext(), PickAndSendMapActivity.this.getClass().getName())){
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showGuidViewDelayed(1);
                                }
                            },300);
                        }
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    private void initControlView(List<CMRangePoint> cmRangePoints){
        rl_pick_map_control.setVisibility(View.VISIBLE);
        //tv_control_switch.setTextSize(DensityUtil.sp2px(getApplicationContext(),8));
        if(cmRangePoints!=null && cmRangePoints.size()>0){
            tv_control_switch.setText("共标记"+cmRangePoints.size()+"个取派点");
            Drawable img_left = getResources().getDrawable(R.drawable.icon_sign_review);
            img_left.setBounds(0, 0, img_left.getMinimumWidth(), img_left.getMinimumHeight());
            tv_control_switch.setCompoundDrawables(img_left,null,null,null);
            addPickedMarkers(cmRangePoints);
        }else{
            tv_control_switch.setText("添加取派范围");
            Drawable img_left = getResources().getDrawable(R.drawable.icon_add);
            img_left.setBounds(0, 0, img_left.getMinimumWidth(), img_left.getMinimumHeight());
            tv_control_switch.setCompoundDrawables(img_left,null,null,null);
            moveCenterPoint(locaticonLatLng);//没有已标记的点定位到当前位置中心
        }
        tv_control_switch.setVisibility(View.VISIBLE);
        tv_description_rang_count.setText("已设取派点："+cmRangePoints.size()+"个");
        tv_description_rang_count.setVisibility(View.GONE);
        tv_review_all_range.setVisibility(View.GONE);
        canMapEdit=true;
    }

    private void addPickedMarkers(List<CMRangePoint> cmRangePoints){
        for(int i=0;i<cmRangePoints.size();i++){
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(new LatLng(Double.parseDouble(cmRangePoints.get(i).getAmap_lat()),Double.parseDouble(cmRangePoints.get(i).getAmap_lng())));
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.takerange_mark_recommend));
            Marker pickedMarker = aMap.addMarker(markerOption);
            pickedMarker.setTitle(cmRangePoints.get(i).getAddress()+cmRangePoints.get(i).getPoint());
            pickedMarker.setSnippet("pickedMarker");
            PointPoiItem pointPoiItem=new PointPoiItem();
            pointPoiItem.setServerId(cmRangePoints.get(i).getId());
            pointPoiItem.setLat(Double.parseDouble(cmRangePoints.get(i).getAmap_lat()));
            pointPoiItem.setLng(Double.parseDouble(cmRangePoints.get(i).getAmap_lng()));
            pointPoiItem.setPoiDescription(cmRangePoints.get(i).getPoint());
            pointPoiItem.setSnippet(cmRangePoints.get(i).getAddress());
            pointPoiItem.setArea(cmRangePoints.get(i).getArea());
            pickedMarker.setObject(pointPoiItem);
            pickedMarkerList.add(pickedMarker);
        }
        showAllMarkersInBound(true);
    }

    private void showAllMarkersInBound(boolean firstInit){
        if(pickedMarkerList!=null && pickedMarkerList.size()>0){
            // 设置所有maker显示在当前可视区域地图中
            final LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            for(Marker pickedMarker:pickedMarkerList){
                bounds.include(pickedMarker.getPosition());
            }
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), AdUrlBuildUtil.getScreenWith(getApplicationContext()),AdUrlBuildUtil.getScreenHeight(getApplicationContext())-DensityUtil.dip2px(getApplicationContext(),120),10));
        }
        if(firstInit){
            aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getCameraPosition().zoom-1));
        }
    }

    public void AddMarkers(LatLng latLng) {
        if(!Utility.isNetworkAvailable(getApplicationContext())){
            UtilToolkit.showToast("请检查您的网络连接");
            return;
        }
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        doRegecodeQuery(latLonPoint);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.takerange_mark_tagged));
        Marker addMarker = aMap.addMarker(markerOption);
        addMarker.setSnippet("addMark");
        if(!addMarker.isInfoWindowShown()) addMarker.showInfoWindow();
        addMarkerMap.put(latLng.hashCode(),addMarker);
        jumpPoint(addMarker,latLng);
        mVibrator.vibrate(50);
    }

    private void doRegecodeQuery(LatLonPoint latLonPoint){
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        mgGeocodeSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    private boolean isHasAddedMarker(int hashCode){
        return addMarkerMap.get(hashCode)!=null;
    }

    public void jumpPoint(final Marker marker,final LatLng latLng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        Point startPoint = proj.toScreenLocation(latLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * latLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * latLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                aMap.invalidate();// 刷新地图
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    /**
     * 地理编码回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        Marker currentAddMarker=addMarkerMap.get(result.getRegeocodeQuery().getPoint().hashCode());
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null) {
                String formatAddress=result.getRegeocodeAddress().getFormatAddress();
                String district=result.getRegeocodeAddress().getDistrict();
                String city=result.getRegeocodeAddress().getCity();
                String province=result.getRegeocodeAddress().getProvince();
                String townShip=result.getRegeocodeAddress().getTownship();
                String neighborhood=result.getRegeocodeAddress().getNeighborhood();
                double lat=result.getRegeocodeQuery().getPoint().getLatitude();
                double lng=result.getRegeocodeQuery().getPoint().getLongitude();
                List<PoiItem> poiItems=result.getRegeocodeAddress().getPois();
                if(poiItems!=null && poiItems.size()>0){
                    Collections.sort(poiItems,comparator);
                }
                if(canMapEdit) {
                    if(TextUtils.isEmpty(formatAddress)){
                        showMapErrorMsg(currentAddMarker,"无法获取该点位置信息",false,0);
                        return;
                    }
                    if(poiItems==null || poiItems.size()==0){
                        showMapErrorMsg(currentAddMarker,"无法查询该点POI信息,请重新标记",false,0);
                        return;
                    }
                    String point=poiItems.get(0).toString();
                    int distance=poiItems.get(0).getDistance();
                    String poiId=poiItems.get(0).getPoiId();
                    String snippet=poiItems.get(0).getSnippet();
                    if(TextUtils.isEmpty(point)){
                            showMapErrorMsg(currentAddMarker,"无法查询该点POI信息,请重新标记",false,0);
                            return;
                    }
                    if(isHadAddedPOI(point)){
                        showMapErrorMsg(currentAddMarker,"附近已添加标记,不必再添加",false,0);
                        return;
                    }
                        PointPoiItem pointPoiItem=buildPointPoiItem(lat,lng,formatAddress,district, city,province,townShip,neighborhood, point,distance,poiId,snippet);
                        if(currentAddMarker!=null){
                            currentAddMarker.setTitle(pointPoiItem.getSnippet()+pointPoiItem.getPoiDescription());
                            currentAddMarker.setObject(pointPoiItem);
                            currentAddMarker.setSnippet("addMark");
                            Message message=new Message();
                            message.what=0XB1;
                            message.obj=currentAddMarker;
                            mHandler.handleMessage(message);
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AMapUtil.convertToLatLng(result.getRegeocodeQuery().getPoint()), aMap.getCameraPosition().zoom));
                        }
                } else{
                    List<PointPoiItem> myPoiItems=new ArrayList<>();
                    if(poiItems!=null && poiItems.size()>0){
                        for(PoiItem poiItem:poiItems){
                            PointPoiItem myPoiItem=buildPointPoiItem(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude(),formatAddress,district, city,province,townShip,neighborhood, poiItem.toString(),poiItem.getDistance(),poiItem.getPoiId(),poiItem.getSnippet());
                            myPoiItems.add(isAddedMarker(myPoiItem));
                        }
                        initPointAdapter(myPoiItems);
                    }else{
                        if(TextUtils.isEmpty(formatAddress)){
                            showMapErrorMsg(null,"该点POI信息为空",false,0);
                            return;
                        }
                        initPointAdapter(myPoiItems);
                    }
                }
            } else {
                showMapErrorMsg(currentAddMarker,"无法获取该点位置信息",false,0);
            }
        }else {
            showMapErrorMsg(currentAddMarker,"",true,rCode);
        }
    }

    private boolean isHadAddedPOI(String poi){
        for(Marker marker:pickedMarkerList){
            if(marker.getObject()!=null){
                PointPoiItem pointPoiItem=(PointPoiItem)marker.getObject();
                if(poi.equals(pointPoiItem.getPoiDescription())){
                    return true;
                }
            }
        }
        return false;
    }

    private PointPoiItem buildPointPoiItem(double lat,double lng,String formatAddress,String district,
                                           String city, String province,String townShip,String neighborhood,
                                           String point,int distance, String poiId,String snippet){
        PointPoiItem pointPoiItem=new PointPoiItem();
        pointPoiItem.setLat(lat);
        pointPoiItem.setLng(lng);
        pointPoiItem.setFormatAddress(formatAddress);
        pointPoiItem.setDistrict(district);
        pointPoiItem.setCity(city);
        pointPoiItem.setProvince(province);
        pointPoiItem.setToweShip(townShip);
        pointPoiItem.setNeighborhood(neighborhood);
        pointPoiItem.setPoiDescription(point);
        pointPoiItem.setDistance(distance);
        pointPoiItem.setPoiId(poiId);
        pointPoiItem.setSnippet(snippet);
        return pointPoiItem;
    }

    private static final Comparator<PoiItem> comparator = new Comparator<PoiItem>() {
        public int compare(PoiItem p1, PoiItem p2) {
            if (p1.getDistance() != p2.getDistance()) {
                return p1.getDistance() - p2.getDistance();
            } else if (!p1.toString().equals(p2.toString())) {
                return p1.toString().compareTo(p2.toString());
            } else {
                return p1.getPoiId().compareTo(p2.getPoiId());
            }
        }
    };

    private void showMapErrorMsg(Marker currentAddMarker,String msg,boolean isGDMsg,int rCode){
        if(isGDMsg){
            ToastUtil.showerror(getApplicationContext(), rCode);
        }else{
            UtilToolkit.showToast(msg);
        }
        if(canMapEdit){
            if(currentAddMarker!=null && currentAddMarker.isVisible()){
                if(currentAddMarker.isInfoWindowShown()) currentAddMarker.hideInfoWindow();
                currentAddMarker.remove();
                aMap.invalidate();
            }
        }else{
            progress_search_result.showContent();
        }
    }

    private PointPoiItem isAddedMarker(PointPoiItem spointPoiItem){
        for(Marker marker:pickedMarkerList){
            if(marker.getObject()!=null){
                PointPoiItem pointPoiItem=(PointPoiItem) marker.getObject();
                if(spointPoiItem.getPoiDescription().equals(pointPoiItem.getPoiDescription())){
                    spointPoiItem.setServerId(pointPoiItem.getServerId());
                    spointPoiItem.setAdded(true);
                }
            }
        }
        return spointPoiItem;
    }

    private void initPointAdapter(List<PointPoiItem> poiItems){
        progress_search_result.showContent();
        pointAdapter=new PickSendPointAdapter(PickAndSendMapActivity.this,poiItems);
        poi_recycler.setAdapter(pointAdapter);
        pointAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final ProgressActivity progressActivity= (ProgressActivity) view.findViewById(R.id.progress_pick_poi);
                PointPoiItem pointPoiItem=pointAdapter.getData().get(position);
                if(pointPoiItem.isAdded()){//已添加点击删除
                    if(progressActivity!=null) progressActivity.showLoading();
                    deleteCMTakePoint(pointPoiItem.getServerId(),pointPoiItem,position);
                }else{//未添加点击标记
                    if(progressActivity!=null) progressActivity.showLoading();
                    addCMTakePoint(pointPoiItem,null,position);
                }
            }
        });
        setPointCounts();
        if(!IsGuid.activityIsGuided(getApplicationContext(),PickAndSendMapActivity.this.getClass().getName())){
            showGuidViewDelayed(3);
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mLocationChangedListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(false);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    @Override
    public void onMapLoaded() {
        getMyPoint();
    }

    @Override
    public void deactivate() {
        mLocationChangedListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        mLocationOption=null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(canMapEdit) dismissAllAnotherInfoWindow(null);
    }

    private void dismissAllAnotherInfoWindow(Marker currentMarker){
        for(Marker marker:pickedMarkerList){
            if(marker.equals(currentMarker)){
                continue;
            }
            if(marker.isInfoWindowShown())
            marker.hideInfoWindow();
        }
        Iterator<Integer> iterator=addMarkerMap.keySet().iterator();
        while(iterator.hasNext()){
            int key=iterator.next();
            Marker marker=addMarkerMap.get(key);
            if(marker.equals(currentMarker)){
                continue;
            }
            if(marker.isInfoWindowShown()) marker.hideInfoWindow();
            if(marker.isVisible()) {
                marker.remove();
                aMap.invalidate();
            }
            addMarkerMap.remove(key);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(canMapEdit){
            if(isHasAddedMarker(latLng.hashCode())){//该方法较鸡肋,几乎不能点击出两个hashCode一致的点出来,做的好的话应加上周围10米内的判断
                UtilToolkit.showToast("已标记该位置,请继续操作");
                return;
            }
            dismissAllAnotherInfoWindow(null);
            AddMarkers(latLng);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mLocationChangedListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mLocationChangedListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                locaticonLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                currentCity=amapLocation.getCity();
            }
        }else {
            String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
            KLog.e("kb",errText);
            UtilToolkit.showToast(errText);
        }
    }

    private void moveCenterPoint(LatLng latLng){
        if(latLng!=null) aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
    }

    @Override
    public View getInfoContents(Marker marker) {
       return null;
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(canMapEdit){
            dismissAllAnotherInfoWindow(marker);
            final String snippet = marker.getSnippet();
            if("addMark".equals(snippet)){
                if(!marker.isInfoWindowShown()) marker.showInfoWindow();
            }else if("pickedMarker".equals(snippet)){
                if(!marker.isInfoWindowShown()) marker.showInfoWindow();
//                PointPoiItem pointPoiItem=(PointPoiItem) marker.getObject();
//                if(pointPoiItem!=null) getPoiList(pointPoiItem);
            }
        }
        return false;
    }
//    private void getPoiList(final PointPoiItem pointPoiItem){
//        final ApiWrapper apiWrapper=new ApiWrapper();
//        Map<String,String> params=new HashMap<>();
//        params.put("sname","cm_take_point");
//        params.put("pname","androids");
//        params.put("lat",MapUtils.formatLatLngRound(pointPoiItem.getLat()));
//        params.put("lng",MapUtils.formatLatLngRound(pointPoiItem.getLng()));
//        Subscription subscription=apiWrapper.getPoiPoint("cm_take_range", params)
//                .doOnError(new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable e) {
//                        Message message=new Message();
//                        message.what=0XA2;
//                        if (e instanceof RetrofitUtil.APIException) {
//                            RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;
//                            message.obj=exception.msg;
//                        } else {
//                            message.obj="未知错误";
//                        }
//                        mHandler.handleMessage(message);
//                    }
//                })
//                .subscribe(newSubscriber(new Action1<JSONObject>() {
//                    @Override
//                    public void call(JSONObject jsonData) {
//                        JSONArray list=jsonData.getJSONArray("list");
//                        if(list!=null && list.size()>0){
//                            List<CMRangePoint> cmRangePoints=new ArrayList<>();
//                            for(int i=0;i<list.size();i++){
//                                CMRangePoint cmRangePoint=JSON.parseObject(list.getJSONObject(i).toJSONString(),CMRangePoint.class);
//                                cmRangePoint.setAmap_lat(pointPoiItem.getLat()+"");
//                                cmRangePoint.setAmap_lng(pointPoiItem.getLng()+"");
//                                cmRangePoints.add(cmRangePoint);
//                            }
//                            Message message=new Message();
//                            message.what=0XA1;
//                            message.obj=cmRangePoints;
//                            mHandler.handleMessage(message);
//                        }else{
//                            Message message=new Message();
//                            message.what=0XA2;
//                            message.obj="POI列表为空";
//                            mHandler.handleMessage(message);
//                        }
//                    }
//                }));
//        mCompositeSubscription.add(subscription);
//    }
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0XB1:
                    if(infoContent_Add!=null && infoContent_Add.findViewById(R.id.progresslayout_add_poi)!=null){
                        ProgressActivity progressActivity=(ProgressActivity)infoContent_Add.findViewById(R.id.progresslayout_add_poi);
                        progressActivity.showContent();
                        Marker marker=(Marker)msg.obj;
                        TextView titleUi = ((TextView) infoContent_Add.findViewById(R.id.tv_marker_name));
                        titleUi.setText(marker.getTitle()==null?"":marker.getTitle());
                    }
                    break;
//                case 0XA1://获取POI列表成功
//                    if(infoContent!=null && infoContent.findViewById(R.id.poi_list_recycler)!=null){
//                        ProgressActivity progressActivity=(ProgressActivity)infoContent.findViewById(R.id.progresslayout_poi_list);
//                        progressActivity.showContent();
//                        PoiListAdapter adapter=new PoiListAdapter(PickAndSendMapActivity.this,(List<CMRangePoint>)msg.obj);
//                        RecyclerView recycler=(RecyclerView) infoContent.findViewById(R.id.poi_list_recycler);
//                        recycler.setAdapter(adapter);
//                        adapter.setOnClickDelete(new PoiListAdapter.OnClickDelete() {
//                            @Override
//                            public void onDeletePoint(String serverId,int position) {
//                                if(!TextUtils.isEmpty(serverId)){
//                                    deleteCMTakePoint(serverId,null,position);
//                                }
//                            }
//                        });
//                    }
//                    break;
//                case 0XA2://获取POI列表发生错误
//                    if(infoContent!=null && infoContent.findViewById(R.id.progresslayout_poi_list)!=null){
//                        ProgressActivity progressActivity=(ProgressActivity)infoContent.findViewById(R.id.progresslayout_poi_list);
//                        progressActivity.showContent();
//                    }
//                    UtilToolkit.showToast(""+msg.obj);
//                    break;
                case 0XD1://删除POI成功
                    if(infoContent!=null && infoContent.findViewById(R.id.poi_list_recycler)!=null){
                        RecyclerView recyclerView=(RecyclerView)infoContent.findViewById(R.id.poi_list_recycler);
                        View view=recyclerView.getChildAt((int)msg.obj);
                        ProgressActivity progressActivity=(ProgressActivity)view.findViewById(R.id.rl_right_body);
                        if(progressActivity!=null){
                            progressActivity.showContent();
                        }
                        PoiListAdapter adapter=(PoiListAdapter)recyclerView.getAdapter();
                        double lat=0;
                        double lng=0;
                        if(adapter!=null && adapter.getData()!=null){
                            if(adapter.getData().size()>0){
                                lat=adapter.getData().get(0).getLat();
                                lng=adapter.getData().get(0).getLng();
                            }
                            if(adapter.getData().size()>1){
                                adapter.getData().remove((int)msg.obj);
                                adapter.notifyDataSetChanged();
                                Marker marker=getPickedMarkerByLatLng(new LatLng(lat,lng));
                                if(marker!=null){
                                    pickedMarkerList.remove(marker);
                                    marker.remove();
                                    aMap.invalidate();
                                }
                            }else{
                                Marker marker=getPickedMarkerByLatLng(new LatLng(lat,lng));
                                if(marker!=null){
                                    if(marker.isInfoWindowShown()) marker.hideInfoWindow();
                                    pickedMarkerList.remove(marker);
                                    marker.remove();
                                    aMap.invalidate();
                                }
                            }
                        }
                    }
                    break;
                case 0XD2://删除POI失败
                    if(infoContent!=null && infoContent.findViewById(R.id.poi_list_recycler)!=null){
                        RecyclerView recyclerView=(RecyclerView)infoContent.findViewById(R.id.poi_list_recycler);
                        View view=recyclerView.getChildAt((int)msg.obj);
                        ProgressActivity progressActivity=(ProgressActivity)view.findViewById(R.id.rl_right_body);
                        if(progressActivity!=null){
                            progressActivity.showContent();
                        }
                    }
                    break;
                case 0XD3://隐藏poi List的Progress
                    if(poi_recycler!=null){
                        final View view=poi_recycler.getChildAt((int)msg.obj);
                        if(view !=null && view.findViewById(R.id.progress_pick_poi)!=null){
                            final ProgressActivity progressActivity=(ProgressActivity)view.findViewById(R.id.progress_pick_poi);
                            if(progressActivity!=null){
                                progressActivity.showContent();
                            }
                        }
                    }
                    break;
                default:break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public View getInfoWindow(final Marker marker) {
        final String snippet = marker.getSnippet();
        if("addMark".equals(snippet)){
            infoContent_Add = getLayoutInflater().inflate(R.layout.mark_popup_layout1, null);
            final ProgressActivity progressActivity=(ProgressActivity)infoContent_Add.findViewById(R.id.progresslayout_add_poi);
            progressActivity.showLoading();
            final ProgressActivity rl_right_body=(ProgressActivity)infoContent_Add.findViewById(R.id.rl_right_body);
            rl_right_body.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        final PointPoiItem pointPoiItem=(PointPoiItem)marker.getObject();
                        if(pointPoiItem!=null){
                            if(rl_right_body!=null) rl_right_body.showLoading();
                            addCMTakePoint(pointPoiItem,rl_right_body,0);
                        }
                }
            });
            return infoContent_Add;
        }else if("pickedMarker".equals(snippet)){
            infoContent = getLayoutInflater().inflate(R.layout.mark_popup_layout2, null);
            RecyclerView recycler=(RecyclerView) infoContent.findViewById(R.id.poi_list_recycler);
            recycler.setLayoutManager(new LinearLayoutManager(this));
            recycler.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                    .color(getResources().getColor(R.color.gray_4))
                    .size(getResources().getDimensionPixelSize(R.dimen.recyle_divider_size))
                    .build());  //添加分割线
//            ProgressActivity progressActivity=(ProgressActivity)infoContent.findViewById(R.id.progresslayout_poi_list);
//            progressActivity.showLoading();
            PoiListAdapter adapter=new PoiListAdapter(getPointPoiByLatLng(marker.getPosition()));
            recycler.setAdapter(adapter);
            adapter.setOnClickDelete(new PoiListAdapter.OnClickDelete() {
                @Override
                public void onDeletePoint(String serverId,int position) {
                    if(!TextUtils.isEmpty(serverId)){
                        deleteCMTakePoint(serverId,null,position);
                    }
                }
            });
            return infoContent;
        }
        UtilToolkit.showToast("数据错误");
        return null;
    }
    private List<PointPoiItem> getPointPoiByLatLng(LatLng latlng){
        List<PointPoiItem> points=new ArrayList<>();
        for(Marker marker:pickedMarkerList){
            if(marker.getPosition().equals(latlng)){
                if(marker.getObject()!=null){
                    points.add((PointPoiItem)marker.getObject());
                }
            }
        }
        return points;
    }

    private Marker getMarkerByPoi(String poi){
        for(Marker marker:pickedMarkerList){
            if(marker.getObject()!=null){
                PointPoiItem pointPoiItem=(PointPoiItem) marker.getObject();
                if(poi.equals(pointPoiItem.getPoiDescription())){
                    return marker;
                }
            }
        }
        return null;
    }

    private Marker getPickedMarkerByLatLng(LatLng latLng){
        for(Marker marker:pickedMarkerList){
//            PointPoiItem pointPoiItem=(PointPoiItem) marker.getObject();
//            if(pointPoiItem!=null && lat.equals(pointPoiItem.getLat()+"") && lng.equals(pointPoiItem.getLng()+"")){
//                return marker;
//            }
            if(marker.getPosition().equals(latLng)){
                return marker;
            }
        }
        return null;
    }

    private void deleteCMTakePoint(String serverId,final PointPoiItem pointPoiItem,final int position){
        Map<String,String> params=new HashMap<>();
        if(canMapEdit){
            params.put("id",serverId);
        }else{
            params.put("id",pointPoiItem.getServerId());
        }
        params.put("sname","cm_take_range_operate");
        params.put("pname","androids");
        params.put("type","cancel");
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.deleteCMPoint("cm_take_range_operate", params)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        Message message=new Message();
                        message.obj=position;
                        message.what=canMapEdit?0XD2:0XD3;
                        mHandler.handleMessage(message);
                        if (e instanceof RetrofitUtil.APIException) {
                            RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;
                            UtilToolkit.showToast(exception.msg);
                        } else {
                            UtilToolkit.showToast("未知错误");
                        }
                    }
                })
                .subscribe(newSubscriber(new Action1<JSONObject>() {
                    @Override
                    public void call(JSONObject jsonData) {
                        if(canMapEdit) {
                            if(!TextUtils.isEmpty(jsonData.getString("id")) && "cancel".equals(jsonData.getString("type"))){
                                Message message=new Message();
                                message.obj=position;
                                message.what=0XD1;
                                mHandler.handleMessage(message);
                                setPointCounts();
                                UtilToolkit.showToast("删除成功");
                            }else{
                                Message message=new Message();
                                message.obj=position;
                                message.what=0XD2;
                                mHandler.handleMessage(message);
                                UtilToolkit.showToast("删除失败");
                            }
                        }else{
                                pointPoiItem.setAdded(false);
                                if(pointAdapter!=null) pointAdapter.notifyItemChanged(position);
                                Marker marker=getMarkerByPoi(pointPoiItem.getPoiDescription());
                                if(marker!=null){
                                    pickedMarkerList.remove(marker);
                                    marker.remove();
                                    aMap.invalidate();
                                }
                            setPointCounts();
                        }
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    private void addCMTakePoint(final PointPoiItem pointPoiItem,final ProgressActivity progressActivity,final int position){
        Map<String,String> params=new HashMap<>();
        params.put("address",pointPoiItem.getSnippet());
        params.put("area",pointPoiItem.getProvince()+pointPoiItem.getDistrict()+pointPoiItem.getToweShip());
        params.put("point",pointPoiItem.getPoiDescription());
        params.put("sname","cm_take_range_add");
        params.put("pname","androids");
        params.put("lat", MapUtils.formatLatLngRound(pointPoiItem.getLat()));
        params.put("lng",MapUtils.formatLatLngRound(pointPoiItem.getLng()));
        final ApiWrapper apiWrapper=new ApiWrapper();
        Subscription subscription=apiWrapper.addCMPoint("cm_take_range_add", params)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        if(!canMapEdit){
                            Message message=new Message();
                            message.obj=position;
                            message.what=0XD3;
                            mHandler.handleMessage(message);
                        }else{
                            if(progressActivity!=null) progressActivity.showContent();
                        }
                        if (e instanceof RetrofitUtil.APIException) {
                            RetrofitUtil.APIException exception = (RetrofitUtil.APIException) e;
                            UtilToolkit.showToast(exception.msg);
                        } else {
                            UtilToolkit.showToast("未知错误");
                        }
                    }
                })
                .subscribe(newSubscriber(new Action1<CMRangePoint>() {
                    @Override
                    public void call(CMRangePoint cMRangePoint) {
                        if(canMapEdit){
                            if(progressActivity!=null) progressActivity.showContent();
                            pointPoiItem.setServerId(cMRangePoint.getId());
                            pointPoiItem.setAdded(true);
                            LatLng latLng=new LatLng(pointPoiItem.getLat(),pointPoiItem.getLng());
                            Marker marker=addMarkerMap.get(latLng.hashCode());
                            if(marker!=null){
                                marker.setSnippet("pickedMarker");
                                if(marker.isInfoWindowShown()) marker.hideInfoWindow();
                                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.takerange_mark_recommend));
                                mMapView.invalidate();
                                pickedMarkerList.add(marker);
                                addMarkerMap.remove(latLng.hashCode());
                            }
                            setPointCounts();
                            UtilToolkit.showToast("添加成功");
                        }else{
                                pointPoiItem.setAdded(true);
                                pointPoiItem.setServerId(cMRangePoint.getId());
                                pointAdapter.notifyItemChanged(position);
                                MarkerOptions markerOption = new MarkerOptions();
                                markerOption.position(new LatLng(pointPoiItem.getLat(),pointPoiItem.getLng()));
                                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.takerange_mark_recommend));
                                Marker pickedMarker = aMap.addMarker(markerOption);
                                pickedMarker.setVisible(false);
                                pickedMarker.setTitle(pointPoiItem.getProvince()+pointPoiItem.getDistrict()+pointPoiItem.getToweShip()+pointPoiItem.getSnippet());
                                pickedMarker.setSnippet("pickedMarker");
                                pickedMarker.setObject(pointPoiItem);
                                pickedMarkerList.add(pickedMarker);
                                setPointCounts();
                        }
                    }
                }));
        mCompositeSubscription.add(subscription);
    }

    private void setPointCounts(){
        if(pickedMarkerList!=null && pickedMarkerList.size()>0){
            tv_control_switch.setText("共标记"+pickedMarkerList.size()+"个取派点");
            Drawable img_left = getResources().getDrawable(R.drawable.icon_sign_review);
            img_left.setBounds(0, 0, img_left.getMinimumWidth(), img_left.getMinimumHeight());
            tv_control_switch.setCompoundDrawables(img_left,null,null,null);
        }else{
            tv_control_switch.setText("添加取派范围");
            Drawable img_left = getResources().getDrawable(R.drawable.icon_add);
            img_left.setBounds(0, 0, img_left.getMinimumWidth(), img_left.getMinimumHeight());
            tv_control_switch.setCompoundDrawables(img_left,null,null,null);
        }
        tv_description_rang_count.setText("已设取派点："+pickedMarkerList.size()+"个");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        deactivate();
        this.aMap=null;
        this.mgGeocodeSearch=null;
        this.pointAdapter=null;
        this.infoContent=null;
        this.locaticonLatLng=null;
        this.pickedMarkerList.clear();
        this.addMarkerMap.clear();
        this.infoContent_Add=null;
        this.mBaseQuickAdapter=null;
        this.mHandler=null;
        this.mGlobalSearchView=null;
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private void jumpCenterMarker(){
        TranslateAnimation up = new TranslateAnimation(0, 0, 0,-iv_center_marker.getMeasuredHeight());
        up.setInterpolator(new AccelerateDecelerateInterpolator());
        up.setDuration(250);
        up.setRepeatCount(1);//设置重复次数
        up.setRepeatMode(Animation.REVERSE);//设置反方向执行
        iv_center_marker.startAnimation(up);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition position) {
        if(!canMapEdit){
            if(lastCenterPosition==null || !lastCenterPosition.equals(position.target)){
                lastCenterPosition=position.target;
                progress_search_result.showLoading();
                doRegecodeQuery(new LatLonPoint(position.target.latitude,position.target.longitude));
                jumpCenterMarker();
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition position) {

    }

}
