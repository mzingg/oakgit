package com.diconium.oak.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;


/**
 * This Class {@link NoOperationCommand} get called if there is no SQL Command.
 *
 */

@Getter
@Setter
public class NoOperationCommand implements Command {

}
