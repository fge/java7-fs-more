package com.github.fge.filesystem.exceptions;

import java.io.IOException;

public class RecursiveDeletionException extends IOException {

	public RecursiveDeletionException() {
		super();
	}

	public RecursiveDeletionException(String message) {
		super(message);
	}

	public RecursiveDeletionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RecursiveDeletionException(Throwable cause) {
		super(cause);
	}

}
