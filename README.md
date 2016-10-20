  shorty是一个快速APP开发的工具套装，能快速的让app拥有http,数据库，事件总线，log工具，ui模板化等基本功能。并能无缝的接入其他继承了 BaseManager 的工具模块。
  基本模块介绍：

  1、UI模板基类BaseActivity</br>
    提供公用跳转方法redirect，插件模块获得方法getManager(Class<? extents BaseManager>)，eventBus生命周期限制（自动销毁event防止activity生命周期外调用View造成闪退）
    
  2、Http模块</br>
    a、如果Activty继承BaseActivity(UI模板基类)</br>
        
        HttpManager httpManager = getManager(HttpManager.class)；
        HttpAction action = new HttpAction("http://apis.baidu.com/apistore/aqiservice/aqi", HttpAction.GET，Class<？extents BaseParse>);//Class<？extents BaseParse> 是自定义解析器的基类，不填第三个参数使用默认JsonParse
        action.putParam("city", "xx");   //设置参数
        action.setHeader("apikey", "xxxxxxx"); //设置header
        action.setHttpActionListener(new HttpActionListener<TestEntry>() {    //TestEntry是经过解析器后得到对象
            @Override
            public void onSuccess(JSONObject result) {  //成功，返回数据对象
            }
                                         
            @Override<
            public void onFailure(int resultCode, String error) { //失败，返回错误和代码
            }
        });
        httpManager.submit(action);

    b、如果Activty没有继承BaseActivity(UI模板基类)</br>

          HttpManager httpManager = (HttpManager) ManagerFactory.getInstance().getManager(HttpManager.class);</br>
          HttpAction action = new HttpAction("http://apis.baidu.com/apistore/aqiservice/aqi", HttpAction.GET);</br>
          httpManager.submit(action)；

  3、数据库模块</br>
     设置生成表

      @DatabaseTable(tableName = "t_account")
      public class TestEntry extends BaseEntry {
        @DatabaseField(columnName = "ks")
        public String ks;
   
        @DatabaseField(columnName = COLUMN_USER_NAME, unioueIndex = true)  //unioueIndex标记为索引，当这个标记为ture时有相同的save为替代
        public String userName;
      }
      
      在Application中创建下表，如果已经创建会自动跳过，否则会创建

      DaoManager daoManager = (DaoManager) ManagerFactory.getInstance().getManager(DaoManager.class);
      daoManager.createTable(TestEntry.class);

      操作数据插入和更新都是
      
      daoManager.save(testEntry);
    
      其他查询，删除的也都在daoManager中

  4、evnetBus
    a、evnetBus是一个简单化的事件驱动模块，能够设置接收到事件后的运行线程

      eventManager = getManager(EventManager.class);
      eventManager.addEventListener("test", new EventListener() {   //注册事件 test为事件关键字  
              @Override
              @Subscribe(threadLevel = Subscribe.DEFAULT, oneTime = true)
              public void onEvent(Object event) {
              }
              @Override
              public void onFailed(int code, String error) {
              }
        });

        eventManager.sendEvent("test", "xxxxx");  //激活事件
        
    threadLevel 定义：
    /**
     * 在当前线程处理结果
     */
    int DEFAULT = 0;
    /**
     * 在主线程处理结果
     */
    int MAIN_THREAD = 1;
    /**
     * 当前为主线程线另开程处理结果,否则在当前线程处理
     */
    int BACKGROUND_THREAD = 2;
    /**
     * 新开线程处理结果
     */
    int ASYNC_THREAD = 3;

    oneTime定义是否为一次性事件，默认为是，激活一次后失效

    b、设置event与BaseActivity同生命周期
      创建EventListenerh时使用BaseActivity标记绑定生命周期的事件监听即可，finish时会自动删除监听

      new EventListener(getContextHash())；
   

  需要使用新的插件时可以新开model然后集成BaseManager,能直接被ManagerFactory获得，方便创建管理
