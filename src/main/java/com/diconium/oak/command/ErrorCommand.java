package com.diconium.oak.command;

import lombok.Getter;

@Getter
// TODO: Add Javadoc documentation describing the function of this command
/**
 * 
 * ErrorCommand saves an errorMessage. 
 * 
 * @author krollsas
 *
 */
public class ErrorCommand implements Command {
    private String errorMessage;

    public ErrorCommand(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
