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
    private HashMap<String, ForeignKeyValues> foreignKeyValues; // Set of foreign key values
    private Faker faker;
    private int noOfRecords;
    public RecordGenerator(HashMap<String, DBTable> tables, int noOfRecords) {
        this.sqlQueries = new HashMap<String,String>();
        this.foreignKeyDependencies = new HashSet<String>();
        this.foreignKeyValues = new HashMap<String, ForeignKeyValues>();
        this.faker = new Faker(new Locale("en-IND"));
        this.noOfRecords = noOfRecords;
        generateForeignKeyDependencies(tables);
        generateInsertQueries(tables, noOfRecords);
    }

    public HashMap<String, String> getSqlQueries() {
        return sqlQueries;
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
        System.out.println("Generating insert query for table: " + table);
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
        String query = "INSERT INTO public.\"" + table + "\" (";
        for (String attributeName: attributes.keySet()) {
            if (this.foreignKeyDependencies.contains(table + "." + attributeName)) {
                this.foreignKeyValues.put(table + "." + attributeName, new ForeignKeyValues(table, attributeName, attributes.get(attributeName).getType()));
            }
            if (attributeName.startsWith("\"") && attributeName.endsWith("\""))
                query += attributeName + ", ";
            else
                query += "\"" + attributeName + "\", ";
        }
        query = query.substring(0, query.length() - 2) + ") VALUES ";
        HashMap<String, HashSet<String>> uniqueEntries = new HashMap<String, HashSet<String>>();
        for (String attribute: attributes.keySet()) {
            if (attributes.get(attribute).isUnique() || attributes.get(attribute).isPrimaryKey()) {
                uniqueEntries.put(attribute, new HashSet<String>());
            }
        }
        for (int i = 0; i < noOfRecords; i++) {
            query += "(";
            for (String attributeName: attributes.keySet()) {
                TableAttribute attribute = attributes.get(attributeName);
                if (attribute.isForeignKey()) {
                    String foreignKey = attribute.getForeignKeyTable() + "." + attribute.getForeignKeyColumn();
                    ForeignKeyValues foreignKeyValues;
                    if (this.foreignKeyDependencies.contains(foreignKey)) {
                        foreignKeyValues = this.foreignKeyValues.get(foreignKey);
                    }
                    else {
                        foreignKey = attribute.getForeignKeyTable() + ".\"" + attribute.getForeignKeyColumn() + "\"";
                        if (this.foreignKeyDependencies.contains(foreignKey)) {
                            foreignKeyValues = this.foreignKeyValues.get(foreignKey);
                        }
                        else {
                            throw new RuntimeException("Foreign key dependency not found");
                        }
                    }
                    query += foreignKeyValues.getValue(i) + ", ";
                } else {
                    String value = generateValue(attribute);
                    if (uniqueEntries.containsKey(attributeName)) {
                        while (uniqueEntries.get(attributeName).contains(value)) {
                            value = generateValue(attribute);
                        }
                        uniqueEntries.get(attributeName).add(value);
                    }
                    if (this.foreignKeyDependencies.contains(table + "." + attributeName)) {
                        ForeignKeyValues foreignKey = this.foreignKeyValues.get(table + "." + attributeName);
                        foreignKey.addValue(value);
                    }
                    query += value + ", ";
                }
            }
            query = query.substring(0, query.length() - 2) + "), ";
        }
        query = query.substring(0, query.length() - 2) + ";";
        this.sqlQueries.put(table, query);
        System.out.println("Insert query generated for table: " + table);
    }

    private String generateValue(TableAttribute attribute) {
        // Update to check for primary key/unique compliance
        String value = "";
        if (attribute.getType() == DataType.SMALLINT){
            value = Integer.toString(faker.number().numberBetween(0, 100));
        }
        else if (attribute.getType() == DataType.INTEGER) {
            if (attribute.isPrimaryKey() || attribute.isUnique())
                value = Long.toString(faker.number().numberBetween(0, this.noOfRecords * 10));
            else if (attribute.getName().toLowerCase().contains("phone") || attribute.getName().toLowerCase().contains("mobile"))
                value = faker.phoneNumber().cellPhone();
            else if (attribute.getName().toLowerCase().contains("pin"))
                value = faker.address().zipCode();
            else if (attribute.getName().toLowerCase().contains("age"))
                value = Integer.toString(faker.number().numberBetween(0, 100));
            else
                value = Long.toString(faker.number().numberBetween(0, 1000));
        } else if (attribute.getType() == DataType.BIGINT) {
            value = Long.toString(faker.number().numberBetween(0, 1000000));
        } else if (attribute.getType() == DataType.CHARACTER_VARYING) {
            if (attribute.getName().toLowerCase().contains("name"))
                value = "'" + faker.name().fullName() + "'";
            else if (attribute.getName().toLowerCase().contains("state"))
                value = "'" + faker.address().state() + "'";
            else if (attribute.getName().toLowerCase().contains("district"))
                value = "'" + faker.address().city() + "'";
            else if (attribute.getName().toLowerCase().contains("city"))
                value = "'" + faker.address().city() + "'";
            else if (attribute.getName().toLowerCase().contains("email"))
                value = "'" + faker.internet().emailAddress() + "'";
            else
                value = "'" + faker.lorem().characters(0, attribute.getCharLength(), true) + "'";
        } else if (attribute.getType() == DataType.CHARACTER) {
            value = "'" + faker.lorem().characters(attribute.getCharLength(), true) + "'";
        } else if (attribute.getType() == DataType.TEXT) {
            value = "'" + faker.lorem().characters(0, 1000, true) + "'";
        } else if (attribute.getType() == DataType.BOOLEAN) {
            value = Boolean.toString(faker.bool().bool());
        } else if (attribute.getType() == DataType.DATE) {
            value = "'" + faker.date().birthday().toString() + "'";
        } else if (attribute.getType() == DataType.TIMESTAMP_WITHOUT_TIMEZONE) {
            value = "'" + faker.date().birthday().toString() + "'";
        } else if (attribute.getType() == DataType.TIMESTAMP_WITH_TIMEZONE) {
            value = "'" + faker.date().birthday().toString() + "'";
        } else if (attribute.getType() == DataType.DOUBLE_PRECISION) {
            value = Double.toString(faker.number().randomDouble(2, 0, 1000));
        } else if (attribute.getType() == DataType.UUID) {
            value = "'" + UUID.randomUUID().toString() + "'";
        } else if (attribute.getType() == DataType.JSONB) {
            value = "'" + "{\"foo\": \"" + faker.lorem().characters(0,100)+ "\"}" + "'";
        }
        return value;
    }
    private void generateForeignKeyDependencies(HashMap<String, DBTable> tables) {
        System.out.println("Generating foreign key dependencies");
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
        System.out.println("Foreign key dependencies generated");
    }
}