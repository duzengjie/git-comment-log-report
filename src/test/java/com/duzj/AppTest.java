package com.duzj;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;

/**
 * Unit tests for App.
 * Note: Using JUnit 3 (junit.framework.*) per existing project conventions.
 *
 * Coverage:
 * - App.main with empty args prints greeting (happy path)
 * - App.main with null args prints greeting (edge case)
 * - App.main with non-empty args prints greeting (input ignored, still prints)
 * - Public interface validation: main signature is public static void
 * - Instantiation sanity check
 */
public class AppTest extends TestCase {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Override
    protected void tearDown() throws Exception {
        System.setOut(originalOut);
        if (outContent \!= null) {
            outContent.reset();
        }
        super.tearDown();
    }

    private String normalizedOutput() {
        // Normalize newlines for cross-platform consistency
        return outContent.toString().replace("\r\n", "\n");
    }

    /**
     * Happy path: main with empty args should print greeting.
     */
    public void testMainWithEmptyArgsPrintsHelloWorld() {
        App.main(new String[0]);
        String output = normalizedOutput();
        assertTrue("Expected output to contain 'Hello World\!'", output.contains("Hello World\!"));
    }

    /**
     * Edge case: main with null args should not throw and should print greeting.
     */
    public void testMainWithNullArgsPrintsHelloWorld() {
        App.main(null);
        String output = normalizedOutput();
        assertTrue("Expected output to contain 'Hello World\!'", output.contains("Hello World\!"));
    }

    /**
     * Input present: main with some args should still print greeting (args typically ignored).
     */
    public void testMainWithSomeArgsPrintsHelloWorld() {
        App.main(new String[] { "arg1", "arg2" });
        String output = normalizedOutput();
        assertTrue("Expected output to contain 'Hello World\!'", output.contains("Hello World\!"));
    }

    /**
     * Public interface validation: main must be public static void(String[]).
     */
    public void testMainMethodSignatureIsPublicStaticVoid() throws Exception {
        java.lang.reflect.Method m = App.class.getMethod("main", String[].class);
        assertEquals("main should return void", void.class, m.getReturnType());
        assertTrue("main should be public", Modifier.isPublic(m.getModifiers()));
        assertTrue("main should be static", Modifier.isStatic(m.getModifiers()));
    }

    /**
     * Sanity: App can be instantiated (default constructor).
     */
    public void testAppCanBeInstantiated() {
        App app = new App();
        assertNotNull("App instance should not be null", app);
    }

    /**
     * Legacy baseline: keep a trivially passing test to mirror original scaffold.
     */
    public void testBaselineTruth() {
        assertTrue(true);
    }
}
