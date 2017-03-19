package org.kovacstelekes.tech.blog.java.lang;

import java.util.Random;

public class Resource implements AutoCloseable {
	private static final Random RND = new Random();

	private final String resourceId;

	public Resource(String resourceId) throws ObjectConstructionFailedException {
		if (RND.nextBoolean()) {
			throw new ObjectConstructionFailedException(resourceId);
		}
		this.resourceId = resourceId;
	}

	public void doSomething() throws OperationFailedException {
		if (RND.nextBoolean()) {
			throw new OperationFailedException(resourceId);
		}
	}

	@Override
	public void close() throws CleanupFailedException {
		if (RND.nextBoolean()) {
			throw new CleanupFailedException(resourceId);
		}
	}

}
