package edu.czb.ros_app.ui.fragments;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.czb.ros_app.R;
import edu.czb.ros_app.model.db.DataStorage;
import edu.czb.ros_app.model.entities.info.BatteryStateEntity;
import edu.czb.ros_app.model.entities.info.RpyDataEntity;
import edu.czb.ros_app.model.entities.info.TempDataEntity;
import edu.czb.ros_app.viewmodel.ControllerViewModel;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.ui.fragments
 * @ClassName: InfoChartFragment
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/14 20:25
 * @Version: 1.0
 */
public class InfoChartFragment extends Fragment {
    public static final long LIMIT=25;

    private FloatingActionButton bntBackToInfo;
    private NavController controller;

    private LineChart batteryChart;
    private LineChart rpyChart;
    private LineChart tempChart;

    private DataStorage dataStorage;

    private LiveData<List<BatteryStateEntity>> batteryData;
    private LiveData<List<RpyDataEntity>> rpyData;
    private LiveData<List<TempDataEntity>> tempData;

    private AlertDialog batteryDialog;
    private AlertDialog rpyDialog;
    private AlertDialog tempDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_info_chart, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller= Navigation.findNavController(getActivity(),R.id.fragment_container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataStorage=DataStorage.getInstance(getContext());

        batteryChart=(LineChart) getView().findViewById(R.id.chart_battery);
        rpyChart=(LineChart)getView().findViewById(R.id.chart_rpy);
        tempChart=(LineChart)getView().findViewById(R.id.chart_temp);

        bntBackToInfo=(FloatingActionButton)getView().findViewById(R.id.switch_info);
        bntBackToInfo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8881D4FA")));

        batteryData = dataStorage.getBatteryLimitList(LIMIT);
        batteryData.observe(getViewLifecycleOwner(),batteryLimitList -> {
            batteryChart.setData(new LineData(getBatteryDataSet(batteryLimitList)));
            batteryChart.invalidate();
        });

        rpyData=dataStorage.getRpyLimitList(LIMIT);
        rpyData.observe(getViewLifecycleOwner(),rpyDate->{
            rpyChart.setData(new LineData(getRpyDataset(rpyDate)));
            rpyChart.invalidate();
        });

        tempData=dataStorage.getTempLimitList(LIMIT);
        tempData.observe(getViewLifecycleOwner(),tempData->{
            tempChart.setData(new LineData(getTempDataset(tempData)));
            rpyChart.invalidate();
        });


        Description batteryDescription = new Description();
        batteryDescription.setText("电池状态");
        batteryChart.setDescription(batteryDescription);

        Description rpyDescription = new Description();
        rpyDescription.setText("欧拉角");
        rpyChart.setDescription(rpyDescription);

        Description tempDescription = new Description();
        tempDescription.setText("温度");
        tempChart.setDescription(tempDescription);

        batteryDialog=new AlertDialog.Builder(getContext())
                .setTitle("请选择要对电池相关数据进行的操作")
                /*.setNeutralButton("刷新",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        batteryData=dataStorage.getBatteryLimitList(LIMIT);
                    }
                })*/
                .setPositiveButton("删除数据", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataStorage.deleteAllBattery();
                        batteryData=dataStorage.getBatteryLimitList(LIMIT);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        rpyDialog=new AlertDialog.Builder(getContext())
                .setTitle("请选择要对欧拉角数据进行的操作")
                /*.setNeutralButton("刷新",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        batteryData=dataStorage.getBatteryLimitList(LIMIT);
                    }
                })*/
                .setPositiveButton("删除数据", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataStorage.deleteAllRpy();
                        rpyData=dataStorage.getRpyLimitList(LIMIT);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        tempDialog=new AlertDialog.Builder(getContext())
                .setTitle("请选择要对温度数据进行的操作")
                /*.setNeutralButton("刷新",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        batteryData=dataStorage.getBatteryLimitList(LIMIT);
                    }
                })*/
                .setPositiveButton("删除数据", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataStorage.deleteAllTempData();
                        tempData=dataStorage.getTempLimitList(LIMIT);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        batteryChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                batteryDialog.show();
                return false;
            }
        });

        rpyChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                rpyDialog.show();
                return false;
            }
        });

        tempChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tempDialog.show();
                return false;
            }
        });


        bntBackToInfo.setOnClickListener(v->{
            controller.navigate(R.id.action_to_info);
        });
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

    public List<ILineDataSet> getBatteryDataSet(List<BatteryStateEntity> list){
        List<Entry> capacityEntries=new ArrayList<>();
        List<Entry> currentEntries=new ArrayList<>();
        List<Entry> voltageEntries=new ArrayList<>();
        List<Entry> chargeEntries=new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            BatteryStateEntity entity = list.get(i);
            capacityEntries.add(new Entry(i,(float) entity.capacity));
            currentEntries.add(new Entry(i,(float) entity.current));
            voltageEntries.add(new Entry(i,(float) entity.voltage));
            chargeEntries.add(new Entry(i,(float) entity.charge));
        }
        LineDataSet capacityDataSet=new LineDataSet(capacityEntries,"功率/W");
        LineDataSet currentDataSet=new LineDataSet(currentEntries,"电流/A");
        LineDataSet voltageDataSet=new LineDataSet(voltageEntries,"电压/V");
        LineDataSet chargeDataSet=new LineDataSet(chargeEntries,"消耗电量/Wh");

        capacityDataSet.setColor(Color.BLUE);
        capacityDataSet.setValueTextColor(Color.BLUE);

        currentDataSet.setColor(Color.GREEN);
        currentDataSet.setValueTextColor(Color.GREEN);

        voltageDataSet.setColor(Color.RED);
        voltageDataSet.setValueTextColor(Color.RED);

        chargeDataSet.setColor(Color.YELLOW);
        chargeDataSet.setValueTextColor(Color.YELLOW);

        List<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(capacityDataSet);
        dataSets.add(currentDataSet);
        dataSets.add(chargeDataSet);
        dataSets.add(voltageDataSet);

        return dataSets;
    }

    public List<ILineDataSet> getRpyDataset(List<RpyDataEntity> list){
        List<Entry> pitchEntries=new ArrayList<>();
        List<Entry> rollEntries=new ArrayList<>();
        List<Entry> yawEntries=new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            RpyDataEntity entity = list.get(i);
            pitchEntries.add(new Entry(i,(float) entity.pitch));
            rollEntries.add(new Entry(i,(float) entity.roll));
            yawEntries.add(new Entry(i,(float) entity.yaw));
        }
        LineDataSet pitchDataSet=new LineDataSet(pitchEntries,"俯仰角");
        LineDataSet rollDataSet=new LineDataSet(rollEntries,"翻滚角");
        LineDataSet yawDataSet=new LineDataSet(yawEntries,"偏航角");


        pitchDataSet.setColor(Color.BLUE);
        pitchDataSet.setValueTextColor(Color.BLUE);

        rollDataSet.setColor(Color.GREEN);
        rollDataSet.setValueTextColor(Color.GREEN);

        yawDataSet.setColor(Color.RED);
        yawDataSet.setValueTextColor(Color.RED);


        List<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(pitchDataSet);
        dataSets.add(rollDataSet);
        dataSets.add(yawDataSet);

        return dataSets;
    }

    public List<ILineDataSet> getTempDataset(List<TempDataEntity> list){
        List<Entry> tempEntries=new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            TempDataEntity entity = list.get(i);
            tempEntries.add(new Entry(i,(float) entity.temp));
        }
        LineDataSet tempDataSet=new LineDataSet(tempEntries,"温度/℃");

        tempDataSet.setColor(Color.BLUE);
        tempDataSet.setValueTextColor(Color.BLUE);



        List<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(tempDataSet);
        return dataSets;
    }

}
