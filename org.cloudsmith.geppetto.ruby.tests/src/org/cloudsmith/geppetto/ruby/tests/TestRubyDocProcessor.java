/**
 * Copyright (c) 2012 Cloudsmith Inc. and other contributors, as listed below.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Cloudsmith
 * 
 */
package org.cloudsmith.geppetto.ruby.tests;

import java.util.List;

import junit.framework.TestCase;

import org.cloudsmith.geppetto.ruby.RubyDocProcessorSimple;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * @author henrik
 * 
 */
public class TestRubyDocProcessor extends TestCase {
	public static final List<String> content1 = ImmutableList.of( //
		"This is an example:\n", //
		"  verbatim\n", //
		"  verbatim\n", //
		"Non verbatim\n");

	public static final List<String> content1Expected = ImmutableList.of( //
		"<p>This is an example:\n", //
		"</p>\n", //
		"<pre>", //
		"  verbatim\n", //
		"  verbatim\n", //
		"</pre>\n", //
		"<p>Non verbatim\n", //
		"</p>");

	public void testRubyDocprocessor() {

		String result = RubyDocProcessorSimple.asHTML(Joiner.on("").join(content1));
		assertEquals(Joiner.on("").join(content1Expected), result);

	}
}