package com.mytest.forest;

import com.dtflys.forest.annotation.Request;

public interface MyClient1 {

    @Request(url = "localhost:8080/api/GetDataTestApi/getDataTest")
    String simpleRequest();
}
