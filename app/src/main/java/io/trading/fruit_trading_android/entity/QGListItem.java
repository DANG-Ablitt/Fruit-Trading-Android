package io.trading.fruit_trading_android.entity;

/**
 * 用于秒杀商品列表加载
 */
public class QGListItem {
    /**
     * id
     */
    private Long id;
    /**
     * 产品名称
     */
    private String name;
    /**
     * 数量
     */
    private Integer count;
    /**
     * 原价
     */
    private String amount1;
    /**
     * 已经预约人数
     */
    private Integer peoplecount;
    /**
     * 图片url
     */
    private String url;
    /**
     * 详细参数
     */
    private String detail;

    /**
     * 有参构造器
     */
    public QGListItem(Long id, String name,
                      Integer count, String amount1,
                      Integer peoplecount, String url,
                      String detail) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.amount1 = amount1;
        this.peoplecount = peoplecount;
        this.url = url;
        this.detail = detail;
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

    public Integer getPeoplecount() {
        return peoplecount;
    }

    public void setPeoplecount(Integer peoplecount) {
        this.peoplecount = peoplecount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
