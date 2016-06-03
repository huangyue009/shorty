package com.shorty.core.http;

import android.test.InstrumentationTestCase;

import com.shorty.core.AsyncInstrumentationTestCase;
import com.shorty.core.http.action.HttpAction;
import com.shorty.core.http.action.HttpActionListener;
import com.shorty.core.http.constant.HttpMethodType;
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
            HttpAction action = new HttpAction(HttpMethodType.SAMPLE);
            action.putParam("wid", String.valueOf(480));
            action.putParam("len", String.valueOf(720));
            action.putParam("lng", String.valueOf(23.33813));
            action.putParam("lat", String.valueOf(60.2231));
            action.putParam("v", "1.1.8");
            action.putParam("appname", "rgbox");
            action.putParam("ostype", "1");
            action.putParam("apptype", "3");
            action.putParam("apptype", "3");
            action.putParam("sessionid", "D7534A157DA2F451BC01909F70BD3F78");
            action.setHttpActionListener(new HttpActionListener<AdvEntry>() {
                @Override
                public void onSuccess(final AdvEntry result) {
                    assertNotNull(result);
                    asyncNotify();
                }

                @Override
                public void onFailure(int resultCode, final String error) {
                    assertTrue(error, false);
                    asyncNotify();
                }
            });

            hm.submit(action);
            asyncWait();
    }
}