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
        ManagerFactory.init(getContext());
        dm = (DaoManager) ManagerFactory.getInstance().getManager(DaoManager.class);
        dm.getEntryIndexMap().put(TestTableEntry.class.getName(), dm.getHelper().createTable(TestTableEntry.class));
    }

//    public void testDropTable(){
//        try {
//            dm.getEntryById(TestTableEntry.class, 1l);
//        } catch (Exception e) {
//          assertTrue("创建数据库失败", false);
//        }
//        dm.dropTable(TestTableEntry.class);
//        try {
//            dm.getEntryById(TestTableEntry.class, 1l);
//        } catch (Exception e) {
//            return;
//        }
//        assertTrue("删除数据库失败", false);
//    }

    /**
     * 测试增删改查
     */
    public void testIDUSData() throws IllegalAccessException, InstantiationException {
        TestTableEntry entry = new TestTableEntry();
        entry.boolId = true;
        entry.eid = 12313;
        entry.longID = 13123l;
        entry.stringID = "ddddd";

        dm.save(entry);
        TestTableEntry tte = dm.getEntryById(TestTableEntry.class, entry.id);
        assertNotNull(tte);

        entry.stringID = "aaaa";
        dm.save(entry);

        tte = dm.getEntryById(TestTableEntry.class, entry.id);
        assertEquals("update failure", tte.stringID, "aaaa");

        dm.delete(entry);
        tte = dm.getEntryById(TestTableEntry.class, entry.id);
        assertNull(tte);
    }

}