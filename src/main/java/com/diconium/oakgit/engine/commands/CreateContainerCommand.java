package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * This class offers a {@link Command} to create a container using the containerName
 */
@Getter
@Setter
public class CreateContainerCommand implements Command {

    private String containerName = StringUtils.EMPTY;

}
