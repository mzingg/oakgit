package com.diconium.oak.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;


/**
 * 
 * This class offers a {@link container} using the {@link containerName}.
 * 
 *
 */

@Getter
@Setter

//TODO: Find a better name for this command - what is its meaning?
public class GetContainerCommand implements Command {

    private String containerName = StringUtils.EMPTY;

}
