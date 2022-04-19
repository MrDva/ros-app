package edu.czb.ros_app.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.Strings;

import org.ros.internal.message.RawMessage;
import org.ros.internal.message.field.Field;
import org.ros.internal.message.field.FloatArrayField;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.czb.ros_app.R;
import edu.czb.ros_app.domain.RosDomain;
import edu.czb.ros_app.model.rosRepositories.message.RosData;
import edu.czb.ros_app.model.rosRepositories.message.Topic;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;
import edu.czb.ros_app.viewmodel.InfoViewModel;
import geometry_msgs.Vector3;
import sensor_msgs.BatteryState;
import sensor_msgs.Imu;
import sensor_msgs.NavSatFix;
import sensor_msgs.Temperature;
import std_msgs.Float64;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.ui.fragments
 * @ClassName: LogFragment
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/11 20:19
 * @Version: 1.0
 */
public class LogFragment extends Fragment {

    private AlertDialog alertDialog;
    private boolean[] isChecked/*=new boolean[]{false,false,false,false,false,false}*/;
    private String[] items/*=new String[]{"Map","Battery","IMU","Rpy","Temp","DestYam"}*/;
    private Map<String,Integer> map= new HashMap<>();
    public static final String LOG_KEY="log_key";
    public static final String SUB_CONTENT="sub_content";
    public static final String PUB_CONTENT="pub_content";
    private RosDomain rosDomain;
    private MutableLiveData<RosData> rosDate;
    FloatingActionButton buttonEdit;
    //FloatingActionButton buttonPub;
    List<String> subList=new ArrayList<>();
    //List<String> pubList=new ArrayList<>();
    TextView sub;
    //TextView pub;

    List<Topic> topicList;
    List<Topic> currentTopic;
    List<String> currentTopicName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        rosDomain=RosDomain.getInstance(getActivity().getApplication());
        rosDate=rosDomain.getRosData();
        sub=getView().findViewById(R.id.sub_text);
        //pub=getView().findViewById(R.id.sub_text);
        //buttonPub=getView().findViewById(R.id.btn_update_pub);
        //buttonSub=getView().findViewById(R.id.btn_update_pub);
        buttonEdit=getView().findViewById(R.id.btn_topic_checked);
        buttonEdit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8881D4FA")));
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(TopicName.TOPIC_KEY, Context.MODE_PRIVATE);




        rosDate.observe(getViewLifecycleOwner(),rosData -> {

           /*Set<String> subSet = sharedPreferences.getStringSet(SUB_CONTENT, new HashSet<>());
            ArrayList<String> subs = new ArrayList<>(subSet);*/
            if(subList.size()>1000){
                subList.remove(0);
            }
            //String msg="";
            Integer index = map.get(rosData.getTopic().name);
            if(index==null){
                return;
            }
            /*if(rosData.getTopic().name.equals(sharedPreferences.getString(TopicName.MAP,TopicName.MAP))&&isChecked[map.get(sharedPreferences.getString(TopicName.MAP,TopicName.MAP))]){
                NavSatFix navSatFix=(NavSatFix)rosData.getMessage();
                msg="NavSatFix:["+navSatFix.getLatitude()+","+navSatFix.getLongitude()+"]";
                subList.add(sdf.format(System.currentTimeMillis())+"  --  topicName:"+rosData.getTopic().name+"  type:"+rosData.getTopic().type+"  msg:"+msg+"\n");
            }else if(rosData.getTopic().name.equals(sharedPreferences.getString(TopicName.IMU,TopicName.IMU))&&isChecked[map.get(sharedPreferences.getString(TopicName.IMU,TopicName.IMU))]){
                Imu imu=(Imu) rosData.getMessage();
                msg="IMU:[x"+imu.getAngularVelocity().getX()+",y:"+imu.getAngularVelocity().getY()+",z:"+imu.getAngularVelocity().getZ()+"]";
                subList.add(sdf.format(System.currentTimeMillis())+"  --  topicName:"+rosData.getTopic().name+"  type:"+rosData.getTopic().type+"  msg:"+msg+"\n");
            }else if(rosData.getTopic().name.equals(sharedPreferences.getString(TopicName.BATTERY,TopicName.BATTERY))&&isChecked[map.get(sharedPreferences.getString(TopicName.BATTERY,TopicName.BATTERY))]){
                BatteryState batteryState=(BatteryState) rosData.getMessage();
                msg="BatteryState:["+batteryState.getPercentage()+"]";
                subList.add(sdf.format(System.currentTimeMillis())+"  --  topicName:"+rosData.getTopic().name+"  type:"+rosData.getTopic().type+"  msg:"+msg+"\n");
            }else if(rosData.getTopic().name.equals(sharedPreferences.getString(TopicName.RPY,TopicName.RPY))&&isChecked[map.get(sharedPreferences.getString(TopicName.RPY,TopicName.RPY))]){
                Vector3 vector3=(Vector3) rosData.getMessage();
                msg="RPY:["+"x:"+vector3.getX()+",y:"+vector3.getY()+",z:"+vector3.getZ()+"]";
                subList.add(sdf.format(System.currentTimeMillis())+"  --  topicName:"+rosData.getTopic().name+"  type:"+rosData.getTopic().type+"  msg:"+msg+"\n");
            }else if(rosData.getTopic().name.equals(sharedPreferences.getString(TopicName.TEMPERATURE,TopicName.TEMPERATURE))&&isChecked[map.get(sharedPreferences.getString(TopicName.TEMPERATURE,TopicName.TEMPERATURE))]){
                Temperature temperature=(Temperature) rosData.getMessage();
                msg="Temp:["+temperature.getTemperature()+"]";
                subList.add(sdf.format(System.currentTimeMillis())+"  --  topicName:"+rosData.getTopic().name+"  type:"+rosData.getTopic().type+"  msg:"+msg+"\n");
            }else if(rosData.getTopic().name.equals(sharedPreferences.getString(TopicName.DEST_YAW,TopicName.DEST_YAW))&&isChecked[map.get(sharedPreferences.getString(TopicName.DEST_YAW,TopicName.DEST_YAW))]){
                Float64 destYam=(Float64) rosData.getMessage();
                msg="destYam:["+destYam+"]";
                subList.add(sdf.format(System.currentTimeMillis())+"  --  topicName:"+rosData.getTopic().name+"  type:"+rosData.getTopic().type+"  msg:"+msg+"\n");
            }else */if(isChecked[index]){
                subList.add(sdf.format(System.currentTimeMillis())+"  --  topicName:"+rosData.getTopic().name+"  type:"+rosData.getTopic().type+"  msg:"+getMsg(rosData.getMessage().toRawMessage())+"\n");
            }
            /*SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putStringSet(SUB_CONTENT,ListToSet(subs));
            edit.commit();*/
            updateLog();
        } );

