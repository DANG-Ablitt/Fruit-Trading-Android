package io.trading.fruit_trading_android.entity;

/**
 * 用于秒杀商品列表加载
 */
public class MSListItem {
    /**
     * id
     */
    private Long id;
    /**
     * 产品名称
     */
    private String name;
    /**
     * 取货门店
     */
    //private String dept;
    /**
     * 开始时间
     */
    private String time2;
    /**
     * 库存数量
     */
    private Integer count;
    /**
     * 优惠价
     */
    private String amount1;
    /**
     * 原价
     */
    private String amount2;
    /**
     * 图片url
     */
    private String url;

    /**
     * 有参构造器
     */
    public MSListItem(Long id, String name, Integer count,
                      String amount1, String amount2,
                      String url,String time2) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.amount1 = amount1;
        this.amount2 = amount2;
        this.url = url;
        //this.dept=dept;
        this.time2=time2;
    }

    /**
     * getter和setter
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getAmount1() {
        return amount1;
    }

    public void setAmount1(String amount1) {
        this.amount1 = amount1;
    }

    public String getAmount2() {
        return amount2;
    }

    public void setAmount2(String amount2) {
        this.amount2 = amount2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    public String getDept() {
//        return dept;
//    }
//
//    public void setDept(String dept) {
//        this.dept = dept;
//    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }
}
