package com.sp.Table;

public class TableAttribute{
    private String name;
    private String type;
    private int charLength = 0;
    private boolean isPrimaryKey;
    private boolean isForeignKey;
    private String foreignKeyTable;
    private String foreignKeyColumn;

    public TableAttribute(String name, String type, boolean isPrimaryKey, boolean isForeignKey, String foreignKeyTable, String foreignKeyColumn) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
        this.isForeignKey = isForeignKey;
        this.foreignKeyTable = foreignKeyTable;
        this.foreignKeyColumn = foreignKeyColumn;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isForeignKey() {
        return isForeignKey;
    }

    public String getForeignKeyTable() {
        return foreignKeyTable;
    }

    public String getForeignKeyColumn() {
        return foreignKeyColumn;
    }
}
