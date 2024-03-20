package com.sp.Records;

import com.sp.Table.DataType;

import java.util.ArrayList;

public class ForeignKeyValues {
    private String foreignTableName;
    private String foreignAttributeName;
    private ArrayList<String> values;
    private DataType type;

    public ForeignKeyValues(String foreignTableName, String foreignAttributeName, DataType type) {
        this.foreignTableName = foreignTableName;
        this.foreignAttributeName = foreignAttributeName;
        this.type = type;
        this.values = new ArrayList<String>();
    }
}
