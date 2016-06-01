package com.shorty.core.database;

import android.test.AndroidTestCase;

import com.shorty.core.manager.ManagerFactory;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class DatabaseTest extends AndroidTestCase {
    private DaoManager dm;
    public DatabaseTest() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dm = (DaoManager) ManagerFactory.getInstance().getManager(DaoManager.class);
    }

    public void testDropTable(){
        dm.dropTable(TestTableEntry.class);
    }
}