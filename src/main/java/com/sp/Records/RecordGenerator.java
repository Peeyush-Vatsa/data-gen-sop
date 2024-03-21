package com.sp.Records;

import com.sp.Table.DBTable;
import com.sp.Table.TableAttribute;

import java.util.*;

public class RecordGenerator {
    private HashMap<String, String> sqlQueries; // Queries to insert records into the tables
    private HashSet<String> foreignKeyDependencies; // Set of foreign key dependencies
    private HashMap<String, HashSet<String>> tableDependencies; // Set of tables that are dependent on a table
    public RecordGenerator(HashMap<String, DBTable> tables, int noOfRecords) {
        this.sqlQueries = new HashMap<String,String>();
        this.foreignKeyDependencies = new HashSet<String>();
        this.tableDependencies = new HashMap<String, HashSet<String>>();
        for (String table: tables.keySet()) {
            this.tableDependencies.put(table, new HashSet<String>());
        }
        generateForeignKeyDependencies(tables);
        generateInsertQueries(tables, noOfRecords);
    }

    private void generateInsertQueries(HashMap<String, DBTable> tables, int noOfRecords) {

    }

    private void generateForeignKeyDependencies(HashMap<String, DBTable> tables) {
        for (String table: tables.keySet()) {
            DBTable dbTable = tables.get(table);
            HashMap<String, TableAttribute> attributes = dbTable.getAttributes();
            for (String attributeName: attributes.keySet()) {
                TableAttribute attribute = attributes.get(attributeName);
                if (attribute.isForeignKey() == true) {
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