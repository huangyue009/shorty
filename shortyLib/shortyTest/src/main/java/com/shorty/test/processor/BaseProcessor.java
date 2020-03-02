package com.shorty.test.processor;

import com.shorty.test.utils.Logger;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;

/**
 * @author yuehuang
 * @version 1.0
 * @since 2020-02-15
 */
public abstract class BaseProcessor extends AbstractProcessor {
    Logger logger;
    Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        logger = new Logger(processingEnvironment.getMessager());
        mFiler = processingEnv.getFiler();
    }
}
