  shorty是一个快速APP开发的工具套装，能快速的让app拥有http,数据库，事件总线，log工具，ui模板化等基本功能。并能无缝的接入其他继承了BaseManager的工具模块。
  基本模块介绍：
  1、UI模板基类BaseActivity
    提供公用跳转方法redirect，插件模块获得方法getManager(Class<? extents BaseManager>)，eventBus生命周期限制（自动销毁event防止activity生命周期外调用View造成闪退）
    
  2、Http模块
    a、如果Activty继承BaseActivity(UI模板基类)
        HttpManager httpManager = getManager(HttpManager.class);
        //Class<？extents BaseParse> 是自定义解析器的基类，不填第三个参数使用默认JsonParse, 
        HttpAction action = new HttpAction("http://apis.baidu.com/apistore/aqiservice/aqi", HttpAction.GET，Class<？extents BaseParse>);
        action.putParam("city", "xx");   //设置参数
        action.setHeader("apikey", "xxxxxxx"); //设置header
        action.setHttpActionListener(new HttpActionListener<TestEntry>() {    //TestEntry是经过解析器后得到对象
            @Override
            public void onSuccess(JSONObject result) {  //成功，返回数据对象
            }

            @Override
            public void onFailure(int resultCode, String error) { //失败，返回错误和代码
            }
        });
        httpManager.submit(action);
    b、如果Activty没有继承BaseActivity(UI模板基类)
        HttpManager httpManager = (HttpManager) ManagerFactory.getInstance().getManager(HttpManager.class);
        HttpAction action = new HttpAction("http://apis.baidu.com/apistore/aqiservice/aqi", HttpAction.GET);
        httpManager.submit(action);
        
  
