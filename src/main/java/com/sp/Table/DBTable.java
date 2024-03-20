package com.sp.Table;

import java.util.*;
public class DBTable {
    private String name;
    private HashMap<String, TableAttribute> attributes;

    public DBTable(String name, HashMap<String, TableAttribute> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, TableAttribute> getAttributes() {
        return attributes;
    }
}
