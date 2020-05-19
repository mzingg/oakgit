package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.engine.Command;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class QueryAnalyzerTest {

    @UnitTest
    public void parseWithCreateSQLQueryWithValuesReturnsNoData() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint)");

        Map<Object, Object> actual = anAnalyzer().getData(statement, Collections.emptyMap());

        assertThat(actual.size(), is(0));
    }

    private QueryAnalyzer anAnalyzer() {
        return new QueryAnalyzer() {

            @Override
            public QueryMatchResult matchAndCollect(String sqlQuery) {
                return null;
            }

            @Override
            public boolean interestedIn(Statement statement) {
                return false;
            }

            @Override
            public QueryParserResult getParserResult(Statement statement) {
                return null;
            }

            @Override
            public String getTableName(Statement statement) {
                return null;
            }

            @Override
            public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
                return Optional.empty();
            }

            @Override
            public Command createCommand(Statement statement, Map<Integer, Object> placeholderData) {
                return null;
            }
        };
    }

}
