package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
public class SelectFromContainerByIdRangeCommand implements Command, MultipleEntitiesCommandResultProvider {

	private String containerName = StringUtils.EMPTY;

	private String idMin = StringUtils.EMPTY;
	private String idMax = StringUtils.EMPTY;
    private List<String> resultFieldList = Collections.emptyList();

	public <T extends ContainerEntry<T>> CommandResult buildResult(List<ContainerEntry<T>> foundEntries) {
		return new MultipleEntitiesCommandResult<T>(this, foundEntries);
	}

}
