package com.sp.Table;

import java.util.ArrayList;

public class TableAttribute{
    private String name;
    private DataType type;
    private int charLength = 0;
    private boolean isNotNull;
    private boolean isUnique;
    private boolean isPrimaryKey;
    private boolean isForeignKey;
    private String foreignKeyTable;
    private String foreignKeyColumn;
    private ArrayList<AttributeCheckConstraint> checkConstraints;

    public TableAttribute(String name, String attribute) {
        this.name = name;
        this.checkConstraints = new ArrayList<AttributeCheckConstraint>();
        parseAttribute(attribute);
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
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

    private void parseAttribute(String attribute) {
        attribute = attribute.trim();
        String attribute1 = attribute.toLowerCase();
        String[] attributeParts = attribute.split(" ");
        if (attributeParts[0].equalsIgnoreCase("Constraint")){
            return;
        }
        this.name = attributeParts[0];
        String typeStr = attributeParts[1].toLowerCase();
        if (typeStr.equalsIgnoreCase("character")){
            if (!attribute1.contains("varying")) {
                this.charLength = Integer.parseInt(attributeParts[1].substring(attributeParts[2].indexOf("(") + 1, attributeParts[2].indexOf(")")));
            }
        }
        if (typeStr.equalsIgnoreCase("timestamp")){
            if (attributeParts[2].equalsIgnoreCase("with")){
                this.type = DataType.TIMESTAMP_WITH_TIMEZONE;
            }
            else if (attributeParts[2].equalsIgnoreCase("without")){
                this.type = DataType.TIMESTAMP_WITHOUT_TIMEZONE;
            }
        }
        else if (attribute1.contains("character varying")){
            this.type = DataType.CHARACTER_VARYING;
            this.charLength = Integer.parseInt(attributeParts[2].substring(attributeParts[2].indexOf("(") + 1, attributeParts[2].indexOf(")")));
        }
        else {
            this.type = matchDataType(typeStr);
        }

        if (attribute1.contains("primary key")){
            this.isPrimaryKey = true;
        }
        else{
            this.isPrimaryKey = false;
        }
        if (attribute1.contains("references")){
            this.isForeignKey = true;
            // Get the index of 'references'
            int foreignKey = attribute.indexOf("references") + 1;
            String[] foreignKeyParts = attributeParts[foreignKey].split("(");
            this.foreignKeyTable = foreignKeyParts[0];
            this.foreignKeyColumn = foreignKeyParts[1].substring(0, foreignKeyParts[1].length() - 1);
        }
        else{
            this.isForeignKey = false;
        }
        if (attribute1.contains("not null")){
            this.isNotNull = true;
        }
        else{
            this.isNotNull = false;
        }
        if (attribute1.contains("unique")){
            this.isUnique = true;
        }
        else{
            this.isUnique = false;
        }
        if (attribute1.contains("check")){
            String checkConstraint = attribute.substring(attribute.indexOf("check") + 5);
            addCheckConstraint(new AttributeCheckConstraint(checkConstraint));
        }
    }

    private DataType matchDataType(String type){
        type = type.toLowerCase();
        if (type.equals("integer")){
            return DataType.INTEGER;
        }
        else if (type.equals("smallint")){
            return DataType.SMALLINT;
        }
        else if (type.equals("bigint")){
            return DataType.BIGINT;
        }
        else if (type.equals("date")){
            return DataType.DATE;
        }
        else if (type.equals("character")){
            return DataType.CHARACTER;
        }
        else if (type.equals("timestamp with time zone")){
            return DataType.TIMESTAMP_WITH_TIMEZONE;
        }
        else if (type.equals("text")){
            return DataType.TEXT;
        }
        else if (type.equals("timestamp without time zone")){
            return DataType.TIMESTAMP_WITHOUT_TIMEZONE;
        }
        else if (type.equals("double")){
            return DataType.DOUBLE_PRECISION;
        }
        else if (type.equals("boolean")){
            return DataType.BOOLEAN;
        }
        else if (type.equals("uuid")){
            return DataType.UUID;
        }
        else if (type.equals("jsonb")){
            return DataType.JSONB;
        }
        else {
            return DataType.UNKNOWN;
        }

    }

    public void addCheckConstraint(AttributeCheckConstraint constraint){
        this.checkConstraints.add(constraint);
    }
    public void setIsUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }
    public void setIsNotNull(boolean isNotNull) {
        this.isNotNull = isNotNull;
    }
    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }
    public void setIsForeignKey(boolean isForeignKey) {
        this.isForeignKey = isForeignKey;
    }
    public void setForeignKeyTable(String foreignKeyTable) {
        this.foreignKeyTable = foreignKeyTable;
    }
    public void setForeignKeyColumn(String foreignKeyColumn) {
        this.foreignKeyColumn = foreignKeyColumn;
    }

    public ArrayList<AttributeCheckConstraint> getCheckConstraints() {
        return checkConstraints;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public boolean isUnique() {
        return isUnique;
    }
}
