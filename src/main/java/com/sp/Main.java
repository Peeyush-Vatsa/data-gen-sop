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