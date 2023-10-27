package org.dwrik;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class WCTest extends TestCase {

  public WCTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(WCTest.class);
  }

  public void testApp() {
    assertTrue(true);
  }
}
