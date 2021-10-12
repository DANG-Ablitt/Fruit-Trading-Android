package io.trading.fruit_trading_android.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import io.trading.fruit_trading_android.fragment.MiaoshaShopFragment;
import io.trading.fruit_trading_android.fragment.QianggouShopFragment;

public class ShopAdapter extends FragmentStateAdapter {

    //设置显示的页数（1.秒杀 2.抢购）
    private static final int NUM_PAGES = 2;

    public ShopAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return new MiaoshaShopFragment();
        }
        else{
            return new QianggouShopFragment();
        }

    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
