package com.shorty.demo.controll;

import java.lang.Exception;
import java.lang.String;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestOneTest {
  private TestOne instance;

  @Before
  public void setUp() throws Exception {
    instance = new TestOne();
  }

  @After
  public void tearDown() throws Exception {
    instance = null;
  }

  @Test
  public void test2() throws Exception {
    TestEntry test2 = new TestEntry();
    test2.string3 = "aaa";
    test2.anInt1 = 1;
    test2.aDouble2 = 1.1d;
    TestEntry result = instance.test2(test2);
    Assert.assertTrue(result.anInt1==1);
    Assert.assertTrue(result.aDouble2==1.1);
    Assert.assertEquals(result.string3, "aaa");
  }

  @Test
  public void test3() throws Exception {
    TestEntry test2 = new TestEntry();
    test2.string3 = "aaa";
    test2.anInt1 = 1;
    test2.aDouble2 = 1.1d;
    String result = instance.test3(test2);
    Assert.assertEquals(result, "'aaa'");
  }

  @Test
  public void test() throws Exception {
    instance.test();
  }

  @Test
  public void test6051() throws Exception {
    int test2 = 1;
    String test = "aaa";
    int result = instance.test(test2,test);
    Assert.assertTrue(result==1);
  }
}
