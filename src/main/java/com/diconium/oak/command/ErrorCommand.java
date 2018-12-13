package com.diconium.oak.command;

import lombok.Getter;

@Getter
public class ErrorCommand implements Command {
    private String errorMessage;

    public ErrorCommand(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
