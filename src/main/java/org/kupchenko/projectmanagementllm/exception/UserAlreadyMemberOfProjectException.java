package org.kupchenko.projectmanagementllm.exception;

public class UserAlreadyMemberOfProjectException extends RuntimeException {
    public UserAlreadyMemberOfProjectException(String message) {
        super(message);
    }

    public UserAlreadyMemberOfProjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
