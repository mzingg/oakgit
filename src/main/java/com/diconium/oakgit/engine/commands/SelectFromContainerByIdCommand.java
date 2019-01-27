package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;



/**
 * This class {@link SelectFromContainerByIdCommand} using the {@link containerName} and 
 * selects specific data using the {@link ID}.
 * 
 */

@Getter
@Setter
public class SelectFromContainerByIdCommand implements Command {
	
	private String containerName = StringUtils.EMPTY;
	
	private String ID = StringUtils.EMPTY;
}
