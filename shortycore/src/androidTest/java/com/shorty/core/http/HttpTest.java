package com.shorty.core.http;

import com.shorty.core.AsyncInstrumentationTestCase;
import com.shorty.core.http.action.HttpAction;
import com.shorty.core.http.action.HttpActionListener;
import com.shorty.core.manager.ManagerFactory;
import com.shorty.core.testEntry.AdvEntry;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class HttpTest extends AsyncInstrumentationTestCase {
    private HttpManager hm;
    public HttpTest() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        hm = (HttpManager) ManagerFactory.getInstance().getManager(HttpManager.class);
    }

    public synchronized void testHttpClientRequest() throws InterruptedException {
    }
}