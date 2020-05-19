package com.diconium.oakgit.processor.git;

public class GitFilesystemException extends Exception {

    public GitFilesystemException(String message) {
        super(message);
    }

    public GitFilesystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitFilesystemException(Throwable cause) {
        super(cause);
    }

}
