package io.trading.fruit_trading_android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import io.trading.fruit_trading_android.R;

public class QianggouShopFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (ViewGroup) inflater.inflate(
                //R.layout.fragment_screen_slide_page1, container, false);
                R.layout.fragment_qianggou_list, container, false);
    }
}
