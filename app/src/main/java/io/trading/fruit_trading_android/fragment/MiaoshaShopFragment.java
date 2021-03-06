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

    /* ???????????? */
    private static final int LIST_SUCCESS = 0;// ??????????????????
    private static final int LIST_FAILURE = 1;// ??????????????????
    private static final int INFO_SUCCESS = 2;// ??????????????????
    private static final int INFO_FAILURE = 3;// ??????????????????
    ViewPager2 viewPager;
    private List<MSListItem> list = new ArrayList<>();
    Activity activity;
    //????????????????????????
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
                    Toast.makeText(getActivity().getApplicationContext(), "?????????????????? ???????????????", Toast.LENGTH_LONG).show();
                    break;
                case INFO_SUCCESS:
                    PopupWindow_info(v1,shop_id,url,pic);
                    break;
                case INFO_FAILURE:
                    Toast.makeText(getActivity().getApplicationContext(), "???????????? ???????????????", Toast.LENGTH_LONG).show();
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
        //????????????????????????
        inis();
        //??????????????????
        //inis1();
        listView = (ListView) v.findViewById(R.id.listview);
        msListAdapter = new MSListAdapter(this.getContext(), R.layout.fragment_miaosha_list, list);
        listView.setAdapter(msListAdapter);
        // ListView ????????????
        listView.setOnItemClickListener((adapterView,view,i,l)->{
            // ????????????id ??? url
            ListView listView = (ListView)adapterView;
            MSListItem msListItem = (MSListItem)listView.getItemAtPosition(i);
            shop_id = msListItem.getId();
            url = msListItem.getUrl();
            v1=v;
            //??????????????????????????????
            //PopupWindow_info(v,shop_id,url);
            inis_data(shop_id);
        });
        return v;
    }

    /**
     *  ????????????????????????
     */
    private void PopupWindow_info(View view,Long shop_id,String url,String pic) {
        View popupView=LayoutInflater.from(getActivity()).inflate(R.layout.shop_info_main,null);
        //??????????????????
        popupWindow_info = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        //????????????????????????????????????????????????????????????
        // ?????? url ??????
        ImageView imageView = popupView.findViewById(R.id.image);
        Glide.with(getContext()).load(url).into(imageView);
        // ??????????????????
        //inis_data(shop_id);
        //????????????
        if (json_data != null){
            initData(popupView,json_data,pic);
        }
        //?????????????????????popup??????????????????
        popupWindow_info.setOutsideTouchable(true);
        popupWindow_info.setBackgroundDrawable(new BitmapDrawable());
        //???popup???????????????activity???????????????????????????????????????????????????
        popupWindow_info.setFocusable(true);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        //????????????   ??????????????????
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        popupWindow_info.setHeight(height-height/3);
        //??????popup
        popupWindow_info.showAsDropDown(view);
    }

    /**
     * ??????????????????????????????
     */
    public void inis() {
        list.clear();
        RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
        //???????????????
        String url = URLconfig.URL_BOOT+URLconfig.URL_LISTMS;
        Log.i("shop_list", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, (response)->{
                    Log.i("shop_list_response", "???????????????" + response.toString());
                    //?????????json?????????????????????json??????
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        //??????code???msg
                        Log.i("shop_list_response_data", "code???" + jsonObject.getInt("code"));
                        Log.i("shop_list_response_data", "msg???" + jsonObject.getString("msg"));
                        Log.i("shop_list_response_data", "data???" + jsonObject.getJSONArray("data"));
                        if(jsonObject.getInt("code")==0){
                            //
                            Gson gson = new Gson();
                            List<MSListItem> ps = gson.fromJson(jsonObject.getJSONArray("data").toString(), new TypeToken<List<MSListItem>>(){}.getType());
                            for(int i = 0; i < ps.size() ; i++)
                            {
                                MSListItem p = ps.get(i);
                                list.add(p);
                            }
                            // ??????handler?????????
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
        //??????????????????
        queue.add(jsonObjectRequest);
    }

    /**
     * ??????????????????????????????
     */
    public void list_update() {
        //??????????????????
        new Thread(){
            public void run() {
                //Log.i("xuhaitao", "???????????????");
                while (true) {
                    try {
                        //????????????10??????????????????
                        Thread.sleep(10000);
                        // ??????????????????????????????
                        inis();
                        //????????????
                        Message message=new Message();
                        message.what=0;
                        mListHandler.sendMessage(message);
                        Log.i("Thread", "???????????????");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * ????????????????????????????????????
     */
    public void inis_data(Long shop_id) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        //???????????????
        String url = URLconfig.URL_BOOT+URLconfig.URL_INFOMS + shop_id.toString();
        Log.i("shop_info", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, (response)->{
                    Log.i("shop_info_response", "???????????????" + response.toString());
                    //?????????json?????????????????????json??????
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        //??????code???msg
                        Log.i("shop_info_response_data", "code???" + jsonObject.getInt("code"));
                        Log.i("shop_info_response_data", "msg???" + jsonObject.getString("msg"));
                        Log.i("shop_info_response_data", "pic???" + jsonObject.getJSONObject("data").getString("pic"));
                        Log.i("shop_info_response_data", "detail???" + jsonObject.getJSONObject("data").getString("detail"));
                        if(jsonObject.getInt("code")==0){
                            //JSONArray jsonArray=jsonObject.getJSONObject("data").getJSONArray("detail");
                            pic = jsonObject.getJSONObject("data").getString("pic");
                            json_data=jsonObject.getJSONObject("data").getString("detail");
                            // ??????handler?????????
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
        //??????????????????
        queue.add(jsonObjectRequest);
    }

    /**
     * ?????????????????????JSON?????????????????????????????????
     */
    private void initData(View view , String json_data,String pic) {
        // ?????? JSON ??????
        Gson gson = new Gson();
        //String json_data = "["+"{"+"detail_name"+":"+"????????????"+","+"detail_info"+":"+"128G"+"},{"+"detail_name"+":"+"????????????"+","+"detail_info"+":"+"4G"+"}]";
        List<ShopDetail> ps = gson.fromJson(json_data,new TypeToken<List<ShopDetail>>(){}.getType());
        TableLayout tab = view.findViewById(R.id.tab_activity02);
        tab.setStretchAllColumns(true);
        TableRow tableRow1 = new TableRow(getActivity());
        tableRow1.setBackgroundColor(Color.WHITE);
        TextView textView3 = new TextView(getActivity());
        textView3.setText("????????????");
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
     * ???????????????????????????????????????
     */
//    public void inis1() {
//        MSListItem msListItem1=new MSListItem(1L,"iPhone13 ??????",20,"7999","8226",
//                "https://img14.360buyimg.com/n1/s450x450_jfs/t1/186812/20/9246/188349/60cf09bcE6b187bc1/cf043125dafc7d77.jpg",
//                "???????????????","2021-12-31 20:00:00");
//        list.add(msListItem1);
//        MSListItem msListItem2=new MSListItem(1L,"iPhone13 mini ???",30,"5199","5999",
//                "https://img14.360buyimg.com/n1/s450x450_jfs/t1/146138/18/23045/182460/618ba0e0E5bee8154/48265d15e6f5a138.jpg",
//                "????????????","2021-11-31 20:20:00");
//        list.add(msListItem2);
//        MSListItem msListItem3=new MSListItem(1L,"iPhone13 pro ???",20,"8999","9300",
//                "https://img12.360buyimg.com/n1/s450x450_jfs/t1/198509/34/16814/120270/618b9c91Ef633755e/eada1756f3459484.jpg",
//                "???????????????","2021-11-22 20:30:11");
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
