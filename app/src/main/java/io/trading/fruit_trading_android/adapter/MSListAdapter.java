package io.trading.fruit_trading_android.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;
import io.trading.fruit_trading_android.R;
import io.trading.fruit_trading_android.entity.MSListItem;

public class MSListAdapter extends ArrayAdapter<MSListItem> {

    //用于绑定布局
    private int layoutId;

    public MSListAdapter(Context context, int layoutId, List<MSListItem> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MSListItem item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        //绑定商品图片
        ImageView imageView = (ImageView) view.findViewById(R.id.image1);
        Glide.with(getContext()).load(item.getUrl()).into(imageView);
        //绑定店铺名称
        TextView textViewaddress = (TextView) view.findViewById(R.id.tv_source_name);
        //textViewaddress.setText(item.getDept());
        //绑定开始时间
        TextView textViewtime = (TextView) view.findViewById(R.id.detail1);
        textViewtime.setText(item.getTime2());
        //绑定商品名称
        TextView textViewname = (TextView) view.findViewById(R.id.name);
        textViewname.setText(item.getName());
        //绑定库存
        TextView textViewcount = (TextView) view.findViewById(R.id.detail12);
        textViewcount.setText(item.getCount().toString());
        //绑定商品优惠价
        TextView textViewamount2 = (TextView) view.findViewById(R.id.amount2);
        textViewamount2.setText("￥"+item.getAmount1());
        //绑定商品原价
        TextView textViewamount1 = (TextView) view.findViewById(R.id.amount1);
        textViewamount1 .getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
        textViewamount1.setText("原"+item.getAmount2());
        // 按钮
        Button button=(Button)view.findViewById(R.id.btLogin1);
        button.setOnClickListener((v)->{
            Toast.makeText(getContext(),"77777",Toast.LENGTH_LONG).show();
        });
        Button button1=(Button)view.findViewById(R.id.btLogin);
        button1.setOnClickListener((v)->{
            Toast.makeText(getContext(),"888888",Toast.LENGTH_LONG).show();
        });
        return view;
    }
}