        buttonEdit.setOnClickListener(v->{
            topicList = rosDomain.getTopicList();
            currentTopic = rosDomain.getCurrentTopic();
            currentTopicName = currentTopic.stream().map(topic -> {
                return topic.name;
            }).collect(Collectors.toList());

            items=new String[topicList.size()];
            isChecked=new boolean[topicList.size()];
            for (int i = 0; i < topicList.size(); i++) {
                items[i]=topicList.get(i).name;
                map.put(topicList.get(i).name,i);
            }
            if(isChecked.length<=0){
                Toast.makeText(getContext(),"没有可订阅的主题，请检查是否连接上Master或Master是否有发布的主题",Toast.LENGTH_SHORT).show();
                return;
            }
            showMutilAlertDialog(v);

        });
        sub.setMovementMethod(ScrollingMovementMethod.getInstance());


    }
    private Set<String> ListToSet(ArrayList<String> list){
        Set<String> strings=new HashSet<>();
        for (String s : list) {
            strings.add(s);
        }
        return strings;
    }

    @Override
    public void onDestroyView(){
        rosDomain.unregisterAllAdditionNodes();
        super.onDestroyView();

    }

    public void updateLog(){
       /* SharedPreferences sharedPreferences = getContext().getSharedPreferences(LOG_KEY, Context.MODE_PRIVATE);
        Set<String> subSet = sharedPreferences.getStringSet(SUB_CONTENT, new HashSet<>());
        Set<String> pubSet = sharedPreferences.getStringSet(PUB_CONTENT,new HashSet<>());*/
        StringBuilder sb=new StringBuilder("");
        for (String s : subList) {
            sb.append(s);
        }
        String s1 = sb.toString();
        if(Strings.isNullOrEmpty(s1)){
            sub.setText("");
        }else{
            sub.setText(s1);
        }
    }
    public void showMutilAlertDialog(View v){
        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("选择你要订阅的主题");
        alertBuilder.setMultiChoiceItems(items, isChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                rosDomain.unregisterAllAdditionNodes();
                if(isChecked){
                    Toast.makeText(getActivity(),"选择"+items[which],Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"取消选择"+items[which],Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                for (int i = 0; i < isChecked.length; i++) {
                    Topic topic = topicList.get(i);
                    if(isChecked[i]&&!currentTopicName.contains(topic.name)){
                        rosDomain.additionNode(topic);
                    }
                }
                rosDomain.registerAllAdditionNodes();
            }
        });
        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog=alertBuilder.create();
        alertDialog.show();
    }

    private String getMsg(RawMessage message){
        StringBuilder sb=new StringBuilder();
        List<Field> fields = message.getFields();
        sb.append("[");
        for (int i = 0; i < fields.size(); i++) {
            Field field=fields.get(i);
            if(!judgeContainsStr(field.getName())){
                String fieldString="";
                if(field.getValue() instanceof List){
                    fieldString=Arrays.toString(((List<?>) field.getValue()).toArray());
                }else{
                    fieldString=field.getValue().toString();
                }
                if(i!=fields.size()-1){
                    sb.append(field.getName()).append(":").append(fieldString).append(",");
                }else{
                    sb.append(field.getName()).append(":").append(fieldString);
                }
            }

        }
        sb.append("]\n");
        return sb.toString();
    }

    public static boolean judgeContainsStr(String str){
        String regex=".*[A-Z]+.*";
        Matcher m= Pattern.compile(regex).matcher(str);
        return m.matches();
    }

}
