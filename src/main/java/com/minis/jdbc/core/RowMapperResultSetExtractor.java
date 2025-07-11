package com.minis.jdbc.core;

import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {
    private final RowMapper<T> rowMapper;

    public RowMapperResultSetExtractor(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    @Override
    public List<T> extractData(ResultSet resultSet) throws SQLException {
        List<T> results = new ArrayList<>();
        int rowNum = 0;
        while (resultSet.next()) {
            log.info("RowData: id = {}, name = {}, birthday = {}",
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getDate("birthday"));
            results.add(rowMapper.mapRow(resultSet, rowNum++));
        }
        return results;
    }
}
