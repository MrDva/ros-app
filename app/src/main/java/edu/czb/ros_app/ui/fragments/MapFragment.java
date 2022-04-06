package edu.czb.ros_app.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import edu.czb.ros_app.R;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.utils.ResourceUtil;
import edu.czb.ros_app.viewmodel.MapViewModel;
import edu.czb.ros_app.widgets.map.MapPathData;
import edu.czb.ros_app.widgets.map.MapPathEntity;
import edu.czb.ros_app.widgets.map.MyPoint32;
import geometry_msgs.Point32;
import geometry_msgs.PolygonStamped;
import sensor_msgs.NavSatFix;

public class MapFragment extends Fragment {
    private final static String TAG=MapFragment.class.getSimpleName();
    private MapView mapView=null;
    private BaiduMap baiduMap;
    private LocationClient mLocationClient;
    private boolean isFirst=true;
    private BDLocation currentLocation;
    private FloatingActionButton myLocation;
    private FloatingActionButton button_send;
    private FloatingActionButton button_clean;
    private BDLocation rosCurrentLocation;
    private List<LatLng> latLngList=new ArrayList<>();
    private List<BDLocation> destLocations= new ArrayList<>();
    private List<OverlayOptions> destOptions=new ArrayList<>();
    private List<Integer> sortDestLocations=new ArrayList<>();
    private Integer index=-1;
    private BDLocation boatLocation=null;
    private MutableLiveData<MapPathData> mapPathData=new MutableLiveData<>();
    private MapViewModel mapViewModel;
    private double latitude_correction_factor=0.004003;
    private double longitude_correction_factor=0.010964;
    private TextView destMarker;


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
        View view=LayoutInflater.from(getContext()).inflate(R.layout.marker,null);
        destMarker=view.findViewById(R.id.destLocation);

        myLocation.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8844ddEE")));
        button_send.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#88ffeecc")));
        button_clean.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#98ccFF22")));
        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        mapPathData.setValue(new MapPathData(new ArrayList<>()));
        BaiduMapOptions options = new BaiduMapOptions();
        options.mapType(BaiduMap.MAP_TYPE_SATELLITE);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18.0f);
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mLocationClient = new LocationClient(getContext());

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
            if(rosData.getTopic().name.contains("navSatFix")){
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
            if(destLocations.isEmpty()){
                return;
            }
            sendPath();
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

    public void updateRosLocation(NavSatFix navSatFix){
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
        Context var1 = BMapManager.getContext();
        Bitmap n = ResourceUtil.getBitmapFromVectorDrawable(BMapManager.getContext(),R.drawable.ic_baseline_directions_boat_24);
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
            return new MyPoint32((float) destLocation.getLatitude(), (float) destLocation.getLongitude(), 0.0f);
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
        addViewContent.setDrawingCacheEnabled(true);
        addViewContent.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0, addViewContent.getMeasuredWidth(), addViewContent.getMeasuredHeight());
        addViewContent.buildDrawingCache();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.marker,null);
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        ImageView imageView=view.findViewById(R.id.destImage);
        Bitmap cacheBitmap = view.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        Canvas canvas = new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(Math.min(bitmap.getHeight()/2,bitmap.getWidth()/2));
        canvas.drawText(((TextView)addViewContent).getText().toString(),imageView.getWidth()/2,imageView.getHeight()/2,paint);
        return bitmap;
    }


}
