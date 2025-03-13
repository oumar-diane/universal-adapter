package org.zenithblox;

public class FailedToInitilizedRepository extends RuntimeZwangineException {

    public FailedToInitilizedRepository(String message) {
        super(message);
    }

    public FailedToInitilizedRepository(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToInitilizedRepository(Throwable cause) {
        super(cause);
    }
}
