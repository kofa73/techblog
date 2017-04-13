package org.kovacstelekes.tech.blog.java.lang;

import java.util.Random;

public class Resource implements AutoCloseable {
	private final Random random = new Random();

	private final String resourceId;

	public Resource(String resourceId) throws ObjectConstructionFailedException {
		if (random.nextBoolean()) {
			throw new ObjectConstructionFailedException(resourceId);
		}
		this.resourceId = resourceId;
	}

	public void doSomething() throws OperationFailedException {
		if (random.nextBoolean()) {
			throw new OperationFailedException(resourceId);
		}
	}

	@Override
	public void close() throws CleanupFailedException {
		if (random.nextBoolean()) {
			throw new CleanupFailedException(resourceId);
		}
	}

}
