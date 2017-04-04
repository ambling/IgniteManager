package com.github.ambling;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Unit test for admin node.
 */
public class AdminNodeTest extends TestCase
{
    Collection<String> hosts;

    /**
     * Create the test case
     */
    public AdminNodeTest()
    {
        super("AdminNode test");

        hosts = new ArrayList<String>();
        hosts.add("localhost");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AdminNodeTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
