package org.kovacstelekes.tech.blog.java.lang;

public class ModernResourceUser implements ResourceUser {
    @Override
    public void useSingleResource() throws ObjectConstructionFailedException, OperationFailedException, CleanupFailedException {
        try (Resource resource = new Resource("1")) {
            resource.doSomething();
        }
    }

    @Override
    public void useTwoResources() throws ObjectConstructionFailedException, OperationFailedException, CleanupFailedException {
        try (Resource resource1 = new Resource("1"); Resource resource2 = new Resource("2")) {
            resource1.doSomething();
            resource2.doSomething();
        }
    }
}
