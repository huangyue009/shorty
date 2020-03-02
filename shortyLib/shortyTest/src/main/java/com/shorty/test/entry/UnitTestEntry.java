package com.shorty.test.entry;

import com.squareup.javapoet.MethodSpec;

import java.util.HashMap;
import java.util.Random;

import javax.lang.model.element.Element;


/**
 * @author yuehuang
 * @version 1.0
 * @since 2020-02-16
 */
public class UnitTestEntry {
    public Element classElement;
    private HashMap<String, MethodSpec> methodSpecs;

    public UnitTestEntry(Element classElement) {
        this.classElement = classElement;
        this.methodSpecs = new HashMap<>();
    }

    public String getMethodSpecName(String methodSpecName) {
        if (methodSpecs.containsKey(methodSpecName)) {
            return methodSpecName + new Random().nextInt(9999);
        } else {
            return methodSpecName;
        }
    }

    public void addMethodSpec(MethodSpec methodSpec) {
        this.methodSpecs.put(methodSpec.name, methodSpec);
    }

    public Iterable<MethodSpec> getMethodSpecs() {
        return methodSpecs.values();
    }

    public void clear() {
        methodSpecs.clear();
        classElement = null;
    }
}
