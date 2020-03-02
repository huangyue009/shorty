package com.shorty.test.utils;

/**
 * @author yuehuang
 * @version 1.0
 * @since 2020-02-15
 */
public class Consts {
    private static final String FACADE_PACKAGE = "com.shorty.test";
    public static final String PROJECT = "Shorty";

    public static final String ANNOTATION_TYPE_UNITTEST = FACADE_PACKAGE + ".annotation.UnitTest";

    //method name
    public static final String TEST_SET_UP = "setUp";
    public static final String TEST_TEAR_DOWN = "tearDown";

    //parmas name
    public static final String INSTANCE = "instance";
    public static final String RESULT = "result";


    // Java type
    private static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String CHAR = LANG + ".Character";
    public static final String STRING = LANG + ".String";
    public static final String SERIALIZABLE = "java.io.Serializable";
    public static final String PARCELABLE = "android.os.Parcelable";

    // Log
    static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler ";

    // code template
//    public static final String Me = LANG + ".String";
}
