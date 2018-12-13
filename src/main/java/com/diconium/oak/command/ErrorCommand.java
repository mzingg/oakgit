package com.diconium.oak.command;

import lombok.Getter;

@Getter
// TODO: Add Javadoc documentation describing the function of this command
public class ErrorCommand implements Command {
    private String errorMessage;

    public ErrorCommand(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
