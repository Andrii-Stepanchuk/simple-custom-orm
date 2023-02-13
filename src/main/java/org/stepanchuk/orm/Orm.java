package org.stepanchuk.orm;

import lombok.SneakyThrows;
import org.stepanchuk.entity.Person;
import org.stepanchuk.orm.annotation.Column;
import org.stepanchuk.orm.annotation.Entity;
import org.stepanchuk.orm.annotation.Id;
import org.stepanchuk.orm.annotation.Table;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Orm {

    private final DBConnection dbConnection = new DBConnection();

    public <T> T find(Class<T> type, Long id) {
        checkClassAnnotations(type);
        List<String> fieldNames = getSelectValuesFromClass(type);
        String sql = createDatabaseQuery(type, fieldNames, id);
        return getEntityFromDatabase(type, fieldNames, sql);
    }

    @SneakyThrows
    private <T> T getEntityFromDatabase(Class<T> type, List<String> fieldNames, String sql) {
        T entity = type.getConstructor().newInstance();
        try (Connection connection = DBConnection.getInstance().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    setValueToEntityFields(type, fieldNames, entity, rs);
                }
            }
        }

        return entity;
    }

    @SneakyThrows
    private <T> void setValueToEntityFields(Class<T> type, List<String> fieldNames, T entity, ResultSet rs) {
        var fieldNamesIterator = fieldNames.iterator();
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            field.set(entity, rs.getObject(fieldNamesIterator.next()));
        }
    }

    @SneakyThrows
    private <T> String createDatabaseQuery(Class<T> type, List<String> fieldNames, Long id) {
        String from = type.getAnnotation(Table.class).value();
        String idField = getIdValueFromEntity(type);
        return new SQLBuilder()
                .addSelect(fieldNames)
                .addFrom(from)
                .addWhere(idField, id)
                .build();
    }

    private <T> String getIdValueFromEntity(Class<T> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(field -> Objects.nonNull(field.getAnnotation(Id.class)))
                .map(field -> field.getAnnotation(Id.class).value())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Annotation @Id not found"));
    }

    private <T> List<String> getSelectValuesFromClass(Class<T> type) {
        return Arrays
                .stream(type.getDeclaredFields())
                .map(this::getColumnName)
                .collect(Collectors.toList());
    }

    private String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (Objects.nonNull(column)) {
            String value = column.value();
            return Objects.nonNull(value) ? value : field.getName();
        }
        return field.getName();
    }

    private <T> void checkClassAnnotations(Class<T> type) {
        if (Objects.isNull(type) ||
                Objects.isNull(type.getAnnotation(Entity.class)) ||
                Objects.isNull(type.getAnnotation(Table.class))) {
            throw new IllegalArgumentException();
        }
    }
}
