package sootup.tests.bugs;

import categories.Java8Test;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import sootup.core.graph.StmtGraph;
import sootup.core.model.SourceType;
import sootup.core.signatures.MethodSignature;
import sootup.core.util.DotExporter;
import sootup.java.bytecode.inputlocation.BytecodeClassLoadingOptions;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaProject;
import sootup.java.core.language.JavaLanguage;
import sootup.java.core.views.JavaView;

@Category(Java8Test.class)
public class Issue664Test {
  @Test
  public void testJar_missing_if_flows() {

    JavaProject applicationProject =
        JavaProject.builder(new JavaLanguage(8))
            .addInputLocation(
                new JavaClassPathAnalysisInputLocation(
                    "src/test/resources/bugs/664_struce-compiled/", SourceType.Application))
            .build();

    JavaView view = applicationProject.createMutableView();
    view.configBodyInterceptors(analysisInputLocation -> BytecodeClassLoadingOptions.Default);

    final MethodSignature methodSignature =
        view.getIdentifierFactory()
            .parseMethodSignature(
                "<org.apache.jsp.java_detection_samples: void _jspService(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)>");
    final StmtGraph<?> stmtGraph = view.getMethod(methodSignature).get().getBody().getStmtGraph();
    System.out.println(DotExporter.createUrlToWebeditor(stmtGraph));

    // TODO: Fix LocalSplitter - generates the precondition that the DeadAssignmentEliminator wants
    // to remove Stmts.. i.e. it does not connect all Locals or does not remove/replace the
    // previously used Locals?
    // TODO: Fix DeadAssignmentEliminator (removes a branchtarget from IfStmt which results in an
    // invalid StmtGrph)

  }
}
