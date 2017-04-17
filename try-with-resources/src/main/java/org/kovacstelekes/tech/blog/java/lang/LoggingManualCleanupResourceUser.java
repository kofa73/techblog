package org.kovacstelekes.tech.blog.java.lang;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingManualCleanupResourceUser implements ResourceUser {
    private final Logger log = LoggerFactory.getLogger(LoggingManualCleanupResourceUser.class);
    
    public void useSingleResource() {
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
        if (root.isPresent()) {
            log.error("Cleanup failed", e);
        } else {
            root = Optional.of(e);
        }
        return root;
    }

    public void useTwoResources() {
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

}
