package com.dodge.plugin;


/**
 * Created by linzheng on 2019/3/27.
 */

public class TestClass {

    @TestAnnotation
    public void test() {

        if (TestIntercepter.intercept()) {
            return;
        }

        System.out.println("Test");

    }


    public boolean test1() {
        if (TestIntercepter.intercept()) {
            return false;
        }


        return true;
    }


    public int test3() {
        if (TestIntercepter.intercept()) {
            return -1;
        }

        return 0;
    }


    public String test4() {
        if (TestIntercepter.intercept()) {
            return null;
        }

        return null;
    }


    public int[] test12() {
        return null;
    }

    public byte test324() {
        return -1;
    }

    public byte[] test2342() {
        return null;
    }


    public long test7() {
        if (TestIntercepter.intercept()) {
            return -1;
        }

        return 0L;
    }

    public Long test8() {

        if (TestIntercepter.intercept()) {
            return -1L;
        }

        return 1L;


    }


    public double test9() {
        if (TestIntercepter.intercept()) {
            return -1;
        }

        return -1;
    }



    public float test10() {
        if (TestIntercepter.intercept()) {
            return -1;
        }

        return -1;
    }



}
