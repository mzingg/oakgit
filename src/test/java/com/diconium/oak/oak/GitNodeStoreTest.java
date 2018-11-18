package com.diconium.oak.oak;

import com.diconium.oak.TestHelpers;
import org.apache.jackrabbit.oak.plugins.document.Collection;
import org.apache.jackrabbit.oak.plugins.document.UpdateOp;
import org.apache.jackrabbit.oak.plugins.document.util.Utils;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.diconium.oak.TestHelpers.getTestDirectory;

class GitNodeStoreTest {


    @Test
    void insertDocument() throws Exception {

        Path gitDirectory = TestHelpers.getTestDirectory("insertTest");
        Git.init().setDirectory(gitDirectory.toFile()).call();

        GitNodeStore store = new GitNodeStore(gitDirectory);

        UpdateOp op = new UpdateOp(Utils.getPathFromId("/"), true);
        op.set("testProperty", "test");
        store.createOrUpdate(Collection.NODES, op);

        op = new UpdateOp(Utils.getPathFromId("/"), false);
        op.set("testProperty", "test2");
        op.set("hallo", "blubb");
        store.createOrUpdate(Collection.NODES, op);

        op = new UpdateOp(Utils.getPathFromId("/content/de"), true);
        op.set("testProperty", "test2");
        op.set("hallo", "blubb");
        store.createOrUpdate(Collection.NODES, op);
    }

}
