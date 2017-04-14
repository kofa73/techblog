package org.kovacstelekes.tech.blog.java.lang;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class TestManualCleanupWithoutInformationLoss {

    private void tryWithSingleResource() {
        List<RuntimeException> exceptions = new ArrayList<>();
        Resource resource = null;
        try {
            resource = new Resource("1");
            resource.doSomething();
        } catch (ObjectConstructionFailedException | OperationFailedException e) {
            exceptions.add(e);
        } finally {
            if (resource != null) {
                try {
                    resource.close();
                } catch (CleanupFailedException e) {
                    exceptions.add(e);
                }
            }
        }
        rethrowAllExceptions(exceptions);
    }

    private void rethrowAllExceptions(List<RuntimeException> exceptions) {
        if (!exceptions.isEmpty()) {
            Iterator<RuntimeException> exceptionsIterator = exceptions.iterator();
            RuntimeException rootException = exceptionsIterator.next();
            while (exceptionsIterator.hasNext()) {
                rootException.addSuppressed(exceptionsIterator.next());
            }
            throw rootException;
        }
    }
    
    private void tryWithTwoResources() {
        List<RuntimeException> exceptions = new ArrayList<>();
        Resource resource1, resource2;
        resource1 = resource2 = null;
        try {
            resource1 = new Resource("1");
            resource2 = new Resource("2");
            resource1.doSomething();
            resource2.doSomething();
        } catch (ObjectConstructionFailedException | OperationFailedException e) {
            exceptions.add(e);
        } finally {
            if (resource1 != null) {
                try {
                    if (resource2 != null) {
                        try {
                            resource2.close();
                        } catch (CleanupFailedException e) {
                            exceptions.add(e);
                        }
                    }
                } finally {
                    try {
                        resource1.close();
                    } catch (CleanupFailedException e) {
                        exceptions.add(e);
                    }
                }
            }
        }
        rethrowAllExceptions(exceptions);
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
