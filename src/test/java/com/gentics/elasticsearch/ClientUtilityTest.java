package com.gentics.elasticsearch;

import static com.gentics.elasticsearch.client.ClientUtility.join;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ClientUtilityTest {

	@Test
	public void testJoin() {
		assertEquals("", join(new String[] {}, ","));
		assertEquals("a", join(new String[] { "a" }, ","));
		assertEquals("a,b", join(new String[] { "a", "b" }, ","));
		assertEquals("a,b,c", join(new String[] { "a", "b", "c" }, ","));
	}
}
