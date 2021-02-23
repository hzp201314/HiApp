package com.hzp.hiapp.model;

import java.util.List;

public class BottomBar {
    /**
     * selectTab : 0
     * tabs : [{"size":24,"enable":true,"index":0,"pageUrl":"main/tabs/home","title":"Home"},{"size":24,"enable":true,"index":1,"pageUrl":"main/tabs/dashboard","title":"Dashboard"},{"size":24,"enable":true,"index":2,"pageUrl":"main/tabs/notification","title":"Notification"}]
     */

    private int selectTab;
    private List<Tab> tabs;

    public int getSelectTab() {
        return selectTab;
    }

    public void setSelectTab(int selectTab) {
        this.selectTab = selectTab;
    }

    public List<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(List<Tab> tabs) {
        this.tabs = tabs;
    }

    public static class Tab {
        /**
         * size : 24
         * enable : true
         * index : 0
         * pageUrl : main/tabs/home
         * title : Home
         */

        private int size;
        private boolean enable;
        private int index;
        private String pageUrl;
        private String title;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getPageUrl() {
            return pageUrl;
        }

        public void setPageUrl(String pageUrl) {
            this.pageUrl = pageUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
