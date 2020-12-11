package de.upb.swt.soot.jimple.parser.javatestsuite.java6;

import static org.junit.Assert.assertTrue;

import de.upb.swt.soot.core.model.Modifier;
import de.upb.swt.soot.core.model.SootClass;
import de.upb.swt.soot.core.model.SootMethod;
import de.upb.swt.soot.core.signatures.MethodSignature;
import de.upb.swt.soot.jimple.parser.categories.Java8Test;
import de.upb.swt.soot.jimple.parser.javatestsuite.JimpleTestSuiteBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/** @author Kaustubh Kelkar */
@Category(Java8Test.class)
public class TransientVariableTest extends JimpleTestSuiteBase {
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        "transientVariable", getDeclaredClassSignature(), "void", Collections.emptyList());
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
    SootClass clazz = loadClass(getDeclaredClassSignature());
    assertTrue(
        clazz.getFields().stream()
            .anyMatch(
                sootField ->
                    sootField.getName().equals("transientVar")
                        && sootField.getModifiers().contains(Modifier.TRANSIENT)));
  }

  public List<String> expectedBodyStmts() {
    return Stream.of(
            "l0 := @this: TransientVariable",
            "$stack2 = <java.lang.System: java.io.PrintStream out>",
            "$stack1 = l0.<TransientVariable: int transientVar>",
            "virtualinvoke $stack2.<java.io.PrintStream: void println(int)>($stack1)",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }
}