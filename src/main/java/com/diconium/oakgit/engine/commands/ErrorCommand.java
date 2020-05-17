package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


/**
 * 
 * {@link ErrorCommand} saves an errorMessage. 
 * 
 */

@Getter
@ToString
public class ErrorCommand implements Command {
    private String errorMessage;

    public ErrorCommand(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
