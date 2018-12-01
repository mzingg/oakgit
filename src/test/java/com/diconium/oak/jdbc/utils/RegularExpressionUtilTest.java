package com.diconium.oak.jdbc.utils;

import org.apache.jackrabbit.oak.api.ContentSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class RegularExpressionUtilTest {

    public static final String CREATE_TABLE_PATTERN_TEST = "create table SETTINGS";

    public static final String CREATE_TABLE_COLUMNS_PATTERN_TEST =
            "create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, " +
                    "DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, " +
                    "SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))";
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getTableNameTest() {
       String expectedContainerName = "SETTINGS";
       String containerName = RegularExpressionUtil.getTableName(CREATE_TABLE_PATTERN_TEST);
       assertThat(containerName, is(expectedContainerName));
    }

    @Test
    void getTableNameTestWithCoumnNames() {
        String expectedContainerName = "CLUSTERNODES";
        String containerName = RegularExpressionUtil.getTableName(CREATE_TABLE_COLUMNS_PATTERN_TEST);
        assertThat(containerName, is(expectedContainerName));
    }

}