/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.eclemma.internal.core.analysis;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.eclemma.core.JavaProjectKit;

/**
 * Tests for {@link TypeTraverser}.
 *
 * TODO: The implementation s well as the test case is broken. The actual class
 * files generated by Eclipse 3.7 are:
 *
 * <pre>
 * Samples
 * Samples$1
 * Samples$1$InnerB
 * Samples$2
 * Samples$2$InnerC
 * Samples$3
 * Samples$4
 * Samples$InnerA
 * Samples$InnerA$1
 * </pre>
 */
public class TypeTraverserTest {

  private static final IProgressMonitor MONITOR = new NullProgressMonitor();

  private JavaProjectKit javaProject;

  private IPackageFragmentRoot root;

  @Before
  public void setup() throws Exception {
    javaProject = new JavaProjectKit();
    root = javaProject.createSourceFolder("src");
    javaProject.createCompilationUnit(root, "testdata/src",
        "typetraverser/Samples.java");
    JavaProjectKit.waitForBuild();
    javaProject.assertNoErrors();
  }

  @After
  public void teardown() throws Exception {
    javaProject.destroy();
  }

  private static final String[] EXPECTEDTYPES = new String[] {
      "typetraverser/Samples", //
      "typetraverser/Samples$1", //
      "typetraverser/Samples$2$InnerB", //
      "typetraverser/Samples$2", //
      "typetraverser/Samples$3$InnerC", //
      "typetraverser/Samples$3",//
      "typetraverser/Samples$4",//
      "typetraverser/Samples$5",//
      "typetraverser/Samples$InnerA"//
  };

  @Test
  public void testTraverse1() throws Exception {
    final Set<String> expected = new HashSet<String>(
        Arrays.asList(EXPECTEDTYPES));
    TypeTraverser t = new TypeTraverser(root);
    t.process(new ITypeVisitor() {
      public void visit(IType type, String vmname) {
        assertTrue("Unexpected type: " + vmname, expected.remove(vmname));
      }

      public void visit(ICompilationUnit unit) throws JavaModelException {
      }

      public void visit(IClassFile classfile) throws JavaModelException {
      }
    }, MONITOR);
    assertTrue("Not all types processed: " + expected, expected.isEmpty());
  }

}
