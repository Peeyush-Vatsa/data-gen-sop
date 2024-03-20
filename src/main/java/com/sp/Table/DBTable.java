package com.sp.Table;

import java.util.*;
public class DBTable {
    private String name;
    private HashMap<String, TableAttribute> attributes;

    public DBTable(String name, String query) {
        this.name = name;
        this.attributes = new HashMap<String, TableAttribute>();
        parseAttributes(query);
    }

    private void parseAttributes(String attributes) {
        String[] attributeList = attributes.split(",");
        for(String attribute : attributeList){
            String[] attributeParts = attribute.trim().split(" ");
            if (attributeParts[0].equalsIgnoreCase("constraint")){
                updateConstraint(attribute);
                continue;
            }
            String attributeName = attributeParts[0];
            String attributeType = attribute;
            TableAttribute newAttribute = new TableAttribute(attributeName, attributeType);
            this.attributes.put(attributeName, newAttribute);
        }
    }

    public String getName() {
        return name;
    }

    public HashMap<String, TableAttribute> getAttributes() {
        return attributes;
    }

    public void updateConstraint(String contraint){
        contraint = contraint.trim();
//        System.out.println(contraint);
        String[] constraintParts = contraint.split(" ");
        if (constraintParts[2].equalsIgnoreCase("foreign")){
            String attributeName = constraintParts[4].substring(1, constraintParts[4].length() - 1);
            TableAttribute attribute = this.attributes.get(attributeName);
            attribute.setIsForeignKey(true);
            String foreignKeyColumn = constraintParts[6].split("\\(")[1];
            if (foreignKeyColumn.endsWith(";")){
                foreignKeyColumn = foreignKeyColumn.substring(0, foreignKeyColumn.length() - 1);
            }
            foreignKeyColumn = foreignKeyColumn.substring(0, foreignKeyColumn.length() - 1);
            System.out.println(foreignKeyColumn);
            String foreignKeyTable = constraintParts[6].split("\\(")[0].trim();
            if (foreignKeyTable.contains(".")){
                foreignKeyTable = foreignKeyTable.split("\\.")[1];
            }
            if (foreignKeyTable.startsWith("\"")){
                foreignKeyTable = foreignKeyTable.substring(1, foreignKeyTable.length() - 1);
            }
            if (foreignKeyColumn.startsWith("\"")){
                foreignKeyColumn = foreignKeyColumn.substring(1, foreignKeyColumn.length() - 1);
            }
            attribute.setForeignKeyTable(foreignKeyTable);
            attribute.setForeignKeyColumn(foreignKeyColumn);
        }
        else if(constraintParts[2].equalsIgnoreCase("check")){
            String checkConstraint = constraintParts[3].substring(2) + " " + constraintParts[4] + " " + constraintParts[5].substring(0, constraintParts[5].length() - 2);
            String attributeName = checkConstraint.split(" ")[0];
            TableAttribute attribute = this.attributes.get(attributeName);
            attribute.addCheckConstraint(new AttributeCheckConstraint(checkConstraint));
        }
        else if(constraintParts[2].equalsIgnoreCase("primary")){
            String attributeName = constraintParts[4].substring(1, constraintParts[4].length() - 2);
            TableAttribute attribute = this.attributes.get(attributeName);
            attribute.setIsPrimaryKey(true);
        }
        else if(constraintParts[2].equalsIgnoreCase("unique")){
            String attributeNames = "";
            for (int i = 3; i < constraintParts.length; i++){
                attributeNames += constraintParts[i];
            }
            attributeNames = attributeNames.substring(1, attributeNames.length() - 2);
            for (String attributeName : attributeNames.split(",")){
                TableAttribute attribute = this.attributes.get(attributeName.trim());
                attribute.setIsUnique(true);
            }
        }
    }
}
