package com.sp;

import com.sp.Table.AttributeCheckConstraint;
import com.sp.Table.DBTable;
import com.sp.Table.TableAttribute;
import com.sp.TableParser.TableParser;

import java.io.*;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Insufficient/excess arguments provided. Required: 3");
            return;
        }

        String databaseCredentialFile = args[0];
        String sourceSchema = args[1];
        int noOfRecords = Integer.parseInt(args[2]);

        // Opening the database connection

        // Reading the schema file
        try {
            FileReader sourceSchemaReader = new FileReader(sourceSchema);
            BufferedReader sourceSchemaBufferedReader = new BufferedReader(sourceSchemaReader);
            String query = "";
            TableParser tableParser = new TableParser();
            String line;
            while ((line = sourceSchemaBufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--"))
                    continue;
                if (line.endsWith(";")) {
                    query += line;
//                    System.out.println(query);
                    tableParser.parseFromQuery(query);
                    query = "";
                } else
                    query += line;
                    query += " ";
            }
            sourceSchemaBufferedReader.close();
            sourceSchemaReader.close();
            HashMap<String, DBTable> tables = tableParser.getTables();
            System.out.println("Table size: " + tables.size());
            //Print tables with attributs and details
            for (String table: tables.keySet()) {
                System.out.println("Table: " + table);
                HashMap<String, TableAttribute> attributes = tables.get(table).getAttributes();
                for (String attribute: attributes.keySet()) {
                    System.out.println("\tAttribute: " + attribute);
                    System.out.println("\tType: " + attributes.get(attribute).getType());
                    System.out.println("\tIs Not Null: " + attributes.get(attribute).isNotNull());
                    System.out.println("\tIs Unique: " + attributes.get(attribute).isUnique());
                    System.out.println("\tIs Primary Key: " + attributes.get(attribute).isPrimaryKey());
                    System.out.println("\tIs Foreign Key: " + attributes.get(attribute).isForeignKey());
                    System.out.println("\tForeign Key Table: " + attributes.get(attribute).getForeignKeyTable());
                    System.out.println("\tForeign Key Column: " + attributes.get(attribute).getForeignKeyColumn());
                    System.out.println("\tCheck Constraints: ");
                    for (AttributeCheckConstraint checkConstraint: attributes.get(attribute).getCheckConstraints()) {
                        System.out.println("\t\tAttribute Name: " + checkConstraint.getAttributeName());
                        System.out.println("\t\tCheck Type: " + checkConstraint.getCheckType());
                        System.out.println("\t\tValue: " + checkConstraint.getValue());
                    }
                }
            }

        }
        catch (FileNotFoundException e) {
            System.out.println("Schema file not found");
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}