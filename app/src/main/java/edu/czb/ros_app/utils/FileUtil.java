package edu.czb.ros_app.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.czb.ros_app.model.entities.info.BatteryStateEntity;
import edu.czb.ros_app.model.entities.info.RpyDataEntity;
import edu.czb.ros_app.model.entities.info.TempDataEntity;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.utils
 * @ClassName: FileUtil
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/4/20 18:09
 * @Version: 1.0
 */
public class FileUtil {
    private static final SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /*public static boolean exportRpyExcel(List<RpyDataEntity> listData) {
        try {
            // 创建excel xlsx格式
            Workbook wb = new XSSFWorkbook();
            // 创建工作表
            Sheet sheet = wb.createSheet();
            String[] title = {"序号","横滚角","俯仰角","偏航角","时间"};
            //创建行对象
            Row row = sheet.createRow(0);
            // 设置有效数据的行数和列数
            int colNum = title.length;   // {"用户", "寄件人姓名", "寄件人手机号", "寄件人省/市/区", "寄件人详细地址", "收件人姓名", "收件人手机号", "收件人省/市/区", "收件人详细地址","实付款", "佣金","创建时间"}

            for (int i = 0; i < colNum; i++) {
                sheet.setColumnWidth(i, 20 * 256);  // 显示20个字符的宽度
                Cell cell1 = row.createCell(i);
                //第一行
                cell1.setCellValue(title[i]);
            }

            // 导入数据
            for (int rowNum = 0; rowNum < listData.size(); rowNum++) {

                // 之所以rowNum + 1 是因为要设置第二行单元格
                row = sheet.createRow(rowNum + 1);
                // 设置单元格显示宽度
                row.setHeightInPoints(28f);

                // PhonebillExpressBean 这个是我的业务类，这个是根据业务来进行填写数据
                RpyDataEntity bean = listData.get(rowNum);

                for (int j = 0; j < title.length; j++) {
                    Cell cell = row.createCell(j);

                    //要和title[]一一对应
                    switch (j) {
                        case 0:
                            //序号
                            cell.setCellValue(bean.id);
                            break;
                        case 1:
                            //横滚角
                            cell.setCellValue(bean.roll);
                            break;
                        case 2:
                            //俯仰角
                            cell.setCellValue(bean.pitch);
                            break;
                        case 3:
                            //偏航角
                            cell.setCellValue(bean.yaw);
                            break;
                        case 4:
                            //时间
                            cell.setCellValue(sdf.format(bean.createdTime));
                            break;
                    }
                }

            }

            String mSDCardFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/快递数据";
            File dir = new File(mSDCardFolderPath);
            //判断文件是否存在
            if (!dir.isFile()) {
                //不存在则创建
                dir.mkdir();
            }
//            File excel=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), convertTime(System.currentTimeMillis(),"MM月dd日HH时mm分")+".xlsx");
            File excel = new File(dir, convertTime(System.currentTimeMillis(), "MM月dd日HH时mm分") + ".xlsx");

            FileOutputStream fos = new FileOutputStream(excel);
            wb.write(fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            Log.e("ExpressExcle", "exportExcel", e);
            return false;

        }

    }*/



    //时间戳转换字符串
    public static String convertTime(long time, String patter) {
        SimpleDateFormat sdf = new SimpleDateFormat(patter);
        return sdf.format(new Date(time));
    }

    public static String root = Environment.getExternalStorageDirectory()
            .getPath();



