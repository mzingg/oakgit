package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.model.ContainerEntry;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class SelectFromContainerByIdRangeCommand implements Command {
	
	private String containerName = StringUtils.EMPTY;
	
	private String idMin = StringUtils.EMPTY;
	private String idMax = StringUtils.EMPTY;

	public CommandResult buildResult(List<ContainerEntry> foundEntries) {
		return new SelectFromContainerByIdRangeCommandResult(this, foundEntries);
	}

	@Override
	public String toString() {
		return "SelectFromContainerByIdRangeCommand{" +
				"containerName='" + containerName + '\'' +
				", idMin='" + idMin + '\'' +
				", idMax='" + idMax + '\'' +
				'}';
	}
}
