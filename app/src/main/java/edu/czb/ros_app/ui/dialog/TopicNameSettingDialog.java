package edu.czb.ros_app.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import edu.czb.ros_app.R;
import edu.czb.ros_app.model.rosRepositories.message.TopicName;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.ui.dialog
 * @ClassName: TopicNameSettingDialog
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/11 9:05
 * @Version: 1.0
 */
public class TopicNameSettingDialog extends Dialog {
    private Button btn_cancel;
    private Button btn_save;
    public EditText text_map;
    public EditText text_imu;
    public EditText text_battery;
    public EditText text_rpy;
    public EditText text_map_path;
    public EditText text_joy;
    public EditText text_temp;
    public EditText text_dest_yaw;

    Activity context;
    public View.OnClickListener mClickListener;
    public TopicNameSettingDialog(@NonNull Context context) {
        super(context);
    }

    public TopicNameSettingDialog(Activity context, int theme, View.OnClickListener clickListener){
        super(context,theme);
        this.context=context;
        this.mClickListener=clickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_topic_name_setting);
        text_map=(EditText) findViewById(R.id.edit_topic_map);
        text_map_path=(EditText) findViewById(R.id.edit_topic_map_Path);
        //text_imu=(EditText) findViewById(R.id.edit_topic_imu);
        text_battery=(EditText) findViewById(R.id.edit_topic_battery);
        text_rpy=(EditText) findViewById(R.id.edit_topic_rpy);
        text_joy=(EditText) findViewById(R.id.edit_topic_joy);
        //text_temp=(EditText) findViewById(R.id.edit_topic_temp);
        text_dest_yaw=(EditText)findViewById(R.id.edit_topic_dest_yaw);
        initTopicName(context.getSharedPreferences(TopicName.TOPIC_KEY,Context.MODE_PRIVATE));
        Window dialogWindow=this.getWindow();
        WindowManager m= context.getWindowManager();
        Display d=m.getDefaultDisplay();
        WindowManager.LayoutParams p=dialogWindow.getAttributes();
        p.width=(int)(d.getWidth()*0.8);
        double height = text_battery.getTextSize();
        p.height=(int)(height *20);
        p.setColorMode(ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT);
        dialogWindow.setAttributes(p);
        btn_save=(Button)findViewById(R.id.btn_save_topic_name);
        btn_cancel=(Button)findViewById(R.id.btn_cancel_topic_name);
        btn_cancel.setOnClickListener(mClickListener);
        btn_save.setOnClickListener(mClickListener);
        this.setCancelable(true);
    }

    public void initTopicName(SharedPreferences topicInfo){
        text_battery.setText(topicInfo.getString(TopicName.BATTERY,TopicName.BATTERY));
        text_map.setText(topicInfo.getString(TopicName.MAP,TopicName.MAP));
        text_map_path.setText(topicInfo.getString(TopicName.MAP_PATH,TopicName.MAP_PATH));
        //text_imu.setText(topicInfo.getString(TopicName.IMU,TopicName.IMU));
        text_rpy.setText(topicInfo.getString(TopicName.RPY,TopicName.RPY));
        text_joy.setText(topicInfo.getString(TopicName.JOY,TopicName.JOY));
        //text_temp.setText(topicInfo.getString(TopicName.TEMPERATURE,TopicName.TEMPERATURE));
        text_dest_yaw.setText(topicInfo.getString(TopicName.DEST_YAW,TopicName.DEST_YAW));
    }
}
