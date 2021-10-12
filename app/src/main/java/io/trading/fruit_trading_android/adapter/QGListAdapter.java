package io.trading.fruit_trading_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import io.trading.fruit_trading_android.R;
import io.trading.fruit_trading_android.entity.MSListItem;
import io.trading.fruit_trading_android.entity.QGListItem;

public class QGListAdapter extends ArrayAdapter<QGListItem> {

    //用于绑定布局
    private int layoutId;

    public QGListAdapter(Context context, int layoutId, List<QGListItem> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QGListItem item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        //ImageView imageView = (ImageView) view.findViewById(R.id.item_img);
        //TextView textView = (TextView) view.findViewById(R.id.item_text);
        //imageView.setImageResource(item.getImgId());
        //textView.setText(item.getName());
        return view;
    }
}
