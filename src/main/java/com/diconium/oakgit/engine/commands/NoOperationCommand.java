package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import lombok.Getter;
import lombok.Setter;


/**
 * This Class {@link NoOperationCommand} get called if there is no SQL Command.
 *
 */

@Getter
@Setter
public class NoOperationCommand implements Command {

}
