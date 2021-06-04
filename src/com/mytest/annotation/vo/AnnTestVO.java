package com.mytest.annotation.vo;

import com.mytest.annotation.MyAnn;


public interface AnnTestVO {

    @MyAnn(sendType = "get", url = "", order = 1)
    void get();
}
