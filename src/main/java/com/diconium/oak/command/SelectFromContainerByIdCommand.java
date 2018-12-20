package com.diconium.oak.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
//TODO: Add Javadoc documentation describing the function of this command
/**
 * This class uses a specific container using the containerName and 
 * selects specific data using the ID
 * 
 * @author krollsas
 *
 */
public class SelectFromContainerByIdCommand implements Command{
	
	private String containerName = StringUtils.EMPTY;
	
	private String ID = StringUtils.EMPTY;
}
