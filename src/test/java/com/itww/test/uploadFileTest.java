package com.itww.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 18:49
 * @Description: This is description of class
 */


public class uploadFileTest {

    @Test
    public void test1(){
        String fileName = "dsaf.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}
