package org.kovacstelekes.tech.blog.java.lang;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

abstract class AbstractResourceUserTest {
    private final ResourceUser resourceUser;
    private int expectedResultsWithSingleResource;
    private int expectedResultsWithTwoResources;
    
    AbstractResourceUserTest(ResourceUser resourceUser, int expectedResultsWithSingleResource, int expectedResultsWithTwoResources) {
        this.resourceUser = resourceUser;
        this.expectedResultsWithSingleResource = expectedResultsWithSingleResource;
        this.expectedResultsWithTwoResources = expectedResultsWithTwoResources;
    }
    
    @Test
    public void testTryWithSingleResource() {
        int failureModes = test(resourceUser::useSingleResource, 100);
        assertThat(failureModes, is(expectedResultsWithSingleResource));
    }

    @Test
    public void testTryWithTwoResources() {
        int failureModes = test(resourceUser::useTwoResources, 1_000);
        assertThat(failureModes, is(expectedResultsWithTwoResources));
    }

    private int test(Runnable test, int iterations) {
        Set<String> outcomes = new HashSet<>();
        for (int i = 1; i < iterations; i++) {
            try {
                test.run();
            } catch (Exception e) {
                outcomes.add(outcome(e));
            }
        }
        output(outcomes);
        return outcomes.size();
    }
    
    protected String outcome(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    private void output(Set<String> stacktraces) {
        stacktraces.stream().map(s -> s + "\n").forEach(System.out::println);
        System.out.println("Total cases: " + stacktraces.size());
    }
}
