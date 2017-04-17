package org.kovacstelekes.tech.blog.java.lang;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class LoggingManualCleanupResourceUserTest {
    private final TestLogger log = TestLoggerFactory.getTestLogger(LoggingManualCleanupResourceUser.class);
    
    private final ResourceUser resourceUser = new LoggingManualCleanupResourceUser();
    
    @Test
    public void testUseSingleResource() {
        int failureModes = test(resourceUser::useSingleResource, 100);
        assertThat(failureModes, is(4));
    }

    @Test
    public void testUseTwoResources() {
        int failureModes = test(resourceUser::useTwoResources, 1_000);
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
