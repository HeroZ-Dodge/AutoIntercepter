package com.dodge.plugin;

/**
 * Created by linzheng on 2019/3/27.
 */

public class TestClass4 {

    @TestAnnotation
    public void test() {


        System.out.println("Test");
        System.out.println("Test");


    }




    @TestAnnotation
    public static void test12(String str) {

        System.out.println(str);


    }




}
