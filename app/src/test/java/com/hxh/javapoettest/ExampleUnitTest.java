package com.hxh.javapoettest;

import android.os.Message;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Example
    ///////////////////////////////////////////////////////////////////////////
    private void generateClassAndFile(MethodSpec main, String className, String packageName) throws IOException {
        TypeSpec helloWorld = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    @Test
    public void test1() throws Exception {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        generateClassAndFile(main, "HelloWorld1", "com.example.helloworld1");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Code & Control Flow
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test2() throws Exception {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addCode(""
                        + "int total = 0;\n"
                        + "for (int i = 0; i < 10; i++) {\n"
                        + "  total += i;\n"
                        + "}\n")
                .build();

        generateClassAndFile(main, "HelloWorld", "com.example.helloworld");
    }

    @Test
    public void test3() throws Exception {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addStatement("int total = 0")
                .beginControlFlow("for (int i = 0; i < 10; i++)")
                .addStatement("total += i")
                .endControlFlow()
                .build();

        generateClassAndFile(main, "HelloWorld", "com.example.helloworld");
    }

    private MethodSpec computeRange(String name, int from, int to, String op) {
        return MethodSpec.methodBuilder(name)
                .returns(int.class)
                .addStatement("int result = 1")
                .beginControlFlow("for (int i = " + from + "; i < " + to + "; i++)")
                .addStatement("result = result " + op + " i")
                .endControlFlow()
                .addStatement("return result")
                .build();
    }

    @Test
    public void test4() throws Exception {
        MethodSpec main = computeRange("add", 2, 12, "+");
        generateClassAndFile(main, "HelloWorld", "com.example.helloworld");
        MethodSpec main2 = computeRange("multi", 3, 13, "*");
        generateClassAndFile(main2, "HelloWorld2", "com.example.helloworld2");
    }

    @Test
    public void test5() throws Exception {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addStatement("long now = $T.currentTimeMillis()", System.class)
                .beginControlFlow("if ($T.currentTimeMillis() < now)", System.class)
                .addStatement("$T.out.println($S)", System.class, "Time travelling, woo hoo!")
                .nextControlFlow("else if ($T.currentTimeMillis() == now)", System.class)
                .addStatement("$T.out.println($S)", System.class, "Time stood still!")
                .nextControlFlow("else")
                .addStatement("$T.out.println($S)", System.class, "Ok, time still moving forward")
                .endControlFlow()
                .build();
        generateClassAndFile(main, "HelloWorld", "com.example.helloworld");
    }

    @Test
    public void test6() throws Exception {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .beginControlFlow("try")
                .addStatement("throw new Exception($S)", "Failed")
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("throw new $T(e)", RuntimeException.class)
                .endControlFlow()
                .build();
        generateClassAndFile(main, "HelloWorld", "com.example.helloworld");
    }

    ///////////////////////////////////////////////////////////////////////////
    // $L for Literals
    ///////////////////////////////////////////////////////////////////////////
    private MethodSpec computeRange2(String name, int from, int to, String op) {
        return MethodSpec.methodBuilder(name)
                .returns(int.class)
                .addStatement("int result = 0")
                .beginControlFlow("for (int i = $L; i < $L; i++)", from, to)
                .addStatement("result = result $L i", op)
                .endControlFlow()
                .addStatement("return result")
                .build();
    }

    @Test
    public void test7() throws Exception {
        MethodSpec main = computeRange2("reduce", 1, 11, "-");
        generateClassAndFile(main, "HelloWorld", "com.example.helloworld");
        MethodSpec main2 = computeRange2("divide", 12, 121, "/");
        generateClassAndFile(main2, "HelloWorld", "com.example.helloworld");
    }

    ///////////////////////////////////////////////////////////////////////////
    // $S for Strings
    ///////////////////////////////////////////////////////////////////////////
    private static MethodSpec whatsMyName(String name) {
        return MethodSpec.methodBuilder(name)
                .returns(String.class)
                .addStatement("return $S", name)
                .build();
    }

    @Test
    public void test8() throws Exception {
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(whatsMyName("slimShady"))
                .addMethod(whatsMyName("eminem"))
                .addMethod(whatsMyName("marshallMathers"))
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    ///////////////////////////////////////////////////////////////////////////
    // $T for Types
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test9() throws Exception {
        MethodSpec today = MethodSpec.methodBuilder("today")
                .returns(Date.class)
                .addStatement("return new $T()", Date.class)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(today)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();

        javaFile.writeTo(System.out);
    }

    @Test
    public void test10() throws Exception {
        ClassName hoverboard = ClassName.get("com.mattel", "Hoverboard");
        MethodSpec today = MethodSpec.methodBuilder("tomorrow")
                .returns(hoverboard)
                .addStatement("return new $T()", hoverboard)
                .build();
        generateClassAndFile(today, "HelloWorld", "com.example.helloworld");
    }

    @Test
    public void test11() throws Exception {
        ClassName hoverboard = ClassName.get("com.mattel", "Hoverboard");
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfHoverboards = ParameterizedTypeName.get(list, hoverboard);

        MethodSpec beyond = MethodSpec.methodBuilder("beyond")
                .returns(listOfHoverboards)
                .addStatement("$T result = new $T<>()", listOfHoverboards, arrayList)
                .addStatement("result.add(new $T())", hoverboard)
                .addStatement("result.add(new $T())", hoverboard)
                .addStatement("result.add(new $T())", hoverboard)
                .addStatement("return result")
                .build();
        generateClassAndFile(beyond, "HelloWorld", "com.example.helloworld");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Import static
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test12() throws Exception {
        ClassName hoverboard = ClassName.get("com.mattel", "Hoverboard");
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfHoverboards = ParameterizedTypeName.get(list, hoverboard);

        ClassName namedBoards = ClassName.get("com.mattel", "Hoverboard", "Boards");

        MethodSpec beyond = MethodSpec.methodBuilder("beyond")
                .returns(listOfHoverboards)
                .addStatement("$T result = new $T<>()", listOfHoverboards, arrayList)
                .addStatement("result.add($T.createNimbus(2000))", hoverboard)
                .addStatement("result.add($T.createNimbus(\"2001\"))", hoverboard)
                .addStatement("result.add($T.createNimbus($T.THUNDERBOLT))", hoverboard, namedBoards)
                .addStatement("$T.sort(result)", Collections.class)
                .addStatement("return result.isEmpty() ? $T.emptyList() : result", Collections.class)
                .build();

        TypeSpec hello = TypeSpec.classBuilder("HelloWorld")
                .addMethod(beyond)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", hello)
                .addStaticImport(namedBoards, "*")
                .addStaticImport(hoverboard, "createNimbus")
                .addStaticImport(Collections.class, "*")
                .build();
        javaFile.writeTo(System.out);
    }

    ///////////////////////////////////////////////////////////////////////////
    // $N for Names
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test13() throws Exception {
        MethodSpec hexDigit = MethodSpec.methodBuilder("hexDigit")
                .addParameter(int.class, "i")
                .returns(char.class)
                .addStatement("return (char) (i < 10 ? i + '0' : i - 10 + 'a')")
                .build();

        MethodSpec byteToHex = MethodSpec.methodBuilder("byteToHex")
                .addParameter(int.class, "b")
                .returns(String.class)
                .addStatement("char[] result = new char[2]")
                .addStatement("result[0] = $N((b >>> 4) & 0xf)", hexDigit)
                .addStatement("result[1] = $N(b & 0xf)", hexDigit)
                .addStatement("return new String(result)")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("Hello")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(hexDigit)
                .addMethod(byteToHex)
                .build();
        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Code block format strings
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test14() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("food", "tacos");
        map.put("count", 3);

        MethodSpec flux = MethodSpec.methodBuilder("flux")
                .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                .build();

        MethodSpec method = MethodSpec.methodBuilder("test")
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addCode(CodeBlock.builder()
                        .add("return \"")
                        .addNamed("I ate $count:L $food:L", map)
                        .add(" I ate $L $L", 2, "fans")
                        .add(" I ate $2L $1L", "noddles", 3)
                        .add("\"")
                        .build())
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addMethod(flux)
                .addMethod(method)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    // Parameters
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test15() throws Exception {
        MethodSpec flux = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "greeting")
                .addStatement("this.$N = $N", "greeting", "greeting")
                .build();

        ParameterSpec android = ParameterSpec.builder(String.class, "android")
                .addModifiers(Modifier.FINAL)
                .build();

        MethodSpec welcomeOverlords = MethodSpec.methodBuilder("welcomeOverlords")
                .addParameter(android)
                .addParameter(String.class, "robot", Modifier.FINAL)
                .addStatement("$T.out.println(\"param1 name is : $L\")", System.class, android.name)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addField(String.class, "greeting", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(flux)
                .addMethod(welcomeOverlords)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fields
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test16() throws Exception {
        FieldSpec android = FieldSpec.builder(String.class, "android")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        FieldSpec android2 = FieldSpec.builder(String.class, "android2")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("$S + $L", "Lollipop v.", 5.0d)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addField(android)
                .addField(String.class, "robot", Modifier.PRIVATE, Modifier.FINAL)
                .addField(android2)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interfaces
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test17() throws Exception {
        ClassName name = ClassName.get("com.hxh", "Person");
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName typeList = ParameterizedTypeName.get(list, name);

        TypeSpec helloWorld = TypeSpec.interfaceBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(String.class, "ONLY_THING_THAT_IS_CONSTANT")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", "change")
                        .build())
                .addField(FieldSpec.builder(name, "PERSON")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", name)
                        .build())
                .addField(FieldSpec.builder(typeList, "PERSON_LIST")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", arrayList)
                        .build())
                .addMethod(MethodSpec.methodBuilder("beep")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Enums
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test18() throws Exception {
        TypeSpec helloWorld = TypeSpec.enumBuilder("Roshambo")
                .addModifiers(Modifier.PUBLIC)
                .addEnumConstant("ROCK")
                .addEnumConstant("SCISSORS")
                .addEnumConstant("PAPER")
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }

    @Test
    public void test19() throws Exception {
        TypeSpec helloWorld = TypeSpec.enumBuilder("Roshambo")
                .addModifiers(Modifier.PUBLIC)
                .addEnumConstant("ROCK", TypeSpec.anonymousClassBuilder("$S", "fist")
                        .addMethod(MethodSpec.methodBuilder("toString")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addStatement("return $S", "avalanche!")
                                .returns(String.class)
                                .build())
                        .build())
                .addEnumConstant("SCISSORS", TypeSpec.anonymousClassBuilder("$S", "peace")
                        .build())
                .addEnumConstant("PAPER", TypeSpec.anonymousClassBuilder("$S", "flat")
                        .build())
                .addField(String.class, "handsign", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "handsign")
                        .addStatement("this.$N = $N", "handsign", "handsign")
                        .build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Anonymous Inner Classes
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test20() throws Exception {
        TypeSpec comparator = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(Comparator.class, String.class))
                .addMethod(MethodSpec.methodBuilder("compare")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(String.class, "a")
                        .addParameter(String.class, "b")
                        .returns(int.class)
                        .addStatement("return $N.length() - $N.length()", "a", "b")
                        .build())
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PRIVATE)
                .addMethod(MethodSpec.methodBuilder("sortByLength")
                        .addParameter(ParameterizedTypeName.get(List.class, String.class).annotated(), "strings")
                        .addStatement("$T.sort($N, $L)", Collections.class, "strings", comparator)
                        .build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Annotations
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test21() throws Exception {
        MethodSpec toString = MethodSpec.methodBuilder("toString")
                .addAnnotation(Override.class)
                .returns(String.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $S", "Hoverboard")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addMethod(toString)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }

    @Test
    public void test22() throws Exception {
        class Headers {

        }
        class LogReceipt {

        }
        class LogRecord {

        }
        class HeaderList {

        }
        MethodSpec logRecord = MethodSpec.methodBuilder("recordEvent")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(AnnotationSpec.builder(Headers.class)
                        .addMember("accept", "$S", "application/json; charset=utf-8")
                        .addMember("userAgent", "$S", "Square Cash")
                        .build())
                .addParameter(LogRecord.class, "logRecord")
                .returns(LogReceipt.class)
                .build();

        MethodSpec logRecord2 = MethodSpec.methodBuilder("recordEvent2")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(AnnotationSpec.builder(HeaderList.class)
                        .addMember("value", "$L", AnnotationSpec.builder(Headers.class)
                                .addMember("name", "$S", "Accept")
                                .addMember("value", "$S", "application/json; charset=utf-8")
                                .build())
                        .addMember("value2", "$L", AnnotationSpec.builder(Headers.class)
                                .addMember("name", "$S", "User-Agent")
                                .addMember("value", "$S", "Square Cash")
                                .build())
                        .build())
                .addParameter(LogRecord.class, "logRecord")
                .returns(LogReceipt.class)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addMethod(logRecord)
                .addMethod(logRecord2)
                .addModifiers(Modifier.ABSTRACT)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Javadoc
    ///////////////////////////////////////////////////////////////////////////
    @Test
    public void test23() throws Exception {
        class Conversation{

        }
        MethodSpec dismiss = MethodSpec.methodBuilder("dismiss")
                .addJavadoc("Hides {@code message} from the caller's history. Other\n"
                        + "participants in the conversation will continue to see the\n"
                        + "message in their own history unless they also delete it.\n")
                .addJavadoc("\n")
                .addJavadoc("<p>Use {@link #delete($T)} to delete the entire\n"
                        + "conversation for all participants.\n", Conversation.class)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(Message.class, "message")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addMethod(dismiss)
                .addModifiers(Modifier.ABSTRACT)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        javaFile.writeTo(System.out);
    }
}