package org.kovacstelekes.tech.blog.java.lang;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class TestManualCleanupWithLogging {
    private final TestLogger log = TestLoggerFactory.getTestLogger(TestManualCleanupWithLogging.class);
    
    private void tryWithSingleResource() {
        Optional<RuntimeException> root = Optional.empty();
        Resource resource = null;
        try {
            resource = new Resource("1");
            resource.doSomething();
        } catch (ObjectConstructionFailedException | OperationFailedException e) {
            root = Optional.of(e);
        } finally {
            if (resource != null) {
                try {
                    resource.close();
                } catch (CleanupFailedException e) {
                    root = handleCleanupFailedException(root, e);
                }
            }
        }
        if (root.isPresent()) {
            throw root.get();
        }
    }

    private Optional<RuntimeException> handleCleanupFailedException(Optional<RuntimeException> root, CleanupFailedException e) {
        if (!root.isPresent()) {
            root = Optional.of(e);
        } else {
            log.error("Cleanup failed", e);
        }
        return root;
    }

    private void tryWithTwoResources() {
        Resource resource1, resource2;
        resource1 = resource2 = null;
        Optional<RuntimeException> root = Optional.empty();
        try {
            resource1 = new Resource("1");
            resource2 = new Resource("2");
            resource1.doSomething();
            resource2.doSomething();
        } catch (ObjectConstructionFailedException | OperationFailedException e) {
            root = Optional.of(e);
        } finally {
            if (resource1 != null) {
                try {
                    if (resource2 != null) {
                        try {
                            resource2.close();
                        } catch (CleanupFailedException e) {
                            root = handleCleanupFailedException(root, e);
                        }
                    }
                } finally {
                    try {
                        resource1.close();
                    } catch (CleanupFailedException e) {
                        root = handleCleanupFailedException(root, e);
                    }
                }
            }
        }
        if (root.isPresent()) {
            throw root.get();
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
                String trace = getStacktrace(e) + loggedExceptions();
                stacktraces.add(trace);
            } finally {
                log.clear();
            }
        }
        output(stacktraces);
        return stacktraces.size();
    }
    
    private String loggedExceptions() {
        return log.getLoggingEvents().stream().map(logEvent -> getStacktrace(logEvent.getThrowable().get())).collect(Collectors.joining("\n"));
    }

    private String getStacktrace(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    private void output(Set<String> stacktraces) {
        stacktraces.stream().map(s -> s + "\n").forEach(System.out::println);
        System.out.println("Total cases: " + stacktraces.size());
    }
}
