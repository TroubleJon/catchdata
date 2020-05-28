package com.zx.catchdata;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zx.catchdata.ExcelUtil.exportXLS;

public class MainActivity extends AppCompatActivity {

    private static final int SCAN_CAMERA = 11;
//    private static final int SCAN_REQUEST_CODE = 110;
//    private static final String SCAN_RESULT = "SCAN_RESULT";
    public SQLiteDatabase database;

    public static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @BindView(R.id.code)
    TextView code;
    @BindView(R.id.producer)
    TextView producer;
    @BindView(R.id.file_NO)
    TextView fileNO;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.model)
    TextView model;
    @BindView(R.id.form_type)
    TextView formType;
    @BindView(R.id.dealer_info)
    TextView dealerInfo;
    @BindView(R.id.sale_type)
    TextView saleType;
    @BindView(R.id.arrive_date)
    TextView arriveDate;
    @BindView(R.id.sale_area)
    TextView saleArea;
    @BindView(R.id.scan)
    Button scan;
    @BindView(R.id.tongji)
    Button tongji;
    @BindView(R.id.count)
    TextView count;
    @BindView(R.id.zero)
    Button zero;
    private SharedPreferences sp;
    private Elements elements;
    private String url = "http://lxxx.cccf.com.cn/lableFind.jsp?lableCode=";
    private String codeInUrl;
    private String scan_result;
    private int sum_count = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    code.setText(elements.get(0).attr("value"));
                    producer.setText(elements.get(3).attr("value"));
                    fileNO.setText(elements.get(4).attr("value"));
                    name.setText(elements.get(5).attr("value"));
                    model.setText(elements.get(6).attr("value"));
                    formType.setText(elements.get(9).attr("value"));
                    dealerInfo.setText(elements.get(15).attr("value"));
                    saleType.setText(elements.get(16).attr("value"));
                    arriveDate.setText(elements.get(17).attr("value"));
                    saleArea.setText(elements.get(18).attr("value"));
                    if (save(elements)) {
                        Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                        sum_count++;
                        count.setText(sum_count + "");
                    }else {
                        Toast.makeText(getApplicationContext(), "重复扫描", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    code.setText(elements.get(0).attr("value"));
                    producer.setText(elements.get(3).attr("value"));
                    fileNO.setText(elements.get(4).attr("value"));
                    name.setText(elements.get(5).attr("value"));
                    model.setText(elements.get(6).attr("value"));
                    formType.setText(elements.get(9).attr("value"));
                    dealerInfo.setText("");
                    saleType.setText("");
                    arriveDate.setText("");
                    saleArea.setText("");
                    if (save(elements)) {
                        Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                        sum_count++;
                        count.setText(sum_count + "");
                    }else {
                        Toast.makeText(getApplicationContext(), "重复扫描", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    code.setText(codeInUrl);
                    database.execSQL("insert into ft_data(id,code,producer,fileNO,name,model,formtype,dealerinfo,saletype,arrivedate,salearea,location,pillarindex)values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                            new String[]{null, scan_result.substring(scan_result.lastIndexOf("=") + 1), null, null, null, null, null, null, null, null, null, null, null});
                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    sum_count++;
                    count.setText(sum_count + "");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        permission();
        sp = getSharedPreferences("catchdata", Context.MODE_PRIVATE);
        sum_count = sp.getInt("sum", 0);
        count.setText(sum_count + "");
    }


    @OnClick({R.id.scan, R.id.zero, R.id.tongji})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan:
                startQrCode();
                break;
//            case R.id.save:
//                if (save(elements)) {
//                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
//                    sum_count++;
//                    count.setText(sum_count + "");
//                }
//                break;
            case R.id.zero:
                count.setText(0+"");
                sum_count = 0;
                break;

            case R.id.tongji:
                database.execSQL("insert into ft_data(id,code,producer,fileNO,name,model,formtype,dealerinfo,saletype,arrivedate,salearea,location,pillarindex)values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        new String[]{null, "这里是分割线，上边属于同一个部门的器材", "-----", "-----", "-----", "-----", "-----", "-----", "-----", "-----", "-----", "-----", "-----"});
                if (exportXLS(getDataList(database),"消防器材导出表") == 1){
                    Toast.makeText(getApplicationContext(), "导出成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "导出失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void startQrCode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, SCAN_CAMERA);
            return;
        }
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        //startActivityForResult(intent, SCAN_REQUEST_CODE);
        startActivityForResult(intent, Constant.REQ_QR_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            scan_result = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            codeInUrl = scan_result.substring(scan_result.lastIndexOf("=") + 1);
//            if (url.contains("www")) {
//                url = url.replace("www", "lxxx");
//            }
            if (isNeworkAvailable(this)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            if (url.contains("http://")) {
//                                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
//                                if(con.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
//                                    codeInUrl = url.substring(url.lastIndexOf("=") + 1);
//                                    url = url+codeInUrl;
//                                    handler.sendEmptyMessage(2);
//                                }
                                Document document = Jsoup.connect(url+codeInUrl).get();
                                elements = document.select("input.kuang");
                                if (elements.size() > 14){
                                    handler.sendEmptyMessage(0);
                                }else {
                                    handler.sendEmptyMessage(1);
                                }

                            //}
//                            else {
//                                handler.sendEmptyMessage(1);
//                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                code.setText(scan_result.substring(scan_result.lastIndexOf("=") + 1));
            }

        }
    }

    private static boolean isNeworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            return true;
        }
        return false;
    }

    private static void copyfile(Context context, File file, String str) {
        try {
            InputStream is = context.getAssets().open(str + ".db");
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = is.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean save(Elements elements) {
        if (isNeworkAvailable(this) & elements.size() > 14) {
            Cursor cursor = database.rawQuery("select * from ft_data where code = ?", new String[]{scan_result.substring(scan_result.lastIndexOf("=") + 1)});
            if (cursor.moveToNext()) {
                database.execSQL("update ft_data set producer=?,fileNO=?,name=?,model=?,formtype=?,dealerinfo=?,saletype=?,arrivedate=?,salearea=?where code = ?", new String[]{
                        elements.get(3).attr("value"), elements.get(4).attr("value"), elements.get(5).attr("value"), elements.get(6).attr("value"),
                        elements.get(9).attr("value"), elements.get(15).attr("value"), elements.get(16).attr("value"), elements.get(17).attr("value"),
                        elements.get(18).attr("value"), scan_result.substring(scan_result.lastIndexOf("=") + 1)});
                cursor.close();
                return false;
            } else {
                database.execSQL("insert into ft_data(id,code,producer,fileNO,name,model,formtype,dealerinfo,saletype,arrivedate,salearea,location,pillarindex)values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        new String[]{null, scan_result.substring(scan_result.lastIndexOf("=") + 1), elements.get(3).attr("value"), elements.get(4).attr("value"), elements.get(5).attr("value"),
                                elements.get(6).attr("value"), elements.get(9).attr("value"), elements.get(15).attr("value"), elements.get(16).attr("value"), elements.get(17).attr("value"),
                                elements.get(18).attr("value"), null, null});
                return true;
            }
        } else if (isNeworkAvailable(this) & elements.size() <= 14){
            Cursor cursor = database.rawQuery("select * from ft_data where code = ?", new String[]{scan_result.substring(scan_result.lastIndexOf("=") + 1)});
            if (cursor.moveToNext()) {
                database.execSQL("update ft_data set producer=?,fileNO=?,name=?,model=?,formtype=?,dealerinfo=?,saletype=?,arrivedate=?,salearea=?where code = ?", new String[]{
                        elements.get(3).attr("value"), elements.get(4).attr("value"), elements.get(5).attr("value"), elements.get(6).attr("value"),
                        elements.get(9).attr("value"), null,null, null, null, scan_result.substring(scan_result.lastIndexOf("=") + 1)});
                cursor.close();
                return false;
            } else {
                database.execSQL("insert into ft_data(id,code,producer,fileNO,name,model,formtype,dealerinfo,saletype,arrivedate,salearea,location,pillarindex)values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        new String[]{null, scan_result.substring(scan_result.lastIndexOf("=") + 1), elements.get(3).attr("value"), elements.get(4).attr("value"), elements.get(5).attr("value"),
                                elements.get(6).attr("value"), elements.get(9).attr("value"), null, null, null,null, null, null});
                return true;
            }
        }
//        else if (!isNeworkAvailable(this)) {
//            database.execSQL("insert into ft_data(id,code,producer,fileNO,name,model,formtype,dealerinfo,saletype,arrivedate,salearea,location,pillarindex)values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
//                    new String[]{null, scan_result.substring(scan_result.lastIndexOf("=") + 1), null, null, null, null, null, null, null, null, null, null, null});
//            return true;
//        }
        else if (elements == null) {
            Toast.makeText(getApplicationContext(), "信息有误,保存失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    private void permission() {
        //网络
        if (!isNeworkAvailable(this)) {
            Toast.makeText(this, "未连接网络，或者网络连接不可用", Toast.LENGTH_LONG).show();
        }
        //读写权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 0);
            }
        }
        //数据库
        File file = new File(getExternalFilesDir(null).getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        File f_db = new File(getExternalFilesDir(null).getAbsolutePath() + "FireToolsData");
        if (!f_db.exists()) {
            copyfile(this, f_db, "FireToolsData");
        }
        database = SQLiteDatabase.openOrCreateDatabase(f_db, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("sum", sum_count);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("sum", sum_count);
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("sum", sum_count);
        editor.commit();
    }

    private ArrayList<FireTools> getDataList(SQLiteDatabase database){
        ArrayList<FireTools> datalist = new ArrayList<>();
        Cursor c = database.rawQuery("select * from ft_data", new String[]{});
        while (c.moveToNext()){
            String id = c.getInt(0)+"";
            String code = c.getString(1);
            String producer = c.getString(2);
            String fileNO = c.getString(3)+"";
            String name = c.getString(4);
            String model = c.getString(5);
            String formType = c.getString(6)+"";
            String dealerInfo = c.getString(7);
            String saleType = c.getString(8);
            String arriveDate = c.getString(9)+"";
            String saleArea = c.getString(10)+"";
            String location = c.getString(11);
            String pillarindex = c.getString(12);
            datalist.add(new FireTools(id,code,producer, fileNO, name, model, formType, dealerInfo, saleType, arriveDate, saleArea, location, pillarindex));
        }
        return datalist;
    }



}
