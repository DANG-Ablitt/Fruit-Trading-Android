package io.trading.fruit_trading_android.entity;

/**
 * 商品属性
 */
public class ShopDetail {
    /**
     * 商品属性名
     */
    private String detail_name;

    /**
     * 商品属性内容
     */
    private String detail_info;

    /**
     * 有参构造器
     */
    public ShopDetail(String detail_name, String detail_info) {
        this.detail_name = detail_name;
        this.detail_info = detail_info;
    }

    /**
     * Getter 和 Setter
     */
    public String getDetail_name() {
        return detail_name;
    }

    public void setDetail_name(String detail_name) {
        this.detail_name = detail_name;
    }

    public String getDetail_info() {
        return detail_info;
    }

    public void setDetail_info(String detail_info) {
        this.detail_info = detail_info;
    }
}