package io.trading.fruit_trading_android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

import io.trading.fruit_trading_android.R;
import io.trading.fruit_trading_android.adapter.MSListAdapter;
import io.trading.fruit_trading_android.adapter.ShopAdapter;
import io.trading.fruit_trading_android.entity.MSListItem;

public class MiaoshaShopFragment extends Fragment {

    ViewPager2 viewPager;
    private List<MSListItem> list = new ArrayList<>();

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
        return v;
    }

    /**
     * 从服务器加载商品列表
     */
    public void inis() {

    }

    /**
     * 为达到演示需要使用模拟数据
     */
    public void inis1() {
        MSListItem msListItem1=new MSListItem(1L,"iPhone13 白色",20,"7999","8226",null,"2.5K视网膜屏幕-8G运行内存-128G存储空间-2560大电池");
        list.add(msListItem1);
        MSListItem msListItem2=new MSListItem(1L,"iPhone13 mini 黑",30,"5199","5999",null,"2K视网膜屏幕-6G运行内存-128G存储空间-2000大电池");
        list.add(msListItem2);
        MSListItem msListItem3=new MSListItem(1L,"iPhone13 pro 黑",20,"8999","9300",null,"2.5K视网膜屏幕-8G运行内存-256G存储空间-2700大电池");
        list.add(msListItem3);

    }


}
