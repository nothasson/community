package com.hasson.community.entity;

//封装分页相关的信息。
public class Page {
    //当前页码
    private int current = 1;

    //显示的上线
    private int limit = 10;

    //数据总数 用于计算总页数
    private int rows;

    //查询路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current > 0)
            this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit > 0 && limit <= 100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows > 0)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", rows=" + rows +
                ", path='" + path + '\'' +
                '}';
    }

    //获取当前页的起始行
    public int getOffset() {
        return (current - 1) * limit;
    }

    /*
     * 获取总的页数*/
    public int getTotal() {
        //整除还是不整除
        return rows % limit == 0 ? rows / limit : rows / limit + 1;
    }

    public int getFrom() {
        return current - 2 > 0 ? current - 2 : 1;
    }

    public int getTo() {
        return current + 2 > getTotal() ? getTotal() : current + 2;
    }
}
