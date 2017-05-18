package com.hjx.v2ex.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaxiboy on 2017/3/24 0024.
 */

public class PageData<T> {

    private int currentPage;
    private int totalPage;
    private int totalItems;
    private List<T> currentPageItems = new ArrayList<>();

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<T> getCurrentPageItems() {
        return currentPageItems;
    }

    public void setCurrentPageItems(List<T> currentPageItems) {
        this.currentPageItems = currentPageItems;
    }

    @Override
    public String toString() {
        return "PageData{" +
                "currentPage=" + currentPage +
                ", totalPage=" + totalPage +
                ", totalItems=" + totalItems +
                ", currentPageItems=" + currentPageItems +
                '}';
    }
}
