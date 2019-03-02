package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.model.ContainerEntry;
import com.diconium.oakgit.engine.model.UpdateSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class UpdatDataInContainerCommand implements Command {

    @NonNull
    private String containerName = StringUtils.EMPTY;

    @NonNull
    private String id = StringUtils.EMPTY;

    @NonNull
    private long modCount = 0L;

    @NonNull
    private UpdateSet data = new UpdateSet();

    @Override
    public String toString() {
        return "UpdatDataInContainerCommand{" +
                "containerName='" + containerName + '\'' +
                ", id='" + id + '\'' +
                ", modCount=" + modCount +
                ", data=" + data +
                '}';
    }
}
