package io.trading.fruit_trading_android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.trading.fruit_trading_android.adapter.ShopAdapter;
import io.trading.fruit_trading_android.config.URLconfig;
import io.trading.fruit_trading_android.dto.LoginDTO;
import io.trading.fruit_trading_android.utils.CountDownTimer1;

import static android.text.TextUtils.isEmpty;

public class LeadActivity extends AppCompatActivity
{

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private final static String[] NAMES = {"秒杀商品", "抢购商品"};
    private FragmentStateAdapter pagerAdapter;
    //登录按钮
    private ImageButton login;
    //登录弹窗
    PopupWindow popupWindow_login;
    //存储的登录信息（token）
    private SharedPreferences shaPreferences;
    //系统登录标识 0：未登录 1：已登录
    private Integer login_ad= 0 ;
    //相机权限代码
    public final int REQUEST_CAMERA_CODE = 0x51;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 去除自带的标题栏
         * RequestWindowFeature(Window.FEATURE_NO_TITLE);对于继承于Activity可以，
         * 但对继承于AppCompatActivity并不适应，使用使用下面的写法：
         */
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_password);
        //实例化ViewPager2和适配器
        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new ShopAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        //完成和标签的绑定
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(NAMES[position])).attach();
        /**
         * 获取XML文件中的控件
         */
        //登录
        login=findViewById(R.id.share_qq_btn);
        //判断是否已经登录（如果token存在）
        inin_login();
        //登录按钮的事件监听
        login.setOnClickListener(v -> {
            if(login_ad==0){
                //弹出登录窗口
                initPopupWindow(v);
            }else{
                //与服务器通信执行登出操作
                logout(); }
            });

        checkPermission(Manifest.permission.CAMERA,1001);
        checkPermission(Manifest.permission.INTERNET,REQUEST_CAMERA_CODE);
    }

    /**
     * 判断是否登录
     */
    public void inin_login(){
        if(login_ad==0){
            //检查token是否存在
            shaPreferences = getSharedPreferences("isLogin", 0);
            String token=shaPreferences.getString("token",null);
            if(!isEmpty(token)){
                //检查 token 是否过期
                //当前时间
                Date now = new Date();
                if(shaPreferences.getLong("expire",0)>now.getTime()){
                    //调整登录按钮图标
                    login.setBackgroundResource(R.drawable.dc);
                    //将登录标记修改为1（已登录）
                    login_ad=1;
                }else{
                    Toast.makeText(getApplicationContext(), "Token过期 请您登录", Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(getApplicationContext(), "请您登录", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 用户登出
     */
    public void logout(){
        //与服务器通信执行登出
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = URLconfig.URL_BOOT+URLconfig.URL_LOGOUT;
        Log.i("logout_json", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, (response)->{
                    Log.i("login_response", "返回的信息" + response.toString());
                    //将得到json数据转换为一个json对象
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        //获取code和msg
                        Log.i("logout_response_data", "code：" + jsonObject.getInt("code"));
                        Log.i("logout_response_data", "msg：" + jsonObject.getString("msg"));
                        if(jsonObject.getInt("code")==0){
                            //执行登出成功的逻辑
                            shaPreferences = getSharedPreferences("isLogin", 0);
                            SharedPreferences.Editor editor = shaPreferences.edit();
                            //当前时间
                            Date now = new Date();
                            // 修改默认时区
                            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
                            //打印当前时间日志
                            Log.i("logout_date", "now_date：" + now.getTime());
                            System.out.println(now);
                            editor.putLong("expire", now.getTime());
                            editor.commit();
                            //日志检查
                            Log.i("logout_data", "expire：" + shaPreferences.getLong("expire",0));
                            //调整登录按钮图标
                            login.setBackgroundResource(R.drawable.dl);
                            //将登录标记修改为0（未登录）
                            login_ad=0;
                        }else{
                            Toast.makeText(getApplicationContext(), "服务器丢失啦", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, null)
        {
            //重写头文件，根据需要设置
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", shaPreferences.getString("token",null));
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    protected void checkPermission(String permission, int resultCode) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {// 没有权限。
            //Log.i("info", "1,需要申请权限。");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //TODO 用户未拒绝过 该权限 shouldShowRequestPermissionRationale返回false  用户拒绝过一次则一直返回true
                //TODO   注意小米手机  则一直返回时 false
                //Log.i("info", "3,用户已经拒绝过一次该权限，需要提示用户为什么需要该权限。\n" +
                        //"此时shouldShowRequestPermissionRationale返回：" + ActivityCompat.shouldShowRequestPermissionRationale(this,
                        //permission));
                //TODO  解释为什么  需要该权限的  对话框
                showMissingPermissionDialog();
            } else {
                // 申请授权。
                ActivityCompat.requestPermissions(this, new String[]{permission}, resultCode);
                //Log.i("info", "2,用户拒绝过该权限，或者用户从未操作过该权限，开始申请权限。-\n" +
                       // "此时shouldShowRequestPermissionRationale返回：" +
                        //ActivityCompat.shouldShowRequestPermissionRationale(this, permission));
            }
        } else {
            //TODO 权限 已经被准许  you can do something
            //permissionHasGranted();
            //Log.i("info", "7,已经被用户授权过了=可以做想做的事情了==打开联系人界面");
        }
    }


    /**
     * 提示用户的 dialog
     */
    protected void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少联系人权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限。");
        // 拒绝, 退出应用
        builder.setNegativeButton("R.string.cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.i("info", "8--权限被拒绝,此时不会再回调onRequestPermissionsResult方法");
                    }
                });
        builder.setPositiveButton("R.string.setting",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.i("info", "4,需要用户手动设置，开启当前app设置界面");
                        startAppSettings();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 打开     App设置界面
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * 弹出登录窗口
     */
    private void initPopupWindow(View view) {
        if(popupWindow_login == null){
            View popupView=LayoutInflater.from(LeadActivity.this).inflate(R.layout.login_main,null);
            //构造函数关联
            popupWindow_login = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
            //绑定手机号文本框
            EditText edittext_mobile=popupView.findViewById(R.id.etUsername);
            //绑定验证码文本框
            EditText edittext_captcha=popupView.findViewById(R.id.etPassword);
            //处理验证码按钮
            popupView.findViewById(R.id.btpass).setOnClickListener(v->
                    {
                        /**
                         * bug记录：
                         * （1）java.lang.IllegalStateException: java.net.SocketException: socket failed: EPERM (Operation not permitted)
                         * 解决方案：卸载原APP，重新安装
                         * （2） W/System.err: java.io.IOException: Cleartext HTTP traffic to **** not permitted
                         * 解决方案： android:usesCleartextTraffic="true"
                         */
                        //new倒计时对象,总共的时间,每隔多少秒更新一次时间
                        final CountDownTimer1 CountDownTimer = new CountDownTimer1(60000,1000,popupView.findViewById(R.id.btpass));
                        CountDownTimer.start();
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        //向服务器请求短信验证码
                        String url = URLconfig.URL_BOOT+URLconfig.URL_CAPTCHA;
                        Log.i("json", url);
                        //对手机号进行格式效验
                        if(mobile_1(edittext_mobile.getText().toString())){
                            //参数封装
                            Map<String,String> map=new HashMap<String,String>();
                            map.put("mobile",edittext_mobile.getText().toString());
                            JSONObject params=new JSONObject(map);
                            Log.i("Captcha_Post", "上传的信息" + params.toString());
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                    (Request.Method.POST, url, params, null, null);
                            //放入请求队列
                            queue.add(jsonObjectRequest);
                        }
                    });
            //处理手机登录按钮
            popupView.findViewById(R.id.btLogin).setOnClickListener(v->login(new LoginDTO(edittext_mobile.getText().toString(),edittext_captcha.getText().toString())));
            //处理扫码登录按钮
            popupView.findViewById(R.id.btLogin1).setOnClickListener(v->Toast.makeText(getApplicationContext(), "功能正在完善", Toast.LENGTH_LONG).show());
        }
        //两者结合才能让popup点击外部消失
        popupWindow_login.setOutsideTouchable(true);
        popupWindow_login.setBackgroundDrawable(new BitmapDrawable());
        //让popup占有优先于activity的交互响应能力，不单单是焦点问题。
        popupWindow_login.setFocusable(true);
        Display display = getWindowManager().getDefaultDisplay();
        //设置高度   必须代码设置
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        popupWindow_login.setHeight(height/2);
        //展示popup
        popupWindow_login.showAsDropDown(view);
        /**
         * bug记录
         * 如果将以下三条语句放在这里第二次点击会发生空指针异常
         * popupView.findViewById(R.id.btpass).setOnClickListener(v->Toast.makeText(getApplicationContext(), "正在向服务器请求验证码", Toast.LENGTH_LONG).show());
         * popupView.findViewById(R.id.btLogin).setOnClickListener(v->Toast.makeText(getApplicationContext(), "登录中", Toast.LENGTH_LONG).show());
         * popupView.findViewById(R.id.btLogin1).setOnClickListener(v->Toast.makeText(getApplicationContext(), "功能正在完善", Toast.LENGTH_LONG).show());
         */
    }

    /**
     * 对手机号进行合法性效验
     * 作用：检查手机号格式是否正确
     * 目前的移动号段：139、138、137、136、135、134、147、150、151、152、157、158、159、172、178、182、183、184、187、188、198.
     * 联通号段：130、131、132、140、145、146、155、156、166、167、185、186、145、175、176
     * 电信号段：133、149、153、177、173、180、181、189、191、199
     */
    public Boolean mobile_1(String mobile){
        //检查手机号是否为11位，是否为空
        if(mobile==null||mobile.length()!=11||mobile==""){
            Toast.makeText(getApplicationContext(), "手机号不合法", Toast.LENGTH_LONG).show();
            return false;
        }else{
            //根据正则表达式检查手机号是否合法
            Pattern p = Pattern.compile("^((13[0-9])|(14[0|5|6|7|9])|(15[0-3])|(15[5-9])|(16[6|7])|(17[2|3|5|6|7|8])|(18[0-9])|(19[1|8|9]))\\d{8}$");
            Matcher m = p.matcher(mobile);
            if(m.matches()){
                return true;
            }else{
                Toast.makeText(getApplicationContext(), "手机号不合法", Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }

    public void login(LoginDTO dto){
        //对手机号和验证码进行合法性检查
        if(mobile_1(dto.getMobile())){
            if(dto.getCaptcha()==null||dto.getCaptcha()==""){
                Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_LONG).show();
            }else{
                //通过手机号和验证码进行合法性检查
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                //连接服务器进行登录
                String url = URLconfig.URL_BOOT+URLconfig.URL_LOGIN;
                Log.i("login_json", url);
                //参数封装
                Map<String,String> map=new HashMap<String,String>();
                map.put("mobile",dto.getMobile());
                map.put("captcha",dto.getCaptcha());
                JSONObject params=new JSONObject(map);
                Log.i("login_Post", "上传的信息" + params.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, params, (response)->{
                            Log.i("login_response", "返回的信息" + response.toString());
                            //将得到json数据转换为一个json对象
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                //获取code和msg
                                Log.i("login_response_data", "code：" + jsonObject.getInt("code"));
                                Log.i("login_response_data", "msg：" + jsonObject.getString("msg"));
                                Log.i("login_response_data", "token：" + jsonObject.getJSONObject("data").getString("token"));
                                Log.i("login_response_data", "expire：" + jsonObject.getJSONObject("data").getString("expire"));
                                //如果登录成功：获取token并保存在本地
                                //如果登录失败：提示用户重新登录
                                /**
                                 * bug 记录
                                 * jsonObject.getString("msg")=="success"的问题
                                 */
                                if(jsonObject.getInt("code")==0){
                                    //将token保存在本地
                                    shaPreferences = getSharedPreferences("isLogin", 0);
                                    SharedPreferences.Editor editor = shaPreferences.edit();
                                    editor.putString("mobile", dto.getMobile());
                                    editor.putString("token", jsonObject.getJSONObject("data").getString("token"));
                                    //当前时间
                                    Date now = new Date();
                                    // 修改默认时区
                                    TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
                                    //打印当前时间日志
                                    Log.i("login_date", "now_date：" + now.getTime());
                                    System.out.println(now);
                                    //过期时间（注意：这里的过期时间和服务器记录同步）
                                    Date expireTime = new Date(jsonObject.getJSONObject("data").getLong("expire"));
                                    editor.putLong("expire", expireTime.getTime());
                                    System.out.println(expireTime);
                                    editor.commit();
                                    //日志检查
                                    Log.i("login_data", "mobile：" + shaPreferences.getString("mobile",null));
                                    Log.i("login_data", "token：" + shaPreferences.getString("token",null));
                                    Log.i("login_data", "expire：" + shaPreferences.getLong("expire",0));
                                    //关闭登录窗口
                                    popupWindow_login.dismiss();
                                    //调整登录按钮图标
                                    login.setBackgroundResource(R.drawable.dc);
                                    //将登录标记修改为1（已登录）
                                    login_ad=1;
                                }else{
                                    Toast.makeText(getApplicationContext(), "登录失败 请稍后再试", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, null);
                //放入请求队列
                queue.add(jsonObjectRequest);
            }
        }else{
            Toast.makeText(getApplicationContext(), "手机号不合法", Toast.LENGTH_LONG).show();
        }
    }





    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}








