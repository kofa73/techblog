package org.kovacstelekes.tech.blog.java.lang;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExceptionPreservingManualCleanupResourceUser implements ResourceUser {
    @Override
    public void useSingleResource() {
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

    @Override
    public void useTwoResources() {
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

    private void rethrowAllExceptions(List<RuntimeException> exceptions) {
        Iterator<RuntimeException> exceptionsIterator = exceptions.iterator();
        if (exceptionsIterator.hasNext()) {
            RuntimeException rootException = exceptionsIterator.next();
            while (exceptionsIterator.hasNext()) {
                rootException.addSuppressed(exceptionsIterator.next());
            }
            throw rootException;
        }
    }

}
