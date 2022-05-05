package edu.czb.ros_app.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;


import com.baidu.entity.pb.PoiResult;

import edu.czb.ros_app.R;
import edu.czb.ros_app.ui.fragments.MapFragment;

import android.widget.Button;
import android.widget.TextView;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.ui.dialog
 * @ClassName: CorrectLatLngDialog
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/10 16:16
 * @Version: 1.0
 */
public class CorrectLatLngDialog extends Dialog {
    Activity context;
    private Button btn_save;
    private Button btn_cancel;
    public EditText text_lng;
    public EditText text_lat;
    public EditText text_distance;
    private TextView lat;
    private TextView lng;
    public CheckBox checkBox;
    private View.OnClickListener mClickListener;
    public CorrectLatLngDialog(@NonNull Activity context) {
        super(context);
        this.context=context;
    }
    public CorrectLatLngDialog(Activity context,int theme,View.OnClickListener clickListener){
        super(context,theme);
        this.context=context;
        this.mClickListener=clickListener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_correct_lng_lat);
        text_lat=(EditText) findViewById(R.id.edit_correct_lat);
        text_lng=(EditText) findViewById(R.id.edit_correct_lng);
        checkBox=(CheckBox)findViewById(R.id.is_show_path);
        lat=(TextView)findViewById(R.id.lat_text);
        lng=(TextView)findViewById(R.id.lng_text);
        text_distance=(EditText)findViewById(R.id.edit_distance);

        SharedPreferences sharedPreferences = context.getSharedPreferences(MapFragment.INFO, Context.MODE_PRIVATE);
        checkBox.setChecked(sharedPreferences.getBoolean(MapFragment.IS_CHECK,false));
        lat.setText("纬度校准值:"+sharedPreferences.getString(MapFragment.LAT_CORR_FACTOR,"0"));
        lng.setText("经度校准值:"+sharedPreferences.getString(MapFragment.LNG_CORR_FACTOR,"0"));
        text_lat.setText(""+(Double.parseDouble(sharedPreferences.getString(MapFragment.CURR_LAT,"0"))-Double.parseDouble(sharedPreferences.getString(MapFragment.LAT_CORR_FACTOR,"0"))));
        text_lng.setText(""+(Double.parseDouble(sharedPreferences.getString(MapFragment.CURR_LNG,"0"))-Double.parseDouble(sharedPreferences.getString(MapFragment.LNG_CORR_FACTOR,"0"))));
        text_distance.setText(""+(Double.parseDouble(sharedPreferences.getString(MapFragment.DISTANCE_FACTOR,"0.0"))));
        Window dialogWindow=this.getWindow();
        WindowManager m= context.getWindowManager();
        Display d=m.getDefaultDisplay();
        WindowManager.LayoutParams p=dialogWindow.getAttributes();
        p.width=(int)(d.getWidth()*0.8);
        double height = text_lat.getTextSize();
        p.height=(int)(height *18);
        dialogWindow.setAttributes(p);
        btn_save=(Button)findViewById(R.id.btn_save_correct);
        btn_cancel=(Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(mClickListener);
        btn_save.setOnClickListener(mClickListener);
        this.setCancelable(true);
    }
}
