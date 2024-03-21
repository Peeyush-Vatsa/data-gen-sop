package com.sp.Records;

import com.github.javafaker.Faker;
import com.sp.Table.DBTable;
import com.sp.Table.DataType;
import com.sp.Table.TableAttribute;

import javax.xml.crypto.Data;
import java.util.*;

public class RecordGenerator {
    private HashMap<String, String> sqlQueries; // Queries to insert records into the tables
    private HashSet<String> foreignKeyDependencies; // Set of foreign key dependencies
    private HashMap<String, HashSet<String>> tableDependencies; // Set of tables that are dependent on a table
    private HashMap<String, ForeignKeyValues> foreignKeyValues; // Set of foreign key values
    private Faker faker;
    public RecordGenerator(HashMap<String, DBTable> tables, int noOfRecords) {
        this.sqlQueries = new HashMap<String,String>();
        this.foreignKeyDependencies = new HashSet<String>();
        this.tableDependencies = new HashMap<String, HashSet<String>>();
        this.foreignKeyValues = new HashMap<String, ForeignKeyValues>();
        this.faker = new Faker();
        for (String table: tables.keySet()) {
            this.tableDependencies.put(table, new HashSet<String>());
        }
        generateForeignKeyDependencies(tables);
        generateInsertQueries(tables, noOfRecords);
    }

    private void generateInsertQueries(HashMap<String, DBTable> tables, int noOfRecords) {
        // Start with all tables which have attributes used as foreign keys
        for(String table: this.foreignKeyDependencies) {
            table = table.split("\\.")[0];
            if (this.sqlQueries.containsKey(table)) {
                continue;
            }
            generateInsertQuery(tables, table, noOfRecords);
        }
        // Generate insert queries for the remaining tables
        for (String table: tables.keySet()) {
            if (this.sqlQueries.containsKey(table)) {
                continue;
            }
            generateInsertQuery(tables, table, noOfRecords);
        }
    }

    private void generateInsertQuery(HashMap<String, DBTable> tables, String table, int noOfRecords) {
        DBTable dbTable = tables.get(table);
        HashMap<String, TableAttribute> attributes = dbTable.getAttributes();
        // Checks for all available data
        for (String attributeName: attributes.keySet()) {
            TableAttribute attribute = attributes.get(attributeName);
            if (attribute.isForeignKey() && !this.sqlQueries.containsKey(attribute.getForeignKeyTable())) {
                generateInsertQuery(tables, attribute.getForeignKeyTable(), noOfRecords);
//                this.generatedRecords.add(attribute.getForeignKeyTable());
            }
        }
        String query = "INSERT INTO " + table + " (";
        for (String attributeName: attributes.keySet()) {
            if (this.foreignKeyDependencies.contains(table + "." + attributeName)) {
                this.foreignKeyValues.put(table + "." + attributeName, new ForeignKeyValues(table, attributeName, attributes.get(attributeName).getType()));
            }
            query += attributeName + ", ";
        }
        query = query.substring(0, query.length() - 2) + ") VALUES ";
        for (int i = 0; i < noOfRecords; i++) {
            query += "(";
            for (String attributeName: attributes.keySet()) {
                TableAttribute attribute = attributes.get(attributeName);
                if (attribute.isForeignKey()) {
                    ForeignKeyValues foreignKey = this.foreignKeyValues.get(table + "." + attributeName);
                    query += foreignKey.getValue(i) + ", ";
                } else {
                    query += generateValue(attribute) + ", ";
                }
            }
            query = query.substring(0, query.length() - 2) + "), ";
        }
        query = query.substring(0, query.length() - 2) + ";";
        this.sqlQueries.put(table, query);
    }

    private String generateValue(TableAttribute attribute) {
        // Update to check for primary key/unique compliance
        // Add null rate at 10% for fields allowing null values
        // Check field name for name, state, district, city, pin, phone, email, dob/age/birthday,gender,etc
        String value = "";
        if (attribute.getType() == DataType.SMALLINT){

        }
        else if (attribute.getType() == DataType.INTEGER) {
            value = Integer.toString(faker.number().numberBetween(0, 1000));
        } else if (attribute.getType() == DataType.BIGINT) {

        } else if (attribute.getType() == DataType.CHARACTER_VARYING) {
            value = "\"" + faker.lorem().word() + "\"";
        } else if (attribute.getType() == DataType.CHARACTER) {
            value = "\"" + faker.lorem().characters(1, true) + "\"";
        } else if (attribute.getType() == DataType.TEXT) {

        } else if (attribute.getType() == DataType.BOOLEAN) {
            value = Boolean.toString(faker.bool().bool());
        } else if (attribute.getType() == DataType.DATE) {
            value = "\"" + faker.date().birthday().toString() + "\"";
        } else if (attribute.getType() == DataType.TIMESTAMP_WITHOUT_TIMEZONE) {
            value = "\"" + faker.date().birthday().toString() + "\"";
        } else if (attribute.getType() == DataType.TIMESTAMP_WITH_TIMEZONE) {

        } else if (attribute.getType() == DataType.DOUBLE_PRECISION) {

        } else if (attribute.getType() == DataType.UUID) {

        } else if (attribute.getType() == DataType.JSONB) {

        }
        return value;
    }
    private void generateForeignKeyDependencies(HashMap<String, DBTable> tables) {
        for (String table: tables.keySet()) {
            DBTable dbTable = tables.get(table);
            HashMap<String, TableAttribute> attributes = dbTable.getAttributes();
            for (String attributeName: attributes.keySet()) {
                TableAttribute attribute = attributes.get(attributeName);
                if (attribute.isForeignKey()) {
                    String foreignKeyTable = attribute.getForeignKeyTable();
                    if (!tables.containsKey(foreignKeyTable)) {
                        throw new RuntimeException("Foreign key dependency not found");
                    }
                    this.tableDependencies.get(table).add(foreignKeyTable);
                    String foreignKeyColumn = attribute.getForeignKeyColumn();
                    if (!tables.get(foreignKeyTable).getAttributes().containsKey(foreignKeyColumn)) {
                        foreignKeyColumn = "\"" + foreignKeyColumn + "\"";
                        if (!tables.get(foreignKeyTable).getAttributes().containsKey(foreignKeyColumn)) {
                            throw new RuntimeException("Foreign key dependency not found");
                        }
                    }
                    String foreignKey = foreignKeyTable + "." + foreignKeyColumn;
                    this.foreignKeyDependencies.add(foreignKey);
                }
            }
        }
    }
}