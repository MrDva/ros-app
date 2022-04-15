package edu.czb.ros_app.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import edu.czb.ros_app.R;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import edu.czb.ros_app.ui.dialog.CorrectLatLngDialog;
import edu.czb.ros_app.utils.ResourceUtil;
import edu.czb.ros_app.viewmodel.MapViewModel;
import edu.czb.ros_app.widgets.map.MapPathData;
import edu.czb.ros_app.widgets.map.MapPathEntity;
import edu.czb.ros_app.widgets.map.MyPoint32;
import geometry_msgs.Point32;
import geometry_msgs.PolygonStamped;
import sensor_msgs.NavSatFix;

public class MapFragment extends Fragment {
    public final static String INFO="info";
    public final static String LAT_CORR_FACTOR="lat_corr_factor_key";
    public final static String LNG_CORR_FACTOR="lng_corr_factor_key";
    public final static String CURR_LAT="curr_lat";
    public final static String CURR_LNG="curr_lng";
    private final static String TAG=MapFragment.class.getSimpleName();
    private MapView mapView=null;
    private BaiduMap baiduMap;
    private LocationClient mLocationClient;
    private boolean isFirst=true;
    private BDLocation currentLocation;
    private FloatingActionButton myLocation;
    private FloatingActionButton button_send;
    private FloatingActionButton button_clean;
    private FloatingActionButton button_correct;
    private BDLocation rosCurrentLocation;
    private List<LatLng> latLngList=new ArrayList<>();
    private List<LatLng> myLatLngList=new ArrayList<>();
    private List<BDLocation> destLocations= new ArrayList<>();
    private List<OverlayOptions> destOptions=new ArrayList<>();
    private List<Integer> sortDestLocations=new ArrayList<>();
    private Integer index=-1;
    private BDLocation boatLocation=null;
    private MutableLiveData<MapPathData> mapPathData=new MutableLiveData<>();
    private MapViewModel mapViewModel;
    private double latitude_correction_factor=0;
    private double longitude_correction_factor=0;
    private TextView destMarker;
    private TextView boatMarkerLat;
    private TextView boatMarkerLng;
    private TextView myMarkerLat;
    private TextView myMarkerLng;

    private CorrectLatLngDialog correctLatLngDialog;



    private MapViewModel viewModel;

    public static MapFragment newInstance() {
        return new MapFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapViewModel=new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        viewModel=new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        mapView=getView().findViewById(R.id.bmapView);
        myLocation = getView().findViewById(R.id.my_location);
        button_clean=getView().findViewById(R.id.button_clean);
        button_send=getView().findViewById(R.id.button_send);
        button_correct=getView().findViewById(R.id.button_correct);

        View view=LayoutInflater.from(getContext()).inflate(R.layout.marker,null);
        destMarker=view.findViewById(R.id.destLocation);
        view=LayoutInflater.from(getContext()).inflate(R.layout.marker_boat,null);
        boatMarkerLat=view.findViewById(R.id.boatLocation_lat);
        boatMarkerLng=view.findViewById(R.id.boatLocation_lng);
        view=LayoutInflater.from(getContext()).inflate(R.layout.marker_mylocation,null);
        myMarkerLat=view.findViewById(R.id.my_location_lat);
        myMarkerLng=view.findViewById(R.id.my_location_lng);

        myLocation.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8844ddEE")));
        button_send.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#88ffeecc")));
        button_clean.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#98ccFF22")));
        button_correct.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0000ffff")));
        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        mapPathData.setValue(new MapPathData(new ArrayList<>()));
        BaiduMapOptions options = new BaiduMapOptions();
        options.mapType(BaiduMap.MAP_TYPE_SATELLITE);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18.0f);
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mLocationClient = new LocationClient(getContext());
        updateLatLngInfo();
//通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

//设置locationClientOption
        mLocationClient.setLocOption(option);

//注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
//开启地图定位图层
        mLocationClient.start();

