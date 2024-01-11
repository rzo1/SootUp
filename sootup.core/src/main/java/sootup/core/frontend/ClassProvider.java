package sootup.core.frontend;
/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004-2020 Ondrej Lhotak, Linghui Luo and others
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

import java.nio.file.Path;
import java.util.Optional;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.inputlocation.FileType;
import sootup.core.types.ClassType;

/**
 * Responsible for creating {@link AbstractClassSource}es based on the handled file type (.class,
 * .jimple, .java, .dex, etc).
 *
 * @author Manuel Benz
 */
public interface ClassProvider {

  Optional<? extends SootClassSource> createClassSource(
      AnalysisInputLocation inputLocation, Path sourcePath, ClassType classSignature);

  /** Returns the file type that is handled by this provider, e.g. class, jimple, java */
  FileType getHandledFileType();
}