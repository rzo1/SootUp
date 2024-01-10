package sootup.java.bytecode.interceptors;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997-2020 Raja Vallée-Rai, Christian Brüggemann
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.*;
import javax.annotation.Nonnull;

import sootup.core.graph.*;
import sootup.core.jimple.basic.LValue;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.Body;
import sootup.core.transform.BodyInterceptor;
import sootup.core.views.View;

/**
 * A BodyInterceptor that attempts to identify and separate uses of a local variable (definition)
 * that are independent of each other.
 *
 * <p>For example the code:
 *
 * <pre>
 *    l0 := @this Test
 *    l1 = 0
 *    l2 = 1
 *    l1 = l1 + 1
 *    l2 = l2 + 1
 *    return
 * </pre>
 * <p>
 * to:
 *
 * <pre>
 *    l0 := @this Test
 *    l1#1 = 0
 *    l2#2 = 1
 *    l1#3 = l1#1 + 1
 *    l2#4 = l2#2 + 1
 *    return
 * </pre>
 *
 */
public class LocalSplitter implements BodyInterceptor {

    private DominanceFinder dominanceFinder;
    private Map<Integer, Map<Local, Integer>> mostRecentDefBlock;
    private Map<Stmt, Stmt> originalToNewStmt;

    @Override
    public void interceptBody(@Nonnull Body.BodyBuilder builder, @Nonnull View<?> view) {
        Set<Local> localsToSplit = findLocalsToSplit(builder);
        init(localsToSplit, builder);
        for (Stmt stmt : builder.getStmtGraph()) {

            handleDefs(builder, localsToSplit, stmt);

            handleUses(builder, localsToSplit, stmt);

        }
    }

    private void init(Set<Local> localsToSplit, Body.BodyBuilder builder){
        originalToNewStmt = new HashMap<>();
        dominanceFinder = new DominanceFinder(builder.getStmtGraph());
        mostRecentDefBlock = new HashMap<>();
        Map<BasicBlock<?>, Integer> blockToIdx = dominanceFinder.getBlockToIdx();
        for (Integer val : blockToIdx.values()) {
            Map<Local, Integer> localToDefId = new HashMap<>();
            for (Local local : localsToSplit) {
                localToDefId.put(local, 0); // init to zero
            }
            mostRecentDefBlock.put(val, localToDefId);
        }
    }

    private int getMostRecentDefInPredBlock(Local local, StmtGraph stmtGraph, Stmt stmt){
        BasicBlock currentBlock = stmtGraph.getBlockOf(stmt);
        List<BasicBlock> predBlocks = currentBlock.getPredecessors();
        if(!predBlocks.isEmpty()){
            BasicBlock predBlock = predBlocks.get(0);
            Integer idx = dominanceFinder.getBlockToIdx().get(predBlock);
            return mostRecentDefBlock.get(idx).get(local);
        }
        Integer currentBlockId = dominanceFinder.getBlockToIdx().get(currentBlock);
        return mostRecentDefBlock.get(currentBlockId).get(local); // entry block
    }

    private void putMostRecentDefInBlock(int id, Local local, StmtGraph stmtGraph, Stmt stmt){
        BasicBlock block = stmtGraph.getBlockOf(stmt);
        Integer blockId = dominanceFinder.getBlockToIdx().get(block);
        Map<Local, Integer> LocalToDefIdInBlock = mostRecentDefBlock.get(blockId);
        if(LocalToDefIdInBlock!=null){
            LocalToDefIdInBlock.put(local, id);
        }else{
            LocalToDefIdInBlock = new HashMap<>();
            LocalToDefIdInBlock.put(local, id);
        }
    }

