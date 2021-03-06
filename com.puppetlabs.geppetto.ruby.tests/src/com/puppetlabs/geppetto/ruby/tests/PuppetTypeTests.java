/**
 * Copyright (c) 2013 Puppet Labs, Inc. and other contributors, as listed below.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Puppet Labs
 */
package com.puppetlabs.geppetto.ruby.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.puppetlabs.geppetto.ruby.PPTypeInfo;
import com.puppetlabs.geppetto.ruby.spi.IRubyParseResult;

public class PuppetTypeTests extends AbstractRubyTests {

	@Test
	public void testConstantExpanded() throws Exception {
		File aRubyFile = testDataProvider.getTestFile(new Path("testData/pp-modules-ruby/module-y/lib/puppet/type/thing.rb"));
		IRubyParseResult<List<PPTypeInfo>> rr = helper.getTypeInfo(aRubyFile);
		assertFalse("Should have found no issues", rr.hasIssues());
		List<PPTypeInfo> foundTypes = rr.getResult();
		assertEquals("Should have found one type", 1, foundTypes.size());
		PPTypeInfo info = foundTypes.get(0);
		assertEquals("Should have found 'thing'", "thing", info.getTypeName());
		assertEquals("Should have found 3 parameters", 3, info.getParameters().size());
		assertEquals("Should have found two properties", 2, info.getProperties().size());

		PPTypeInfo.Entry flagEntry = info.getParameters().get("flag");
		assertNotNull("Should have found a parameter called 'flag'", flagEntry);
		assertEquals(
			"Should have constant expanded in description of 'flag'", "The flag. Valid values are: False/0/No or True/1/Yes.",
			flagEntry.getDocumentation());
	}

	@Test
	public void testParseFunctionInNestedModules() throws Exception {
		File aRubyFile = testDataProvider.getTestFile(new Path("testData/pp-modules-ruby/module-x/lib/puppet/type/thing.rb"));
		IRubyParseResult<List<PPTypeInfo>> rr = helper.getTypeInfo(aRubyFile);
		assertFalse("Should have found no issues", rr.hasIssues());
		List<PPTypeInfo> foundTypes = rr.getResult();
		assertEquals("Should have found one type", 1, foundTypes.size());
		PPTypeInfo info = foundTypes.get(0);
		assertEquals("Should have found 'thing'", "thing", info.getTypeName());
		assertEquals("Should have found two parameters", 2, info.getParameters().size());
		assertEquals("Should have found two properties", 2, info.getProperties().size());

		PPTypeInfo.Entry nameEntry = info.getParameters().get("name");
		assertNotNull("Should have found a parameter called 'name'", nameEntry);
		assertEquals("Should have found a description of 'name'", "Description of name", nameEntry.getDocumentation());

		// TODO: check "ensure"
		PPTypeInfo.Entry weightEntry = info.getProperties().get("weight");
		assertNotNull("Should have found a property called 'weight'", weightEntry);
		assertEquals("Should have found a description of 'weight'", "Description of weight", weightEntry.getDocumentation());

		PPTypeInfo.Entry emptyEntry = info.getProperties().get("empty");
		assertNotNull("Should have found a property called 'weight'", emptyEntry);
		assertNull("Should have found a missing description of 'empty'", emptyEntry.getDocumentation());
	}

}
