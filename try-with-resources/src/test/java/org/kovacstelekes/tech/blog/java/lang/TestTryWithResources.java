package org.kovacstelekes.tech.blog.java.lang;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestTryWithResources {

    public void tryWithSingleResource() throws ObjectConstructionFailedException, OperationFailedException, CleanupFailedException {
        try (Resource resource = new Resource("1")) {
            resource.doSomething();
        }
    }

    public void tryWithTwoResources() throws ObjectConstructionFailedException, OperationFailedException, CleanupFailedException {
        try (Resource resource1 = new Resource("1"); Resource resource2 = new Resource("2")) {
            resource1.doSomething();
            resource2.doSomething();
        }
    }
    
    @Test
    public void testTryWithSingleResource() {
        int failureModes = test(this::tryWithSingleResource, 100);
        assertThat(failureModes, is(4));
    }

    @Test
    public void testTryWithTwoResources() {
        int failureModes = test(this::tryWithTwoResources, 1_000);
        assertThat(failureModes, is(14));
    }

    private int test(Runnable test, int iterations) {
        Set<String> stacktraces = new HashSet<>();
        for (int i = 1; i < iterations; i++) {
            try {
                test.run();
            } catch (Exception e) {
                stacktraces.add(getStacktrace(e));
            }
        }
        output(stacktraces);
        return stacktraces.size();
    }
    
    private String getStacktrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String stacktrace = stringWriter.toString();
        return stacktrace;
    }

    private void output(Set<String> stacktraces) {
        stacktraces.stream().map(s -> s + "\n").forEach(System.out::println);
        System.out.println("Total cases: " + stacktraces.size());
    }
}
