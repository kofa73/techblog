package org.kovacstelekes.tech.blog.java.lang;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

public class TryWithResources {
    private static void tryWithSingleResource()
            throws ObjectConstructionFailedException, OperationFailedException, CleanupFailedException {
        try (Resource resource = new Resource("1")) {
            resource.doSomething();
        }
    }

    private static void tryWithTwoResources()
            throws ObjectConstructionFailedException, OperationFailedException, CleanupFailedException {
        try (Resource resource1 = new Resource("1"); Resource resource2 = new Resource("2")) {
            resource1.doSomething();
            resource2.doSomething();
        }
    }

    private static void test(TryWithResourcesTest test, int iterations) {
        Set<String> stacktraces = new HashSet<>();
        for (int i = 1; i < iterations; i++) {
            try {
                test.test();
            } catch (ObjectConstructionFailedException | OperationFailedException | CleanupFailedException e) {
                stacktraces.add(getStacktrace(e));
            }
        }
        output(stacktraces);
    }

    public static void main(String[] args) {
        test(TryWithResources::tryWithSingleResource, 100);
        test(TryWithResources::tryWithTwoResources, 10_000);
    }

    private static void output(Set<String> stacktraces) {
        System.out.println(stacktraces);
        System.out.println("Number of unique stacktraces: " + stacktraces.size());
    }

    private static String getStacktrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String stacktrace = stringWriter.toString();
        return stacktrace;
    }
}
