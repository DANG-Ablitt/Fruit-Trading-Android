package io.trading.fruit_trading_android.dto;

public class LoginDTO {
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 短信验证码
     */
    private String captcha;

    /**
     * 有参构造器
     */
    public LoginDTO(String mobile, String captcha) {
        this.mobile = mobile;
        this.captcha = captcha;
    }

    /**
     * getter和setter
     */
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
