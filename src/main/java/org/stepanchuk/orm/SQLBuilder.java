package org.stepanchuk.orm;

import java.util.List;
import java.util.StringJoiner;

class SQLBuilder {
    private StringBuilder sql;

    public SQLBuilder() {
        this.sql = new StringBuilder();
    }

    public SQLBuilder addSelect() {
        sql.append("SELECT * ");
        return this;
    }

    public SQLBuilder addSelect(List<String> values) {
        StringJoiner stringJoiner = new StringJoiner(",");
        values.forEach(stringJoiner::add);
        sql.append("SELECT ").append(stringJoiner).append(" ");
        return this;
    }

    public SQLBuilder addFrom(String value) {
        sql.append("FROM ").append(value).append(" ");
        return this;
    }

    public SQLBuilder addWhere(String field, Long value) {
        sql.append("WHERE ").append(field).append(" = ").append(value);
        return this;
    }

    public String build(){
        return sql.toString();
    }

}
