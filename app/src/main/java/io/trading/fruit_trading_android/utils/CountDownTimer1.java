package io.trading.fruit_trading_android.utils;

import android.os.CountDownTimer;
import android.widget.Button;

//倒计时函数
public class CountDownTimer1 extends CountDownTimer {

    //用于倒计时的按钮
    private Button timeButton;

    public CountDownTimer1(long millisInFuture, long countDownInterval,Button timeButton) {
        super(millisInFuture, countDownInterval);
        this.timeButton=timeButton;
    }

    //计时过程
    @Override
    public void onTick(long l) {
        //防止计时过程中重复点击
        timeButton.setClickable(false);
        timeButton.setText(l/1000+"秒");

    }

    //计时完毕的方法
    @Override
    public void onFinish() {
        //重新给Button设置文字
        timeButton.setText("获取验证码");
        //设置可点击
        timeButton.setClickable(true);
    }

}


