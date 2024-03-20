package com.sp.TableParser;

import com.sp.Table.DBTable;

import java.util.*;
public class TableParser {
    private HashMap<String, DBTable> tables;

    public TableParser() {
        this.tables = new HashMap<String, DBTable>();
    }

    public HashMap<String, DBTable> getTables() {
        return tables;
    }

    public void parseFromQuery(String query) throws Exception {
        query = query.trim();
        String query1 = query.toLowerCase();
        if(query1.startsWith("create table")){
            // Gets table name
            String[] queryParts = query.split(" ");
            String tableName = queryParts[2];
            if (this.tables.containsKey(tableName)){
                throw new Exception("Table already exists");
            }
            // Gets attributes
            String attributes = query.substring(query.indexOf("(") + 1, query.lastIndexOf(")"));
            DBTable newTable = new DBTable(tableName, attributes);
            this.tables.put(tableName, newTable);
            return;
        }
        else if (query1.startsWith("alter table")){
            if (query1.contains("constraint")) {
                String[] queryParts = query.split(" ");
                String tableName = queryParts[2];
                if (tableName.equalsIgnoreCase("only")){
                    tableName = queryParts[3];
                }
                if (!this.tables.containsKey(tableName)){
                    throw new Exception("Table does not exist");
                }
                DBTable table = this.tables.get(tableName);
                String constraint = query.substring(query1.indexOf("constraint"));
                table.updateConstraint(constraint);
            }
        }
    }
}
