package com.diconium.oak.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
// TODO: Add Javadoc documentation describing the function of this command
/**
 * This class offers a Command to create a container using the containerName 
 * 
 * 
 * @value containerName
 * @author krollsas
 *
 */
public class CreateContainerCommand implements Command {

    private String containerName = StringUtils.EMPTY;

}
