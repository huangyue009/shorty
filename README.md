  shorty是一个快速APP开发的工具套装，能快速的让app拥有http,数据库，事件总线，log工具，ui模板化等基本功能。并能无缝的接入其他继承了 BaseManager 的工具模块。
  基本模块介绍：

  1、UI模板基类BaseActivity</br>
    提供公用跳转方法redirect，插件模块获得方法getManager(Class<? extents BaseManager>)，eventBus生命周期限制（自动销毁event防止activity生命周期外调用View造成闪退）
    
  2、Http模块</br>
    a、如果Activty继承BaseActivity(UI模板基类)</br>
        HttpManager httpManager = getManager(HttpManager.class);</br>
        //Class<？extents BaseParse> 是自定义解析器的基类，不填第三个参数使用默认JsonParse, </br>
        HttpAction action = new HttpAction("http://apis.baidu.com/apistore/aqiservice/aqi", HttpAction.GET，Class<？extents BaseParse>);</br>
        action.putParam("city", "xx");   //设置参数</br>
        action.setHeader("apikey", "xxxxxxx"); //设置header</br>
        action.setHttpActionListener(new HttpActionListener<TestEntry>() {    //TestEntry是经过解析器后得到对象</br>
            Override</br>
            public void onSuccess(JSONObject result) {  //成功，返回数据对象</br>
            }</br>

            Override</br>
            public void onFailure(int resultCode, String error) { //失败，返回错误和代码</br>
            }</br>
        });</br>
        httpManager.submit(action);</br>
    b、如果Activty没有继承BaseActivity(UI模板基类)</br>
          HttpManager httpManager = (HttpManager) ManagerFactory.getInstance().getManager(HttpManager.class);</br>
          HttpAction action = new HttpAction("http://apis.baidu.com/apistore/aqiservice/aqi", HttpAction.GET);</br>
          httpManager.submit(action)；

  3、数据库模块</br>
    <p>
     a、设置生成表
      @DatabaseTable(tableName = "t_account")
      public class TestEntry extends BaseEntry {
    
        @DatabaseField(columnName = "ks")
        public String ks;
   
        @DatabaseField(columnName = COLUMN_USER_NAME, unioueIndex = true)  //unioueIndex标记为索引，当这个标记为ture时有相同的save为替代
        public String userName;
      }
   
  </p>
