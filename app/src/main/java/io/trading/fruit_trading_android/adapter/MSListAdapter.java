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
        //ImageView imageView = (ImageView) view.findViewById(R.id.item_img);
        //imageView.setImageResource(item.getImgId());
        //绑定商品名称
        TextView textViewname = (TextView) view.findViewById(R.id.name);
        textViewname.setText(item.getName());
        /**
         * 逐一绑定商品重要参数
         */
        //提取参数字符串
        String detail=item.getDetail();
        //绑定
        TextView textViewdetail1 = (TextView) view.findViewById(R.id.detail1);
        TextView textViewdetail2 = (TextView) view.findViewById(R.id.detail2);
        //判断是否为空
        if(detail!=null){
            // 分割字符串
            String[] temp = detail.split("-");
            if(temp.length>4||temp.length==4){
                textViewdetail1.setText(temp[0]+"|"+temp[1]);
                textViewdetail2.setText(temp[2]+"|"+temp[3]);
            }else {
                if(temp.length<4&&temp.length%2==0&&temp.length!=0){
                    textViewdetail1.setText(temp[0]+"|"+temp[1]);
                    textViewdetail2.setText(null);
                }
                if(temp.length<4&&temp.length%2!=0){
                    if(temp.length==1){
                        textViewdetail1.setText(temp[0]);
                        textViewdetail2.setText(null);
                    }else{
                        textViewdetail1.setText(temp[0]+"|"+temp[1]);
                        textViewdetail2.setText(temp[2]);
                    }
                }
            }
        }else{
            textViewdetail1.setText("请到商品详细页面查看更多");
            textViewdetail2.setText(null);
        }
        //绑定库存
        TextView textViewcount = (TextView) view.findViewById(R.id.count);
        textViewcount.setText("库存:"+item.getCount());
        //绑定商品优惠价
        TextView textViewamount2 = (TextView) view.findViewById(R.id.amount2);
        textViewamount2.setText("￥"+item.getAmount1());
        //绑定商品原价
        TextView textViewamount1 = (TextView) view.findViewById(R.id.amount1);
        textViewamount1.setText("原"+item.getAmount2());
        return view;
    }
}
