package de.upb.soot.frontends.java;

import static org.junit.Assert.assertEquals;

import com.ibm.wala.util.collections.Pair;
import de.upb.soot.core.SootClass;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;

/**
 * This test loads application source code with wala, library code with old soot and build callgraph with old soot.
 *
 * @author Linghui Luo
 */
public class WalaClassLoaderTest {
  @Test
  public void testAndroidProject() {
    G.v().reset();
    Options.v().set_whole_program(true);
    Options.v().setPhaseOption("cg.cha", "on");
    Options.v().setPhaseOption("cg", "all-reachable:true");

    String projectDir = new File("src/test/resources/android-target/ActivityLifecycle1").getAbsolutePath();
    Set<String> sourcePath = new HashSet<>();
    sourcePath.add(projectDir + File.separator + "src");
    sourcePath.add(projectDir + File.separator + "gen");
    
    Set<String> libPath = new HashSet<>();
    File libDir = new File(projectDir + File.separator + "libs");
    for (File file : libDir.listFiles()) {
      if (file.getName().endsWith(".jar")) {
        libPath.add(file.getAbsolutePath());
      }
    }

    String androidJarPath = new File("src/test/resources/android-target/platforms/android-17/android.jar").getAbsolutePath();

    // explicitly include packages for shorter runtime:
    List<String> excludeList = new LinkedList<String>();
    excludeList.add("java.*");
    excludeList.add("sun.*");
    excludeList.add("android.*");
    excludeList.add("org.apache.*");
    excludeList.add("org.eclipse.*");
    excludeList.add("soot.*");
    excludeList.add("javax.*");
    Options.v().set_exclude(excludeList);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_no_bodies_for_excluded(true);

    // load library class with old soot
    Options.v().set_process_dir(new ArrayList<String>(libPath));
    Options.v().set_soot_classpath(androidJarPath);
    System.out.println("classpath "+Scene.v().getSootClassPath());
    System.out.println("processdir "+ Options.v().process_dir());
    Scene.v().loadNecessaryClasses();
    
    // load application class with wala
    libPath.add(androidJarPath);
    WalaClassLoader loader = new WalaClassLoader(sourcePath, libPath, null);
    List<SootClass> sootClasses = loader.getSootClasses();
    assertEquals(10, sootClasses.size());

    // convert apllication class to old jimple
    JimpleConverter jimpleConverter = new JimpleConverter(sootClasses);
    jimpleConverter.convertAllClasses();
    System.out.println(Scene.v().getClasses().size());
    assertEquals(10, Scene.v().getApplicationClasses().size());

    PackManager.v().getPack("cg").apply();
    HashSet<Pair<String, String>> futureSoot = saveCallGraph(Scene.v().getCallGraph());
    HashSet<Pair<String, String>> oldSoot = saveCallGraph(getCallGraphFromOldSoot());

    int i = 0;
    for (Pair<String, String> edge : oldSoot) {
      boolean found = false;
      for (Pair<String, String> edgeOld : futureSoot) {
        if (edge.fst.equals(edgeOld.fst) && edge.snd.equals(edgeOld.snd)) {
          found = true;
        }
      }
      if (!found) {
        i++;
      }
    }
    assertEquals(1, i);
    // The following edge does not exist in futureSoot.
    // [<de.ecspride.ActivityLifecycle1: void onCreate(android.os.Bundle)>,<android.app.Activity:
    // void setContentView(int)>]
    // TODO. add more assertions and find out why the above edge does not exist. right now actual
    // call graph has more edges
    // than the call graph generated by old soot.
  }

  private CallGraph getCallGraphFromOldSoot() {
    G.v().reset();
    Options.v().set_whole_program(true);
    Options.v().setPhaseOption("cg.cha", "on");
    Options.v().setPhaseOption("cg", "all-reachable:true");

    // explicitly include packages for shorter runtime:
    List<String> excludeList = new LinkedList<String>();
    excludeList.add("java.*");
    excludeList.add("sun.*");
    excludeList.add("android.*");
    excludeList.add("org.apache.*");
    excludeList.add("org.eclipse.*");
    excludeList.add("soot.*");
    excludeList.add("javax.*");
    Options.v().set_exclude(excludeList);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_no_bodies_for_excluded(true);

    // set soot to process apk
    Options.v().set_src_prec(Options.src_prec_apk);
    String apkPath
        = new File("src/test/resources/android-target/ActivityLifecycle1/ActivityLifecycle1.apk").getAbsolutePath();
    Options.v().set_process_dir(Collections.singletonList(apkPath));

    // set android plaform path
    String androidJar = new File("src/test/resources/android-target/platforms").getAbsolutePath();
    Options.v().set_android_jars(androidJar);
    
    System.err.println("old classpath "+Scene.v().getSootClassPath());
    System.err.println("old processdir "+ Options.v().process_dir());
    Scene.v().loadNecessaryClasses();
    System.err.println(Scene.v().getClasses().size());
    PackManager.v().getPack("cg").apply();
    return Scene.v().getCallGraph();
  }

  private HashSet<Pair<String, String>> saveCallGraph(CallGraph cg) {
    HashSet<Pair<String, String>> ret = new HashSet<>();
    Iterator<soot.jimple.toolkits.callgraph.Edge> it = cg.iterator();
    while (it.hasNext()) {
      soot.jimple.toolkits.callgraph.Edge edge = it.next();
      if (edge.getSrc().toString().startsWith("<de.ecspride")) {
        ret.add(Pair.make(edge.getSrc().toString(), edge.getTgt().toString()));
      }
    }
    return ret;
  }
}