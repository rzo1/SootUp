/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package de.upb.soot.jimple.javabytecode.stmt;

import de.upb.soot.jimple.Jimple;
import de.upb.soot.jimple.basic.IStmtBox;
import de.upb.soot.jimple.basic.Value;
import de.upb.soot.jimple.basic.ValueBox;
import de.upb.soot.jimple.common.expr.AbstractInvokeExpr;
import de.upb.soot.jimple.common.ref.FieldRef;
import de.upb.soot.jimple.common.ref.JArrayRef;
import de.upb.soot.jimple.common.stmt.AbstractOpStmt;
import de.upb.soot.jimple.common.stmt.IStmt;
import de.upb.soot.jimple.visitor.IStmtVisitor;
import de.upb.soot.jimple.visitor.IVisitor;
import de.upb.soot.util.printer.IStmtPrinter;

import java.util.List;

public class JExitMonitorStmt extends AbstractOpStmt {
  /**
   * 
   */
  private static final long serialVersionUID = -1179706103954735007L;

  public JExitMonitorStmt(Value op) {
    this(Jimple.newImmediateBox(op));
  }

  protected JExitMonitorStmt(ValueBox opBox) {
    super(opBox);
  }

  @Override
  public JExitMonitorStmt clone() {
    return new JExitMonitorStmt(Jimple.cloneIfNecessary(getOp()));
  }

  @Override
  public String toString() {
    return Jimple.EXITMONITOR + " " + opBox.getValue().toString();
  }

  @Override
  public void toString(IStmtPrinter up) {
    up.literal(Jimple.EXITMONITOR);
    up.literal(" ");
    opBox.toString(up);
  }

  @Override
  public void accept(IVisitor sw) {
    ((IStmtVisitor) sw).caseExitMonitorStmt(this);

  }

  @Override
  public boolean fallsThrough() {
    return true;
  }

  @Override
  public boolean branches() {
    return false;
  }

  @Override
  public AbstractInvokeExpr getInvokeExpr() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ValueBox getInvokeExprBox() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public JArrayRef getArrayRef() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ValueBox getArrayRefBox() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FieldRef getFieldRef() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ValueBox getFieldRefBox() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ValueBox> getDefBoxes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<IStmtBox> getStmtBoxes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<IStmtBox> getBoxesPointingToThis() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addBoxPointingToThis(IStmtBox b) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeBoxPointingToThis(IStmtBox b) {
    // TODO Auto-generated method stub

  }

  @Override
  public void clearStmtBoxes() {
    // TODO Auto-generated method stub

  }

  @Override
  public List<ValueBox> getUseAndDefBoxes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void redirectJumpsToThisTo(IStmt newLocation) {
    // TODO Auto-generated method stub

  }

}