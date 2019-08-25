package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class SelectFromContainerByIdRangeCommand extends AbstractCommand<SelectFromContainerByIdRangeCommand> {

    @NonNull
    private String containerName = StringUtils.EMPTY;

    @NonNull
    private String idMin = StringUtils.EMPTY;

    @NonNull
    private String idMax = StringUtils.EMPTY;

    public <T extends ContainerEntry<T>> CommandResult buildResult(List<ContainerEntry<T>> foundEntries) {
        return new SelectFromContainerByIdRangeCommandResult<>(this, foundEntries);
    }

}
