package com.diconium.oak.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * This class {@link InsertIntoContainerCommand} offers to insert some {@link data} in a container using the {@link containerName}.
 *
 */

@Getter
@Setter
public class InsertIntoContainerCommand implements Command {

    private String containerName = StringUtils.EMPTY;

    /**
     * data field for Data object
     */
    private String data;


}
