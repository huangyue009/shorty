package com.shorty.core.http.action;


import java.io.File;


/**
 * 上传多媒体文件
 *
 * Created by yue.huang on 2016/5/27.
 */
public class MultiPartAction extends HttpAction{
//    private MultipartEntity multipartEntity;

    public MultiPartAction(String url, int requestType) {
        super(url, requestType);
//        multipartEntity = new MultipartEntity();
    }

    public void addPart(String key, String value) {
//        StringPart part = new StringPart(key, value, DEFAULT_PARAMS_ENCODING);
//        multipartEntity.addPart(part);
    }

    public void addPart(String key, File file) {
//        FilePart part = new FilePart(key, file, null, null);
//        multipartEntity.addPart(part);
    }

//    public MultipartEntity getMultipartEntity() {
//        return multipartEntity;
//    }
}
