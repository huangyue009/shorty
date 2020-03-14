package com.shorty.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记要生成单元测试代码的方法
 *
 * @author yuehuang
 * @version 1.0
 * @since 2020/2/15 10:29
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface UnitTest {
    /**
     * 入参赋值，支持表达式
     * <p>
     * 例如 方法 public void test(A attr1, int attr2, String attr3, Array[] attr4)
     * 表达式为
     *
     * @UnitTest(input="#attr1.pamar1=1, #attr1.pamar2='abc', #attr2=1.1, #attr3='def', #attr4=[1,1.2,'rty']")
     */
    String intput() default "";

    /**
     * 结果判断，支持多个判断，默认不填则除报错外都为通过
     * <p>
     * 例如 返回对象以$表示，
     *
     * @return
     * @UnitTest(assertResult="$=1") 对象的参数$.pamar1 pamar1为对象内的参数名
     * @UnitTest(assertResult="#.pamar1=1, #.pamar2='aaa'")
     */
    String assertResult() default "";

    /**
     * 是否异步方法测试
     * @return
     */
    boolean aync() default false;
}
