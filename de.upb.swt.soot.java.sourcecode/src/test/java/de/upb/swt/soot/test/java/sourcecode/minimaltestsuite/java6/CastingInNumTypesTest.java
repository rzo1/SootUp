package de.upb.swt.soot.test.java.sourcecode.minimaltestsuite.java6;

import de.upb.swt.soot.core.model.SootMethod;
import de.upb.swt.soot.core.signatures.MethodSignature;
import de.upb.swt.soot.test.java.sourcecode.minimaltestsuite.MinimalSourceTestSuiteBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class CastingInNumTypesTest extends MinimalSourceTestSuiteBase {

  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        "displayNum", getDeclaredClassSignature(), "void", Collections.emptyList());
  }

  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of(
            "r0 := @this: CastingInNumTypes",
            "$i0 = 1",
            "$i1 = 2",
            "$i2 = 3",
            "$l0 = 4551598461L",
            "$f0 = 5.4F",
            "$d0 = 4.5515954846546467E39",
            "$r1 = <java.lang.System: java.io.PrintStream out>",
            "virtualinvoke $r1.<java.io.PrintStream: void println(int)>($i0)",
            "$r2 = <java.lang.System: java.io.PrintStream out>",
            "$b0 = (byte) $i2",
            "virtualinvoke $r2.<java.io.PrintStream: void println(int)>($b0)",
            "$r3 = <java.lang.System: java.io.PrintStream out>",
            "$d1 = (double) $i1",
            "virtualinvoke $r3.<java.io.PrintStream: void println(double)>($d1)",
            "$r4 = <java.lang.System: java.io.PrintStream out>",
            "$s0 = (short) $l0",
            "virtualinvoke $r4.<java.io.PrintStream: void println(int)>($s0)",
            "$r5 = <java.lang.System: java.io.PrintStream out>",
            "$d2 = (double) $f0",
            "virtualinvoke $r5.<java.io.PrintStream: void println(double)>($d2)",
            "$r6 = <java.lang.System: java.io.PrintStream out>",
            "$i3 = (int) $l0",
            "virtualinvoke $r6.<java.io.PrintStream: void println(int)>($i3)",
            "$r7 = <java.lang.System: java.io.PrintStream out>",
            "$f1 = (float) $d0",
            "virtualinvoke $r7.<java.io.PrintStream: void println(float)>($f1)",
            "$r8 = <java.lang.System: java.io.PrintStream out>",
            "virtualinvoke $r8.<java.io.PrintStream: void println(double)>($d0)",
            "$d3 = 4.7867778678678685E51",
            "$r9 = <java.lang.System: java.io.PrintStream out>",
            "$f2 = (float) $d3",
            "virtualinvoke $r9.<java.io.PrintStream: void println(float)>($f2)",
            "$r10 = <java.lang.System: java.io.PrintStream out>",
            "$l1 = (long) $d3",
            "virtualinvoke $r10.<java.io.PrintStream: void println(long)>($l1)",
            "$r11 = <java.lang.System: java.io.PrintStream out>",
            "$i4 = (int) $d3",
            "virtualinvoke $r11.<java.io.PrintStream: void println(int)>($i4)",
            "$r12 = <java.lang.System: java.io.PrintStream out>",
            "$s1 = (short) $d3",
            "virtualinvoke $r12.<java.io.PrintStream: void println(int)>($s1)",
            "$r13 = <java.lang.System: java.io.PrintStream out>",
            "$b1 = (byte) $d3",
            "virtualinvoke $r13.<java.io.PrintStream: void println(int)>($b1)",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Test
  public void defaultTest() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
  }
}
