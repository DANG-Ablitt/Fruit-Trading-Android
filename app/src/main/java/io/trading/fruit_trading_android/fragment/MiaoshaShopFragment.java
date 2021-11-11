package io.trading.fruit_trading_android.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import io.trading.fruit_trading_android.LeadActivity;
import io.trading.fruit_trading_android.R;
import io.trading.fruit_trading_android.adapter.MSListAdapter;
import io.trading.fruit_trading_android.adapter.ShopAdapter;
import io.trading.fruit_trading_android.config.URLconfig;
import io.trading.fruit_trading_android.dto.LoginDTO;
import io.trading.fruit_trading_android.entity.MSListItem;
import io.trading.fruit_trading_android.entity.ShopDetail;
import io.trading.fruit_trading_android.utils.CountDownTimer1;

public class MiaoshaShopFragment extends Fragment {

    ViewPager2 viewPager;
    private List<MSListItem> list = new ArrayList<>();
    Activity activity;
    //商品详细消息弹窗
    PopupWindow popupWindow_info;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=(ViewGroup) inflater.inflate(
                R.layout.fragment_miaosha, container, false);
        //从服务器获取数据
        //inis();
        //使用模拟数据
        inis1();
        ListView listView = (ListView) v.findViewById(R.id.listview);
        MSListAdapter msListAdapter = new MSListAdapter(this.getContext(), R.layout.fragment_miaosha_list, list);
        listView.setAdapter(msListAdapter);
        // ListView 点击事件
        listView.setOnItemClickListener((adapterView,view,i,l)->{
            //弹窗显示商品详细消息
            PopupWindow_info(v);
        });
        return v;
    }

    /**
     *  商品详细数据弹窗
     */
    private void PopupWindow_info(View view) {
        View popupView=LayoutInflater.from(getActivity()).inflate(R.layout.shop_info_main,null);
        //构造函数关联
        popupWindow_info = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        //连接服务器获取商品图片地址和详细数据列表
        // 商品 url
        String url ;
        // 商品参数列表
        String json_data = null;
        inis_data();
        //表格绑定
        if (json_data != null){
            initData(popupView,json_data);
        }
        //两者结合才能让popup点击外部消失
        popupWindow_info.setOutsideTouchable(true);
        popupWindow_info.setBackgroundDrawable(new BitmapDrawable());
        //让popup占有优先于activity的交互响应能力，不单单是焦点问题。
        popupWindow_info.setFocusable(true);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        //设置高度   必须代码设置
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        popupWindow_info.setHeight(height-height/3);
        //展示popup
        popupWindow_info.showAsDropDown(view);
    }

    /**
     * 从服务器加载商品列表
     */
    public void inis() {

    }

    /**
     * 从服务器加载商品详细数据
     */
    public void inis_data() {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        //连接服务器
        String url = URLconfig.URL_BOOT+URLconfig.URL_LOGIN;
        Log.i("shop_info", url);
        //参数封装
        Map<String,String> map=new HashMap<String,String>();
        //map.put("mobile",dto.getMobile());
        //map.put("captcha",dto.getCaptcha());
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
                            //shaPreferences = getSharedPreferences("isLogin", 0);
                            //SharedPreferences.Editor editor = shaPreferences.edit();
                            //editor.putString("mobile", dto.getMobile());
                            //editor.putString("token", jsonObject.getJSONObject("data").getString("token"));
                            //当前时间
                            Date now = new Date();
                            // 修改默认时区
                            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
                            //打印当前时间日志
                            Log.i("login_date", "now_date：" + now.getTime());
                            System.out.println(now);
                            //过期时间（注意：这里的过期时间和服务器记录同步）
                            Date expireTime = new Date(jsonObject.getJSONObject("data").getLong("expire"));
                            //editor.putLong("expire", expireTime.getTime());
                            System.out.println(expireTime);
                            //editor.commit();
                            //日志检查
                            //Log.i("login_data", "mobile：" + shaPreferences.getString("mobile",null));
                            //Log.i("login_data", "token：" + shaPreferences.getString("token",null));
                            //Log.i("login_data", "expire：" + shaPreferences.getLong("expire",0));
                            //关闭登录窗口
                            //popupWindow_login.dismiss();
                            //调整登录按钮图标
                            //login.setBackgroundResource(R.drawable.dc);
                            //将登录标记修改为1（已登录）
                            //login_ad=1;
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "登录失败 请稍后再试", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, null);
        //放入请求队列
        queue.add(jsonObjectRequest);
    }

    /**
     * 对服务器返回的JSON数据解析并绑定在表格中
     */
    private void initData(View view , String json_data) {
        // 解析 JSON 数据
        Gson gson = new Gson();
        //String json_data = "["+"{"+"detail_name"+":"+"内存容量"+","+"detail_info"+":"+"128G"+"},{"+"detail_name"+":"+"运行内存"+","+"detail_info"+":"+"4G"+"}]";
        List<ShopDetail> ps = gson.fromJson(json_data,new TypeToken<List<ShopDetail>>(){}.getType());
        TableLayout tab = view.findViewById(R.id.tab_activity02);
        tab.setStretchAllColumns(true);
        for(int row = 0; row < ps.size() ; row++)
        {
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setBackgroundColor(Color.WHITE);
            ShopDetail p = ps.get(row);
            TextView textView1 = new TextView(getActivity());
            textView1.setText(p.getDetail_name());
            textView1.setGravity(Gravity.CENTER);
            tableRow.addView(textView1);
            TextView textView2 = new TextView(getActivity());
            textView2.setText(p.getDetail_info());
            textView2.setGravity(Gravity.CENTER);
            tableRow.addView(textView2);
            tab.addView(tableRow,new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }
    }


    /**
     * 为达到演示需要使用模拟数据
     */
    public void inis1() {
        MSListItem msListItem1=new MSListItem(1L,"iPhone13 白色",20,"7999","8226",
                "https://img14.360buyimg.com/n1/s450x450_jfs/t1/186812/20/9246/188349/60cf09bcE6b187bc1/cf043125dafc7d77.jpg",
                "裕华万达店","2021-12-31 20:00:00");
        list.add(msListItem1);
        MSListItem msListItem2=new MSListItem(1L,"iPhone13 mini 黑",30,"5199","5999",
                "https://img14.360buyimg.com/n1/s450x450_jfs/t1/146138/18/23045/182460/618ba0e0E5bee8154/48265d15e6f5a138.jpg",
                "万象城店","2021-11-31 20:20:00");
        list.add(msListItem2);
        MSListItem msListItem3=new MSListItem(1L,"iPhone13 pro 黑",20,"8999","9300",
                "https://img12.360buyimg.com/n1/s450x450_jfs/t1/198509/34/16814/120270/618b9c91Ef633755e/eada1756f3459484.jpg",
                "北国商城店","2021-11-22 20:30:11");
        list.add(msListItem3);

    }

    @Override
    public void onAttach(Activity activity) {
        //this.mContext = activity;
        super.onAttach(activity);
        this.activity = activity;
    }


}
