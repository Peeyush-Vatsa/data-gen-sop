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
        contraint = contraint.toLowerCase();
        String[] constraintParts = contraint.split(" ");
        if (constraintParts[2].equalsIgnoreCase("foreign")){
            String attributeName = constraintParts[3].substring(1, constraintParts[3].length() - 1);
            TableAttribute attribute = this.attributes.get(attributeName);
            attribute.setIsForeignKey(true);
            attribute.setForeignKeyTable(constraintParts[5].split("\\(")[0].trim());
            attribute.setForeignKeyColumn(constraintParts[5].split("\\(")[1].substring(0, constraintParts[6].length() - 1));
        }
        else if(constraintParts[2].equalsIgnoreCase("check")){
            String checkConstraint = constraintParts[3].substring(2, constraintParts[3].length() - 2);
            String attributeName = checkConstraint.split(" ")[0];
            TableAttribute attribute = this.attributes.get(attributeName);
            attribute.addCheckConstraint(new AttributeCheckConstraint(checkConstraint));
        }
        else if(constraintParts[2].equalsIgnoreCase("primary")){
            String attributeName = constraintParts[4].substring(1, constraintParts[4].length() - 1);
            TableAttribute attribute = this.attributes.get(attributeName);
            attribute.setIsPrimaryKey(true);
        }
        else if(constraintParts[2].equalsIgnoreCase("unique")){
            String attributeNames = constraintParts[4].substring(1, constraintParts[4].length() - 1);
            for (String attributeName : attributeNames.split(",")){
                TableAttribute attribute = this.attributes.get(attributeName.trim());
                attribute.setIsUnique(true);
            }
        }
    }
}
