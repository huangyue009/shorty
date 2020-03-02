package com.shorty.encrypt;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ShortyEncryptTest {
    private ShortyEncrypt instance;

    @Before
    public void setUp() throws Exception {
        instance = new ShortyEncrypt();
    }

    @After
    public void tearDown() throws Exception {
        instance = null;
    }

    @Test
    public void aesEncrypt() {
        String key = "121345";
        byte[] result = instance.aesEncrypt(msg, key);
    }
}
