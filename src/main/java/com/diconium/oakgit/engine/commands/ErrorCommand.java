package com.diconium.oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link ErrorCommand} saves an errorMessage.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ErrorCommand extends AbstractCommand<ErrorCommand> {

    @NonNull
    private String errorMessage = StringUtils.EMPTY;

}
