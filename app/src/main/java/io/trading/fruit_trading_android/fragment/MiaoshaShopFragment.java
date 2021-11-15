package io.trading.fruit_trading_android.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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

    /* 标记信息 */
    private static final int LIST_SUCCESS = 0;// 登录成功标示
    private static final int LIST_FAILURE = 1;// 登录失败标示
    private static final int INFO_SUCCESS = 2;// 登录成功标示
    private static final int INFO_FAILURE = 3;// 登录失败标示
    ViewPager2 viewPager;
    private List<MSListItem> list = new ArrayList<>();
    Activity activity;
    //商品详细消息弹窗
    PopupWindow popupWindow_info;
    ListView listView;
    MSListAdapter msListAdapter;
    String json_data;
    String pic;
    Long shop_id ;
    String url ;
    View v1;

    /* handler */
    private Handler mListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIST_SUCCESS:
                    msListAdapter.notifyDataSetChanged();
                    break;
                case LIST_FAILURE:
                    Toast.makeText(getActivity().getApplicationContext(), "网络连接失败 请稍后再试", Toast.LENGTH_LONG).show();
                    break;
                case INFO_SUCCESS:
                    PopupWindow_info(v1,shop_id,url,pic);
                    break;
                case INFO_FAILURE:
                    Toast.makeText(getActivity().getApplicationContext(), "获取失败 请稍后再试", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=(ViewGroup) inflater.inflate(
                R.layout.fragment_miaosha, container, false);
        list_update();
        //从服务器获取数据
        inis();
        //使用模拟数据
        //inis1();
        listView = (ListView) v.findViewById(R.id.listview);
        msListAdapter = new MSListAdapter(this.getContext(), R.layout.fragment_miaosha_list, list);
        listView.setAdapter(msListAdapter);
        // ListView 点击事件
        listView.setOnItemClickListener((adapterView,view,i,l)->{
            // 获取商品id 和 url
            ListView listView = (ListView)adapterView;
            MSListItem msListItem = (MSListItem)listView.getItemAtPosition(i);
            shop_id = msListItem.getId();
            url = msListItem.getUrl();
            v1=v;
            //弹窗显示商品详细消息
            //PopupWindow_info(v,shop_id,url);
            inis_data(shop_id);
        });
        return v;
    }

    /**
     *  商品详细数据弹窗
     */
    private void PopupWindow_info(View view,Long shop_id,String url,String pic) {
        View popupView=LayoutInflater.from(getActivity()).inflate(R.layout.shop_info_main,null);
        //构造函数关联
        popupWindow_info = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        //连接服务器获取商品图片地址和详细数据列表
        // 商品 url 绑定
        ImageView imageView = popupView.findViewById(R.id.image);
        Glide.with(getContext()).load(url).into(imageView);
        // 商品参数列表
        //inis_data(shop_id);
        //表格绑定
        if (json_data != null){
            initData(popupView,json_data,pic);
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
        list.clear();
        RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
        //连接服务器
        String url = URLconfig.URL_BOOT+URLconfig.URL_LISTMS;
        Log.i("shop_list", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, (response)->{
                    Log.i("shop_list_response", "返回的信息" + response.toString());
                    //将得到json数据转换为一个json对象
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        //获取code和msg
                        Log.i("shop_list_response_data", "code：" + jsonObject.getInt("code"));
                        Log.i("shop_list_response_data", "msg：" + jsonObject.getString("msg"));
                        Log.i("shop_list_response_data", "data：" + jsonObject.getJSONArray("data"));
                        if(jsonObject.getInt("code")==0){
                            //
                            Gson gson = new Gson();
                            List<MSListItem> ps = gson.fromJson(jsonObject.getJSONArray("data").toString(), new TypeToken<List<MSListItem>>(){}.getType());
                            for(int i = 0; i < ps.size() ; i++)
                            {
                                MSListItem p = ps.get(i);
                                list.add(p);
                            }
                            // 返给handler的信息
                            Message msg = new Message();
                            msg.what = 0;
                            mListHandler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            msg.what = 1;
                            mListHandler.sendMessage(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, null);
        //放入请求队列
        queue.add(jsonObjectRequest);
    }

    /**
     * 定期更新秒杀商品列表
     */
    public void list_update() {
        //开启一个线程
        new Thread(){
            public void run() {
                //Log.i("xuhaitao", "子线程打印");
                while (true) {
                    try {
                        //线程暂停10秒，单位毫秒
                        Thread.sleep(10000);
                        // 从服务器查询最新数据
                        inis();
                        //发送消息
                        Message message=new Message();
                        message.what=0;
                        mListHandler.sendMessage(message);
                        Log.i("Thread", "子线程打印");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 从服务器加载商品详细数据
     */
    public void inis_data(Long shop_id) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        //连接服务器
        String url = URLconfig.URL_BOOT+URLconfig.URL_INFOMS + shop_id.toString();
        Log.i("shop_info", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, (response)->{
                    Log.i("shop_info_response", "返回的信息" + response.toString());
                    //将得到json数据转换为一个json对象
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        //获取code和msg
                        Log.i("shop_info_response_data", "code：" + jsonObject.getInt("code"));
                        Log.i("shop_info_response_data", "msg：" + jsonObject.getString("msg"));
                        Log.i("shop_info_response_data", "pic：" + jsonObject.getJSONObject("data").getString("pic"));
                        Log.i("shop_info_response_data", "detail：" + jsonObject.getJSONObject("data").getString("detail"));
                        if(jsonObject.getInt("code")==0){
                            //JSONArray jsonArray=jsonObject.getJSONObject("data").getJSONArray("detail");
                            pic = jsonObject.getJSONObject("data").getString("pic");
                            json_data=jsonObject.getJSONObject("data").getString("detail");
                            // 返给handler的信息
                            Message msg = new Message();
                            msg.what = 2;
                            mListHandler.sendMessage(msg);
                            //return jsonArray.toString();

                        }else{
                            Message msg = new Message();
                            msg.what = 3;
                            mListHandler.sendMessage(msg);
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
    private void initData(View view , String json_data,String pic) {
        // 解析 JSON 数据
        Gson gson = new Gson();
        //String json_data = "["+"{"+"detail_name"+":"+"内存容量"+","+"detail_info"+":"+"128G"+"},{"+"detail_name"+":"+"运行内存"+","+"detail_info"+":"+"4G"+"}]";
        List<ShopDetail> ps = gson.fromJson(json_data,new TypeToken<List<ShopDetail>>(){}.getType());
        TableLayout tab = view.findViewById(R.id.tab_activity02);
        tab.setStretchAllColumns(true);
        TableRow tableRow1 = new TableRow(getActivity());
        tableRow1.setBackgroundColor(Color.WHITE);
        TextView textView3 = new TextView(getActivity());
        textView3.setText("商品品牌");
        textView3.setGravity(Gravity.CENTER);
        tableRow1.addView(textView3);
        TextView textView4 = new TextView(getActivity());
        textView4.setText(pic);
        textView4.setGravity(Gravity.CENTER);
        tableRow1.addView(textView4);
        tab.addView(tableRow1,new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT
        ));
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
//    public void inis1() {
//        MSListItem msListItem1=new MSListItem(1L,"iPhone13 白色",20,"7999","8226",
//                "https://img14.360buyimg.com/n1/s450x450_jfs/t1/186812/20/9246/188349/60cf09bcE6b187bc1/cf043125dafc7d77.jpg",
//                "裕华万达店","2021-12-31 20:00:00");
//        list.add(msListItem1);
//        MSListItem msListItem2=new MSListItem(1L,"iPhone13 mini 黑",30,"5199","5999",
//                "https://img14.360buyimg.com/n1/s450x450_jfs/t1/146138/18/23045/182460/618ba0e0E5bee8154/48265d15e6f5a138.jpg",
//                "万象城店","2021-11-31 20:20:00");
//        list.add(msListItem2);
//        MSListItem msListItem3=new MSListItem(1L,"iPhone13 pro 黑",20,"8999","9300",
//                "https://img12.360buyimg.com/n1/s450x450_jfs/t1/198509/34/16814/120270/618b9c91Ef633755e/eada1756f3459484.jpg",
//                "北国商城店","2021-11-22 20:30:11");
//        list.add(msListItem3);
//
//    }

    @Override
    public void onAttach(Activity activity) {
        //this.mContext = activity;
        super.onAttach(activity);
        this.activity = activity;
    }


}