    private void handleUses(Body.BodyBuilder builder, Set<Local> localsToSplit, Stmt stmt) {
        for (Value use : stmt.getUses()) {
            if (!(use instanceof Local)) {
                continue;
            }
            Local oldLocalUse = (Local) use;
            if (localsToSplit.contains(use)) {
                Stmt toReplace;
                if (originalToNewStmt.containsKey(stmt)) {
                    toReplace = originalToNewStmt.get(stmt);
                } else {
                    toReplace = stmt;
                }
                int mostRecentDefId = getMostRecentDefInPredBlock(oldLocalUse, builder.getStmtGraph(), toReplace);
                Local newLocal = oldLocalUse.withName(oldLocalUse.getName() + '#' + mostRecentDefId); // use the most recent split name
                Stmt withNewUse = toReplace.withNewUse(oldLocalUse, newLocal);
                //Stmt withNewUse = new JAssignStmt(toReplace.getDefs().get(0), newLocal, stmt.getPositionInfo());
                builder.getStmtGraph().replaceNode(toReplace, withNewUse);
                originalToNewStmt.put(stmt, withNewUse);
            }
        }
    }

    private void handleDefs(Body.BodyBuilder builder, Set<Local> localsToSplit, Stmt stmt) {
        List<LValue> defs = stmt.getDefs();
        if(defs.size()==0){
            return;
        }
        if (defs.size()==1) {
            LValue def = defs.get(0);
            if (def instanceof Local) {
                Local oldLocal = (Local) def;
                if (localsToSplit.contains(oldLocal)) {
                    Stmt toReplace;
                    if (originalToNewStmt.containsKey(stmt)) {
                        toReplace = originalToNewStmt.get(stmt);
                    } else {
                        toReplace = stmt;
                    }
                    int id = getMostRecentDefInPredBlock(oldLocal, builder.getStmtGraph(), toReplace);
                    handleSelfUse(builder, stmt, oldLocal);
                    Local newLocal = oldLocal.withName(oldLocal.getName() + '#' + (++id)); // renaming should not be done here
                    putMostRecentDefInBlock(id, oldLocal, builder.getStmtGraph(), stmt);
                    builder.addLocal(newLocal);

                    Stmt withNewDef = new JAssignStmt(newLocal, toReplace.getUses().get(0), stmt.getPositionInfo());
                    builder.getStmtGraph().replaceNode(toReplace, withNewDef);
                    originalToNewStmt.put(stmt, withNewDef);
                }
            }
        } else{
            throw new RuntimeException("stmt with more than 1 def!");
        }
    }

    private void handleSelfUse(Body.BodyBuilder builder, Stmt stmt, Local def) {
        for (Value use : stmt.getUses()) {
            if(use.equals(def)){
                Local oldLocalUse = (Local) use;
                Stmt toReplace;
                if (originalToNewStmt.containsKey(stmt)) {
                    toReplace = originalToNewStmt.get(stmt);
                } else {
                    toReplace = stmt;
                }
                int id = getMostRecentDefInPredBlock(oldLocalUse, builder.getStmtGraph(), toReplace);
                Local newLocal = oldLocalUse.withName(oldLocalUse.getName() + '#' + id); // use the most recent split name
                Stmt withNewUse = stmt.withNewUse(oldLocalUse, newLocal);
                builder.getStmtGraph().replaceNode(toReplace, withNewUse);
                originalToNewStmt.put(stmt, withNewUse);
            }
        }
    }

    /**
     * Multiple defs of the same local are to split.
     * @param builder
     * @return
     */
    private Set<Local> findLocalsToSplit(Body.BodyBuilder builder) {
        Set<Local> visitedLocals = new LinkedHashSet<>();
        Set<Local> localsToSplit = new LinkedHashSet<>();
        for (Stmt stmt : builder.getStmts()) {
            for (LValue def : stmt.getDefs()) {
                if (def instanceof Local) {
                    if (visitedLocals.contains(def)) {
                        localsToSplit.add((Local) def);
                    } else {
                        visitedLocals.add((Local) def);
                    }
                }
            }
        }
        return localsToSplit;
    }


}
