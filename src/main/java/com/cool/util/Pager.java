package com.cool.util;

/**
 * Created by codelover on 17/3/31.
 * 分页类
 */
public class Pager {
    private int pageSize;
    private int currentPage;
    private int recordCount;
    private boolean isFirstPage;
    private boolean isLastPage;

    private Pager(){

    }

    private Pager(int pageSize, int currentPage, int recordCount) {
        this.pageSize = pageSize;
        setCurrentPage(currentPage);
        this.recordCount = recordCount;
    }
    /**
     * 返回指定的记录在总的结果集中的行号
     * @param index 当前记录在当前页中的第几行，从0开始
     * @return 总的结果集中的行号,行号从1开始
     */
    public int getRowNumber(int index){
        return (currentPage - 1) * pageSize + index + 1;
    }

    public boolean getIsFirstPage(){
        return this.currentPage == 1;
    }
    public boolean getIsLastPage(){
        return this.currentPage == this.getPageNum() || this.getPageNum() == 0;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getPageNum(){
        return ((int)recordCount  +  pageSize  - 1) / pageSize;
    }


    /**
     * 获取记录开始的行号
     * @return
     */
    public int getStartRowCount(){
        return ((currentPage - 1) * pageSize)+1;
    }

    /**
     * 获取记录结束的行号
     * @return
     */
    public int getEndRowCount(){
        return getStartRowCount()+pageSize;
    }


    /**
     * 获取页大小，一页有几条记录数
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置页大小
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取当前页码，从1开始
     * @return
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 设置当前页码
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        this.isFirstPage = getIsFirstPage();
        this.isLastPage = getIsLastPage();
    }

    /**
     * 获取总记录数
     * @return
     */
    public int getRecordCount() {
        return recordCount;
    }

    /**
     * 设置总记录数
     * @param recordCount
     */
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    /**
     * 创建一个pager,初始化正确的分页信息
     * @param currentPage 当前页码，从1开始
     * @param pageSize 页大小，一页几条记录
     * @param recordCount 总记录数
     * @return pager
     */
    public static Pager createPager(int currentPage, int pageSize, int recordCount){
        if (currentPage == 0)
        currentPage = 1;
        if (pageSize == 0)
            pageSize = 30;
        Pager pager = new Pager(pageSize, currentPage, recordCount);
        int pages = pager.getPageNum();
        if (currentPage > pages && pages != 0) {
            currentPage = pages;
            pager.setCurrentPage(currentPage);
        }
        return pager;
    }
}
