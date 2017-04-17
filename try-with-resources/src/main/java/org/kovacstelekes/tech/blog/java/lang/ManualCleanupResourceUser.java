package org.kovacstelekes.tech.blog.java.lang;

public class ManualCleanupResourceUser implements ResourceUser {
    public void useSingleResource() {
        Resource resource = null;
        try {
            resource = new Resource("1");
            resource.doSomething();
        } finally {
            if (resource != null) {
                resource.close();
            }
        }
    }
    
    public void useTwoResources() {
        Resource resource1, resource2;
        resource1 = resource2 = null;
        try {
            resource1 = new Resource("1");
            resource2 = new Resource("2");
            resource1.doSomething();
            resource2.doSomething();
        } finally {
            if (resource1 != null) {
                try {
                    if (resource2 != null) {
                        resource2.close();
                    }
                } finally {
                    resource1.close();
                }
            }
        }
    }
    
}
