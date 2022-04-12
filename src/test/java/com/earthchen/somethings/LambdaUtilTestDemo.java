package com.earthchen.somethings;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class LambdaUtilTestDemo {


    @Test
    void getFieldName() {

        String aaa = LambdaUtil.getFieldName(TestDemo::getAaa);
        String bbb = LambdaUtil.getFieldName(TestDemo::getBbb);
        String ccc = LambdaUtil.getFieldName(TestDemo::getCcc);

        Map<String, String> map = LambdaUtil.getAllFieldNameMap(TestDemo.class);

        System.out.println(map);

        System.out.println(String.format("aaa: %s, bbb: %s, ccc: %s", aaa, bbb, ccc));


    }
}