    public static void writeRpyExcel(List<RpyDataEntity> exportOrder) throws Exception{
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)&&getAvailableStorage()>1000000) {
            Log.i("FileUtil:","SD存储空间不足");
            return ;
        }
        String[] title = { "序号","横滚角","俯仰角","偏航角","时间" };
        File file;
        String mSDCardFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/rosData";
        File dir = new File(mSDCardFolderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建Excel工作表
        file = new File(dir, convertTime(System.currentTimeMillis(), "MM_dd_HH_mm") + "_rpy.xls");
        OutputStream os = new FileOutputStream(file);
        WritableWorkbook wwb;
        wwb = Workbook.createWorkbook(os);
        // 添加第一个工作表并设置第一个Sheet的名字
        WritableSheet sheet = wwb.createSheet("Rpy", 0);
        Label label;
        for (int i = 0; i < title.length; i++) {
            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label = new Label(i, 0, title[i], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label);
        }

        for (int i = 0; i < exportOrder.size(); i++) {
            RpyDataEntity order = exportOrder.get(i);

            Label id = new Label(0, i + 1, order.id+"");
            Label roll = new Label(1, i + 1, String.format("%.2f",order.roll));
            Label pitch = new Label(2,i+1,String.format("%.2f",order.pitch));
            Label yaw = new Label(3, i + 1, String.format("%.2f",order.yaw));
            Label time=new Label(4,i+1,sdf.format(order.createdTime));

            sheet.addCell(id);
            sheet.addCell(roll);
            sheet.addCell(pitch);
            sheet.addCell(yaw);
            sheet.addCell(time);

        }
        // 写入数据
        wwb.write();
        // 关闭文件
        wwb.close();
    }
    public static void writeBatteryExcel(List<BatteryStateEntity> exportOrder) throws Exception{
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)&&getAvailableStorage()>1000000) {
            Log.i("FileUtil:","SD存储空间不足");
            return ;
        }
        String[] title = { "序号","电压/V","电流/A","消耗电量/Wh","功率/W","时间" };
        File file;
        String mSDCardFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/rosData";
        File dir = new File(mSDCardFolderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建Excel工作表
        file = new File(dir, convertTime(System.currentTimeMillis(), "MM_dd_HH_mm") + "_battery.xls");
        OutputStream os = new FileOutputStream(file);
        WritableWorkbook wwb;
        wwb = Workbook.createWorkbook(os);
        // 添加第一个工作表并设置第一个Sheet的名字
        WritableSheet sheet = wwb.createSheet("battery", 0);
        Label label;
        for (int i = 0; i < title.length; i++) {
            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label = new Label(i, 0, title[i], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label);
        }

        for (int i = 0; i < exportOrder.size(); i++) {
            BatteryStateEntity order = exportOrder.get(i);

            Label id = new Label(0, i + 1, order.id+"");
            Label voltage = new Label(1, i + 1, String.format("%.2f",order.voltage));
            Label current = new Label(2,i+1,String.format("%.2f",order.current));
            Label charge = new Label(3, i + 1, String.format("%.2f",order.charge));
            Label capacity = new Label(3, i + 1, String.format("%.2f",order.capacity));
            Label time=new Label(4,i+1,sdf.format(order.createdTime));

            sheet.addCell(id);
            sheet.addCell(voltage);
            sheet.addCell(current);
            sheet.addCell(charge);
            sheet.addCell(capacity);
            sheet.addCell(time);

        }
        // 写入数据
        wwb.write();
        // 关闭文件
        wwb.close();
    }

    public static void writeTempExcel(List<TempDataEntity> exportOrder) throws Exception{
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)&&getAvailableStorage()>1000000) {
            Log.i("FileUtil:","SD存储空间不足");
            return ;
        }
        String[] title = { "序号","温度/℃","时间" };
        File file;
        String mSDCardFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/rosData";
        File dir = new File(mSDCardFolderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建Excel工作表
        file = new File(dir, convertTime(System.currentTimeMillis(), "MM_dd_HH_mm") + "_temp.xls");
        OutputStream os = new FileOutputStream(file);
        WritableWorkbook wwb;
        wwb = Workbook.createWorkbook(os);
        // 添加第一个工作表并设置第一个Sheet的名字
        WritableSheet sheet = wwb.createSheet("battery", 0);
        Label label;
        for (int i = 0; i < title.length; i++) {
            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label = new Label(i, 0, title[i], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label);
        }

        for (int i = 0; i < exportOrder.size(); i++) {
            TempDataEntity order = exportOrder.get(i);

            Label id = new Label(0, i + 1, order.id+"");
            Label temp = new Label(1, i + 1, String.format("%.2f",order.temp));
            Label time=new Label(4,i+1,sdf.format(order.createdTime));

            sheet.addCell(id);
            sheet.addCell(temp);
            sheet.addCell(time);

        }
        // 写入数据
        wwb.write();
        // 关闭文件
        wwb.close();
    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            // format.setBorder(Border.ALL, BorderLineStyle.THIN,
            // Colour.BLACK);// 黑色边框
            // format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }



    /** 获取SD可用容量 */
    private static long getAvailableStorage() {

        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        // Formatter.formatFileSize(context, availableSize);
        return availableSize;
    }


}
