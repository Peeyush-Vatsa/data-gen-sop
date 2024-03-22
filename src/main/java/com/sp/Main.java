package com.sp;

import com.sp.Records.RecordGenerator;
import com.sp.Table.DBTable;
import com.sp.TableParser.TableParser;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        if (args.length != 3) {
            System.out.println("Insufficient/excess arguments provided. Required: 3");
            return;
        }

        String databaseCredentialFile = args[0];
        String sourceSchema = args[1];
        int noOfRecords = Integer.parseInt(args[2]);

        // Opening the database connection
        DriverManager.registerDriver(new org.postgresql.Driver());
        BufferedReader dbCredentialsReader = new BufferedReader(new FileReader(databaseCredentialFile));
        String url = "";
        String user = "";
        String password = "";
        String line;
        while ((line = dbCredentialsReader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty())
                continue;
            String[] parts = line.split("=");
            if (parts[0].equalsIgnoreCase("jdbcUrl"))
                url = parts[1];
            else if (parts[0].equalsIgnoreCase("user"))
                user = parts[1];
            else if (parts[0].equalsIgnoreCase("password"))
                password = parts[1];
        }
        dbCredentialsReader.close();
        Connection connection;
        System.out.println(url);
        if (user.isEmpty() || password.isEmpty())
            connection = DriverManager.getConnection(url);
        else
            connection = DriverManager.getConnection(url, user, password);
        System.out.println("Database connection established");
        HashMap<String, DBTable> tables;
        // Reading the schema file
        try {
            FileReader sourceSchemaReader = new FileReader(sourceSchema);
            BufferedReader sourceSchemaBufferedReader = new BufferedReader(sourceSchemaReader);
            Statement statement = connection.createStatement();
            String query = "";
            TableParser tableParser = new TableParser();
            while ((line = sourceSchemaBufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--"))
                    continue;
                if (line.endsWith(";")) {
                    query += line;
                    statement.execute(query);
                    tableParser.parseFromQuery(query);
                    query = "";
                } else
                    query += line;
                    query += " ";
            }
            sourceSchemaBufferedReader.close();
            sourceSchemaReader.close();
            statement.close();
            System.out.println("Schema file read successfully\nTables successfully created");
            tables = tableParser.getTables();
        }
        catch (FileNotFoundException e) {
            System.out.println("Schema file not found");
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (tables == null) {
            System.out.println("No tables found in the schema file");
            return;
        }
        System.out.println("Generating records...");
        RecordGenerator recordGenerator = new RecordGenerator(tables, noOfRecords);
        HashMap<String, String> sqlQueries = recordGenerator.getSqlQueries();
        Statement statement = connection.createStatement();
        ArrayList<String> pending = new ArrayList<String>();
        System.out.println("Inserting records...");
        for (String table: sqlQueries.keySet()) {
            try {
                statement.execute(sqlQueries.get(table));
                System.out.println("Records inserted into " + table);
            }
            catch (org.postgresql.util.PSQLException e) {
                pending.add(sqlQueries.get(table));
            }
        }
        while (!pending.isEmpty()){
            for (int i = 0; i < pending.size(); i++){
                String query = pending.get(i);
                try {
//                    System.out.println("Executing: " + query);
                    statement.execute(query);
                    pending.remove(i);
                    System.out.println("Records inserted for " + query.split(" ")[2]);
                }
                catch (org.postgresql.util.PSQLException e) {
//                    System.out.println("Failed to execute: " + query);
                }
            }
        }
        statement.close();
        connection.close();
        System.out.println("Records successfully inserted");

    }
}