        viewModel.getRosDate().observe(getViewLifecycleOwner(),rosData -> {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(TopicName.TOPIC_KEY, Context.MODE_PRIVATE);
            if(rosData.getTopic().name.equals(sharedPreferences.getString(TopicName.MAP,TopicName.MAP))){
               // updateLatLngInfo();
                updateRosLocation((NavSatFix) rosData.getMessage());
            }/*else if(rosData.getTopic().name.toLowerCase().contains("imu")){
                binding.imu.setText(rosData.getMessage().toString());
            }else if(rosData.getTopic().name.toLowerCase().contains("navSatFix")){
                binding.navSatFix.setText(rosData.getMessage().toString());
            }else if(rosData.getTopic().name.toLowerCase().contains("temperature")){
                binding.temperature.setText(rosData.getMessage().toString());
            }else if(rosData.getTopic().type.contains("Float32")){
                binding.other.setText(rosData.getTopic().name+":"+((Float32)rosData.getMessage()).getData());
            }*/
        });

        myLocation.setOnClickListener(v -> {
            if(rosCurrentLocation==null){
                return;
            }
            updateMapStatus(rosCurrentLocation);
        });
        button_clean.setOnClickListener(v->{
            if(destLocations.isEmpty()){
                return;
            }
            List<LatLng> latLngs = destLocations.stream().map(
                    destLocation -> new LatLng(destLocation.getLatitude(), destLocation.getLongitude()))
                    .collect(Collectors.toList());
            removeOverLayList(latLngs);
            destLocations.clear();
            destOptions.clear();
            sortDestLocations.clear();
            Toast.makeText(getContext(),"目的地清理完成",Toast.LENGTH_SHORT).show();
        });
        button_send.setOnClickListener(v->{
            //updateRosLocationTest(currentLocation.getLongitude(),currentLocation.getLatitude(),0);
            if(destLocations.isEmpty()){
                return;
            }
            sendPath();
        });
        button_correct.setOnClickListener(v->{
            showEditDialog(v);
        });
       /* baiduMap.setOnMapTouchListener(v->{
            LatLng latLng = baiduMap.getProjection().fromScreenLocation(new Point((int) v.getX(), (int) v.getY()));
            Log.i(TAG,"latLng:"+latLng.latitude+" "+latLng.longitude);
        });*/
        baiduMap.setOnMapLongClickListener(v->{
            double longitude = v.longitude;
            double latitude = v.latitude;
            //Log.i(TAG,"latLng222:"+latitude+" "+ longitude);
            AlertDialog.Builder builder1=new AlertDialog.Builder(getContext());
            builder1.setMessage("确定将经纬度("+(latitude-latitude_correction_factor)+","+ (longitude-longitude_correction_factor) +")设置为目的地？");
            builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*if(destLocation!=null){
                        Toast.makeText(getContext(),"请先清理已有的目的地址",Toast.LENGTH_SHORT).show();
                        return;
                    }*/
                    BDLocation destLocation=new BDLocation();
                    destLocation.setLatitude(latitude);
                    destLocation.setLongitude(longitude);
                    int index=sortDestLocations.size();
                    destMarker.setText(new StringBuilder().append(index));
                    sortDestLocations.add(index);
                    //Bitmap n = ResourceUtil.getBitmapFromVectorDrawable(BMapManager.getContext(),R.drawable.ic_baseline_room_24);
                    Bitmap n=getViewBitmap(destMarker);
                    BitmapDescriptor bitmap= BitmapDescriptorFactory.fromBitmap(n);
                    MarkerOptions destOption = new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .icon(bitmap);
                    destLocations.add(destLocation);
                    destOptions.add(destOption);
                    Marker marker = (Marker)baiduMap.addOverlay(destOption);
                    marker.setTitle(index+"");
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("id",index);
                    marker.setExtraInfo(bundle);
                }
            });
            builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder1.show();
        });
        BaiduMap.OnMarkerClickListener onMarkerClickListener=new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle=marker.getExtraInfo();
                index=-1;
                try {
                    index =(Integer) bundle.getSerializable("id");
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(sortDestLocations.contains(index)){
                    AlertDialog.Builder builder1=new AlertDialog.Builder(getContext());
                    builder1.setMessage("确定取消目的地("+index+")?");
                    builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BDLocation destLocation=destLocations.get(index);
                            LatLng latLng = new LatLng(destLocation.getLatitude(), destLocation.getLongitude());
                            removeOverLays(latLng);
                            destOptions.remove((int)index);
                            destLocations.remove((int)index);
                            sortDestLocations.remove((int)index);
                            updateMarkerList();
                        }
                    });
                    builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder1.show();
                }
                return false;
            }
        };
        baiduMap.setOnMarkerClickListener(onMarkerClickListener);
        mapPathData.observe(getViewLifecycleOwner(),mapPathData1 -> {
            mapViewModel.getRosDomain().publicData(mapPathData1);
        });


    }

    @Override
    public void onDestroyView(){
        mLocationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView=null;
        super.onDestroyView();
    }

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mapView == null){
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            baiduMap.setMyLocationData(locData);

            currentLocation=location;
            saveCurrLatLng(location.getLatitude(),location.getLongitude());

            LatLng rosPoint=new LatLng(location.getLatitude()-0.0001,location.getLongitude());
            myLatLngList.add(rosPoint);
            if(myLatLngList.size()>=2){
                LatLng latLng = myLatLngList.remove(0);
                removeOverLays(latLng);
            }
            updateLatLngInfo();
            myMarkerLat.setText("lat:["+String.format("%.6f",(location.getLatitude()-latitude_correction_factor))+"]");
            myMarkerLng.setText("lng:["+String.format("%.6f",(location.getLongitude()-longitude_correction_factor))+"]");

            Context var1 = BMapManager.getContext();
            //Bitmap n = ResourceUtil.getBitmapFromVectorDrawable(BMapManager.getContext(),R.drawable.ic_baseline_directions_boat_24);
            Bitmap n=getViewText(myMarkerLat,myMarkerLng);
            BitmapDescriptor bitmap= BitmapDescriptorFactory.fromBitmap(n);
            OverlayOptions options=new MarkerOptions()
                    .position(rosPoint)
                    .icon(bitmap);
            baiduMap.addOverlay(options);

            if(isFirst){
                updateMapStatus(location);
                isFirst=false;
            }
        }


    }

    private void updateMapStatus(BDLocation location){
        LatLng GEO_MyLocation=new LatLng(location.getLatitude(),location.getLongitude());
        MapStatusUpdate statusUpdate=MapStatusUpdateFactory.newLatLng(GEO_MyLocation);
        baiduMap.setMapStatus(statusUpdate);
    }
    /*public void updateRosLocationTest(double lon ,double lat,double alt){
        boatLocation=new BDLocation();
        boatLocation.setLongitude(lon+longitude_correction_factor);
        boatLocation.setAltitude(alt);
        boatLocation.setLatitude(lat+latitude_correction_factor);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(boatLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(boatLocation.getDirection())
                .latitude(boatLocation.getLatitude())
                .longitude(boatLocation.getLongitude())
                .build();
        rosCurrentLocation=boatLocation;
        LatLng rosPoint=new LatLng(boatLocation.getLatitude(),boatLocation.getLongitude());
        latLngList.add(rosPoint);
        if(latLngList.size()>=2){
            LatLng latLng = latLngList.remove(0);
            removeOverLays(latLng);
        }
        Context var1 = BMapManager.getContext();
        Bitmap n = ResourceUtil.getBitmapFromVectorDrawable(BMapManager.getContext(),R.drawable.ic_baseline_directions_boat_24);
        BitmapDescriptor bitmap= BitmapDescriptorFactory.fromBitmap(n);
        OverlayOptions options=new MarkerOptions()
                .position(rosPoint)
                .icon(bitmap);
        baiduMap.addOverlay(options);
    }*/

    public void updateRosLocation(NavSatFix navSatFix){
        updateLatLngInfo();
        Log.i(TAG,"navSatFix:"+navSatFix.getAltitude()+":"+navSatFix.getLatitude());
        boatLocation=new BDLocation();
        boatLocation.setLongitude(navSatFix.getLongitude()+longitude_correction_factor);
        boatLocation.setAltitude(navSatFix.getAltitude());
        boatLocation.setLatitude(navSatFix.getLatitude()+latitude_correction_factor);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(boatLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(boatLocation.getDirection())
                .latitude(boatLocation.getLatitude())
                .longitude(boatLocation.getLongitude())
                .build();
        rosCurrentLocation=boatLocation;
        LatLng rosPoint=new LatLng(boatLocation.getLatitude(),boatLocation.getLongitude());
        latLngList.add(rosPoint);
        if(latLngList.size()>=2){
            LatLng latLng = latLngList.remove(0);
            removeOverLays(latLng);
        }
        boatMarkerLat.setText("lat:["+String.format("%.6f",navSatFix.getLatitude())+"]");
        boatMarkerLng.setText("lng:["+String.format("%.6f",navSatFix.getLongitude())+"]");

        //Bitmap n = ResourceUtil.getBitmapFromVectorDrawable(BMapManager.getContext(),R.drawable.ic_baseline_directions_boat_24);
        Bitmap n=getViewBoat(boatMarkerLat,boatMarkerLng);
        BitmapDescriptor bitmap= BitmapDescriptorFactory.fromBitmap(n);
        OverlayOptions options=new MarkerOptions()
                .position(rosPoint)
                .icon(bitmap);
        baiduMap.addOverlay(options);
    }
    private void removeOverLays(LatLng latLng){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng);
        LatLngBounds build = builder.build();
        List<Marker> markersInBounds = baiduMap.getMarkersInBounds(build);
        List<Overlay> overlays=new ArrayList<>();
        overlays.addAll(markersInBounds);
        baiduMap.removeOverLays(overlays);
    }
    private void removeOverLayList(List<LatLng> list){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(list);
        LatLngBounds build = builder.build();
        List<Marker> markersInBounds = baiduMap.getMarkersInBounds(build);
        List<Overlay> overlays=new ArrayList<>();
        overlays.addAll(markersInBounds);
        baiduMap.removeOverLays(overlays);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateMarkerList(){
        if(destLocations.isEmpty()||sortDestLocations.isEmpty()||destOptions.isEmpty()){
            return;
        }
        List<LatLng> latLngs = destLocations.stream().map(
                destLocation -> {
                    return new LatLng(destLocation.getLatitude(), destLocation.getLongitude());
                })
                .collect(Collectors.toList());
        removeOverLayList(latLngs);
        sortDestLocations.clear();
        for (int i = 0; i < destOptions.size(); i++) {
            MarkerOptions destOption=(MarkerOptions) destOptions.get(i);
            Marker marker = (Marker)baiduMap.addOverlay(destOption);
            int index=i;
            destMarker.setText(new StringBuilder().append(index));
            BitmapDescriptor markerIcon=BitmapDescriptorFactory.fromBitmap(getViewBitmap(destMarker));
            marker.setIcon(markerIcon);
            sortDestLocations.add(i);
            marker.setTitle(i+"");
            Bundle bundle=new Bundle();
            bundle.putSerializable("id",i);
            marker.setExtraInfo(bundle);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendPath(){
        if(destLocations.isEmpty()){
            return;
        }
        /*List<MyPoint32> list=new ArrayList<>();
        MyPoint32 myPoint32=new MyPoint32((float) destLocation.getLatitude(),(float) destLocation.getLongitude(),0.0f);
        list.add(myPoint32);*/
        List<MyPoint32> list = destLocations.stream().map(destLocation -> {
            return new MyPoint32((float) (destLocation.getLatitude()-latitude_correction_factor), (float) (destLocation.getLongitude()-longitude_correction_factor), 0.0f);
        })
                .collect(Collectors.toList());
        /*MyPolygon polygon=new MyPolygon();
        
        polygon.setPoints(list);*/
        MapPathEntity mapPathEntity=new MapPathEntity();

        MapPathData mapPathData = new MapPathData(list);
        mapPathData.setTopic(mapPathEntity.getTopic());
        this.mapPathData.postValue(mapPathData);
    }

    private Bitmap getViewBitmap(View addViewContent) {
        return getViewBitmap(addViewContent,R.layout.marker,R.id.destImage);
    }
    private Bitmap getViewBoat(View addViewContent,View addViewContent2){
        return getViewBitmap(addViewContent,addViewContent2,R.layout.marker_boat);
    }
    private Bitmap getViewBitmap(View addViewContent,int layoutId,int imageId) {
        addViewContent.setDrawingCacheEnabled(true);
        addViewContent.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0, addViewContent.getMeasuredWidth(), addViewContent.getMeasuredHeight());
        addViewContent.buildDrawingCache();
        View view=LayoutInflater.from(getContext()).inflate(layoutId,null);
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        ImageView imageView=view.findViewById(imageId);
        Bitmap cacheBitmap = view.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        Canvas canvas = new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(Math.min(bitmap.getHeight()/2,bitmap.getWidth()/2));
        canvas.drawText(((TextView)addViewContent).getText().toString(),0,imageView.getHeight(),paint);
        return bitmap;
    }
    private Bitmap getViewBitmap(View addViewContent,View addViewContent2,int layoutId) {
        View view=LayoutInflater.from(getContext()).inflate(layoutId,null);
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        Canvas canvas = new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(Math.min(bitmap.getHeight()/6,bitmap.getWidth()/6));
        canvas.drawText(((TextView)addViewContent).getText().toString(),bitmap.getWidth()/6,bitmap.getHeight()/2,paint);
        canvas.drawText(((TextView)addViewContent2).getText().toString(),bitmap.getWidth()/6,bitmap.getHeight()/6+bitmap.getHeight()/2,paint);
        return bitmap;
    }
    private Bitmap getViewText(View addViewContent,View addViewContent2) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.marker_mylocation,null);
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        Canvas canvas = new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(Math.min(bitmap.getHeight()/6,bitmap.getWidth()/6));
        canvas.drawText(((TextView)addViewContent).getText().toString(),bitmap.getWidth()/6,bitmap.getHeight()/2+bitmap.getHeight()/6,paint);
        canvas.drawText(((TextView)addViewContent2).getText().toString(),bitmap.getWidth()/6,bitmap.getHeight()/6+bitmap.getHeight()/2+bitmap.getHeight()/6,paint);
        return bitmap;
    }

    public void showEditDialog(View view){
        correctLatLngDialog=new CorrectLatLngDialog(getActivity(),R.style.AppTheme,onClickListener);
        correctLatLngDialog.show();

    }
    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_save_correct:
                    try {
                        latitude_correction_factor = currentLocation.getLatitude() - Double.parseDouble(Strings.isNullOrEmpty(correctLatLngDialog.text_lat.getText().toString())?(currentLocation.getLatitude()+""):correctLatLngDialog.text_lat.getText().toString());
                        longitude_correction_factor = currentLocation.getLongitude() - Double.parseDouble(Strings.isNullOrEmpty(correctLatLngDialog.text_lng.getText().toString())?(currentLocation.getLongitude()+""):correctLatLngDialog.text_lng.getText().toString());
                        saveLatLngCorrect(latitude_correction_factor, longitude_correction_factor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    correctLatLngDialog.cancel();
                    break;
                case R.id.btn_cancel:
                    correctLatLngDialog.cancel();
                    break;
            }
        }
    };

    private void saveLatLngCorrect(double lat,double lng){
        SharedPreferences info= getActivity().getSharedPreferences(INFO,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=info.edit();
        editor.putString(LAT_CORR_FACTOR,lat+"");
        editor.putString(LNG_CORR_FACTOR,lng+"");
        editor.commit();
        Log.i(TAG,"经纬度偏差值保存成功");
    }

    private void saveCurrLatLng(double lat,double lng){
        SharedPreferences info= getActivity().getSharedPreferences(INFO,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=info.edit();
        editor.putString(CURR_LAT,lat+"");
        editor.putString(CURR_LNG,lng+"");
        editor.commit();
        Log.i(TAG,"当前经纬度保存成功");
    }

    private void updateLatLngInfo(){
        SharedPreferences info= getActivity().getSharedPreferences(INFO,Context.MODE_PRIVATE);
        longitude_correction_factor=Double.parseDouble(info.getString(LNG_CORR_FACTOR,"0"));
        latitude_correction_factor=Double.parseDouble(info.getString(LAT_CORR_FACTOR,"0"));
        Log.i(TAG,"经纬度偏差值:["+latitude_correction_factor+" ,"+longitude_correction_factor+"]");
    }


}
