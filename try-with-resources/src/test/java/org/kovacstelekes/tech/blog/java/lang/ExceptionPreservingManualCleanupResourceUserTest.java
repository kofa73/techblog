package org.kovacstelekes.tech.blog.java.lang;

public class ExceptionPreservingManualCleanupResourceUserTest extends AbstractResourceUserTest {
    public ExceptionPreservingManualCleanupResourceUserTest() {
        super(new ExceptionPreservingManualCleanupResourceUser(), 4, 14);
    }
}
