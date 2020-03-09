package com.shorty.test.processor;

import com.google.auto.service.AutoService;
import com.shorty.test.annotation.UnitTest;
import com.shorty.test.entry.UnitTestEntry;
import com.shorty.test.utils.TypeUtils;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import static com.shorty.test.utils.Consts.ANNOTATION_TYPE_UNITTEST;
import static com.shorty.test.utils.Consts.INSTANCE;
import static com.shorty.test.utils.Consts.RESULT;
import static com.shorty.test.utils.Consts.TEST_SET_UP;
import static com.shorty.test.utils.Consts.TEST_TEAR_DOWN;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * 测试标签@UnitTest 解析
 *
 * @author yuehuang
 * @version 1.0
 * @since 2020-02-15
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({ANNOTATION_TYPE_UNITTEST})
public class UnitTestProcessor extends BaseProcessor {
    private TypeUtils typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = new TypeUtils(processingEnvironment.getTypeUtils(), processingEnvironment.getElementUtils());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(UnitTest.class);
            try {
                logger.info(">>> make unit test file, start... <<<");
                return this.executeUnitTestFile(routeElements);

            } catch (Exception e) {
                logger.error(e);
            }

        }
        return false;
    }

    private boolean executeUnitTestFile(Set<? extends Element> routeElements) throws Exception {
        if (CollectionUtils.isNotEmpty(routeElements)) {
            HashMap<String, UnitTestEntry> typeSpecHashMap = new HashMap<>();

            for (Element element : routeElements) {
                TypeMirror classType = element.getEnclosingElement().asType();
                if (!typeSpecHashMap.containsKey(classType.toString())) {
                    typeSpecHashMap.put(classType.toString(), new UnitTestEntry(element.getEnclosingElement()));
                }

                UnitTestEntry unitTestEntry = typeSpecHashMap.get(classType.toString());
                String methodName = unitTestEntry.getMethodSpecName(element.getSimpleName().toString());
                MethodSpec.Builder methodSpecBuild = MethodSpec.methodBuilder(methodName)
                        .addException(Exception.class)
                        .addAnnotation(Test.class)
                        .addModifiers(PUBLIC);

                if (element instanceof ExecutableElement) {
                    methodSpecBuild = parseUnitTest((ExecutableElement) element, methodSpecBuild);
                    unitTestEntry.addMethodSpec(methodSpecBuild.build());
                }
            }

            JavaFile javaFile = null;
            for (UnitTestEntry unitTestEntry : typeSpecHashMap.values()) {
                PackageElement packageElement = (PackageElement) unitTestEntry.classElement.getEnclosingElement();
                TypeSpec typeSpec = createTestType(unitTestEntry.classElement, unitTestEntry.getMethodSpecs());
                javaFile = JavaFile.builder(packageElement.getQualifiedName().toString(), typeSpec).build();
                javaFile.writeTo(getTestDirPath(unitTestEntry.classElement));

                unitTestEntry.clear();
            }

            return true;
        }

        return false;
    }

    /**
     * 解析方法标签
     *
     * @param element
     * @param methodSpecBuild
     * @return
     */
    private MethodSpec.Builder parseUnitTest(ExecutableElement element, MethodSpec.Builder methodSpecBuild) {
        UnitTest unitTest = element.getAnnotation(UnitTest.class);
        List<VariableElement> variableElements = (List<VariableElement>) element.getParameters();

        if (variableElements.size() > 0 && StringUtils.isEmpty(unitTest.intput())) {
            logger.error(String.format("Method %s have parameters, You need set a input value at @UnitTest input field", element.getSimpleName()));
        }

        if (CollectionUtils.isEmpty(variableElements) && "void".equals(element.getReturnType().toString().toLowerCase())) {
            methodSpecBuild.addStatement(INSTANCE + ".$N()", element.getSimpleName());
        } else {
            if (!StringUtils.isEmpty(unitTest.intput())) {
                HashMap<String, String> paramsMap = new HashMap<>();
                HashMap<String, List<String>> objParamsMap = new HashMap<>();
                String input = unitTest.intput().substring(1).replaceAll(" ", "");
                String[] inputs = input.split(",#");
                for (String param : inputs) {
                    String key = param.substring(0, param.indexOf("="));
                    String value = param.substring(param.indexOf("=") + 1);

                    if (key.contains(".")) {
                        String[] kv = key.split("\\.");
                        if (!objParamsMap.containsKey(kv[0])) {
                            objParamsMap.put(kv[0], new ArrayList<>());
                        }
                        objParamsMap.get(kv[0]).add(key);
                    }
                    paramsMap.put(key, value);
                }

                StringBuilder paramesKey = new StringBuilder();
                Object[] paramesValue = new Object[variableElements.size() + 2];
                int i = 0;
                paramesValue[i++] = element.getReturnType();
                paramesValue[i++] = element.getSimpleName();


                for (VariableElement variableElement : variableElements) {
                    String paramName = variableElement.getSimpleName().toString();
                    // Primitive
                    if (variableElement.asType().getKind().isPrimitive()) {
                        methodSpecBuild.addStatement("$T $N = $N", variableElement.asType(),
                                paramName,
                                paramsMap.get(paramName));
                    } else {
                        int typeIndex = typeUtils.typeExchange(variableElement.asType());
                        // Base type
                        if (typeIndex >= 0 && typeIndex <= 7) {
                            methodSpecBuild.addStatement("$T $N = $N", variableElement.asType(),
                                    paramName,
                                    paramsMap.get(paramName));
                        }
                        // String type
                        else if (typeIndex == 8) {
                            String value = paramsMap.get(paramName);
                            if (value.startsWith("'") && value.endsWith("'")) {
                                value = value.substring(1, value.length() - 1);
                            }
                            methodSpecBuild.addStatement("$T $N = $S", variableElement.asType(),
                                    paramName,
                                    value);
                        }
                        // 对象 Object type
                        else if (typeIndex > 8) {
                            if (objParamsMap.containsKey(paramName)) {
                                methodSpecBuild.addStatement("$T $N = new $T()", variableElement.asType(),
                                        paramName,
                                        variableElement.asType());

                                List<String> objParamsList = objParamsMap.get(paramName);

                                for (String inputParamName : objParamsList) {
                                    String[] kv = inputParamName.split("\\.");

                                    if (kv.length == 2) {
                                        String value = paramsMap.get(inputParamName);
                                        if (value.startsWith("'") && value.endsWith("'")) {
                                            value = value.substring(1, value.length() - 1);
                                            methodSpecBuild.addStatement("$N.$N = $S",
                                                    kv[0], kv[1],
                                                    value);
                                        } else {
                                            methodSpecBuild.addStatement("$N.$N = $N",
                                                    kv[0], kv[1],
                                                    value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    paramesKey.append("$N,");
                    paramesValue[i++] = paramName;
                }

                buildAssert(element, methodSpecBuild, unitTest, paramesKey, paramesValue);
            }

        }

        return methodSpecBuild;
    }

    /**
     * 构建断言
     *
     * @param element
     * @param methodSpecBuild
     * @param unitTest
     * @param paramesKey
     * @param paramesValue
     */
    private void buildAssert(ExecutableElement element, MethodSpec.Builder methodSpecBuild, UnitTest unitTest, StringBuilder paramesKey, Object[] paramesValue) {
        if ("void".equals(element.getReturnType().toString().toLowerCase())) {
            methodSpecBuild.addStatement(String.format("%s.$N(%s)",
                    paramesKey.toString().substring(0, paramesKey.length() - 1)), paramesValue);
        } else {
            methodSpecBuild.addStatement(String.format("$T %s = %s.$N(%s)", RESULT, INSTANCE,
                    paramesKey.toString().substring(0, paramesKey.length() - 1)), paramesValue);

            if (!StringUtils.isEmpty(unitTest.assertResult())) {
                int returnIndex = typeUtils.typeExchange(element.getReturnType());
                String assertResult = unitTest.assertResult().replaceAll(" ", "");
                if (element.getReturnType().getKind().isPrimitive() || (returnIndex >= 0 && returnIndex <= 7)) {
                    methodSpecBuild.addStatement("$T.assertTrue($N==$N)", Assert.class, RESULT, assertResult.substring(assertResult.indexOf("=") + 1));
                } else if (returnIndex == 8) {
                    methodSpecBuild.addStatement("$T.assertEquals($N, $S)", Assert.class, RESULT, assertResult.substring(assertResult.indexOf("=") + 1));
                } else {
                    String[] results = assertResult.split(",");
                    for (String resultStr : results) {
                        if (resultStr.startsWith("#.")) {
                            String[] tempKv = resultStr.split("=");

                            if (tempKv[1].startsWith("'") && tempKv[1].endsWith("'")) {
                                tempKv[1] = tempKv[1].substring(1, tempKv[1].length() - 1);
                                methodSpecBuild.addStatement("$T.assertEquals($N.$N, $S)", Assert.class, RESULT, tempKv[0].substring(2), tempKv[1]);
                            } else {
                                methodSpecBuild.addStatement("$T.assertTrue($N.$N==$N)", Assert.class, RESULT, tempKv[0].substring(2), tempKv[1]);
                            }
                        }
                    }
                }
            }
        }
    }

    private File getTestDirPath(Element classElement) throws IOException {
        JavaFileObject javaFileObject = mFiler.createSourceFile(classElement.toString());
        String path = javaFileObject.toUri().getPath();
        path = path.substring(0, path.indexOf("build/")) + "src/test/java";
        return new File(path);
    }

    private TypeSpec createTestType(Element typeElement, Iterable<MethodSpec> methodSpecs) {
        FieldSpec instanceFieldSpec = FieldSpec.builder(TypeName.get(typeElement.asType()), INSTANCE).addModifiers(PRIVATE).build();
        MethodSpec.Builder methodBeforeBuild = MethodSpec.methodBuilder(TEST_SET_UP)
                .addAnnotation(Before.class)
                .addModifiers(PUBLIC)
                .addException(Exception.class);

        List<Element> typeElements = (List<Element>) typeElement.getEnclosedElements();
        for(Element typeElem : typeElements){
            if(typeElem instanceof ExecutableElement && "<init>".equals(typeElem.getSimpleName().toString())){
                ExecutableElement initM = (ExecutableElement) typeElem;
                if(initM.getParameters() != null && initM.getParameters().size() > 0){
                    continue;
                }
                if(initM.getModifiers().iterator().hasNext()){
                    Modifier modifier = initM.getModifiers().iterator().next();
                    if(modifier != null && PUBLIC.ordinal() == modifier.ordinal()){
                        methodBeforeBuild.addStatement(INSTANCE + " = new $T()", typeElement.asType());
                    } else {
                        methodBeforeBuild.addStatement("$T clazz = $T.class", Class.class, typeElement.asType());
                        methodBeforeBuild.addStatement("$T cons = clazz.getDeclaredConstructor(null)", Constructor.class);
                        methodBeforeBuild.addStatement("cons.setAccessible(true)");
                        methodBeforeBuild.addStatement(INSTANCE + " = ($T)cons.newInstance(null)", typeElement.asType());
                    }
                }
            }
        }
        MethodSpec methodBefore = methodBeforeBuild.build();

        MethodSpec methodAfter = MethodSpec.methodBuilder(TEST_TEAR_DOWN)
                .addAnnotation(After.class)
                .addModifiers(PUBLIC)
                .addException(Exception.class)
                .addStatement(INSTANCE + " = null")
                .build();

        TypeSpec executingTypeSpec = TypeSpec.classBuilder(typeElement.getSimpleName() + "Test")
                .addModifiers(PUBLIC)
                .addMethod(methodBefore)
                .addMethod(methodAfter)
                .addMethods(methodSpecs)
                .addField(instanceFieldSpec).build();
        return executingTypeSpec;
    }
}
