package com.diconium.oak.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
// TODO: Add Javadoc documentation describing the function of this command
/**
 * 
 * This class offers to insert some data in a container using the containerName
 * 
 * @author krollsas
 *
 */
public class InsertIntoContainerCommand implements Command {

    private String containerName = StringUtils.EMPTY;

    /**
     * data field for Data object
     */
    private String data;


}
