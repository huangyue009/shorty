package com.shorty.demo.controll;

import com.shorty.test.annotation.UnitTest;

/**
 * @author yuehuang
 * @version 1.0
 * @since 2020-02-15
 */
public class TestOne {
    public void init(){
        System.out.println("初始化");
    }

    @UnitTest
    public void test(){
        System.out.println("测试内容");
    }

    @UnitTest(intput = "#test='aaa', #test2 = 1",
     assertResult = "#=1")
    public int test(int test2, String test){
        System.out.println("测试内容" + test + " test2 = " + test2);
        return 1;
    }

    @UnitTest(intput = "#test2.string3='aaa', #test2.anInt1 = 1, #test2.aDouble2 = 1.1d",
            assertResult = "#.anInt1=1, #.aDouble2=1.1, #.string3='aaa'")
    public TestEntry test2(TestEntry test2){
        TestEntry testEntry = new TestEntry();
        testEntry.anInt1 = test2.anInt1;
        testEntry.aDouble2 = test2.aDouble2;
        testEntry.string3 = test2.string3;

        return testEntry;
    }

    @UnitTest(intput = "#test2.string3='aaa', #test2.anInt1 = 1, #test2.aDouble2 = 1.1d",
            assertResult = "#='aaa'")
    public String test3(TestEntry test2){
        TestEntry testEntry = new TestEntry();
        testEntry.anInt1 = test2.anInt1;
        testEntry.aDouble2 = test2.aDouble2;
        testEntry.string3 = test2.string3;

        return testEntry.string3;
    }
}
