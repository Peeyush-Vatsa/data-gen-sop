package com.sp.Table;

public class AttributeCheckConstraint {
    private String attributeName;
    private CheckType checkType;
    private int value;

    public AttributeCheckConstraint(String query) {
        parseConstraint(query);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public int getValue() {
        return value;
    }
    private void parseConstraint(String query) {
        String[] queryParts = query.split(" ");
        this.attributeName = queryParts[0];
        this.checkType = getCheckType(queryParts[1]);
        this.value = Integer.parseInt(queryParts[2]);
    }
    private CheckType getCheckType(String checkType) {
        if (checkType.equals(">")){
            return CheckType.GREATER_THAN;
        }
        else if(checkType.equals("<")){
            return CheckType.LESS_THAN;
        }
        else if(checkType.equals("=")){
            return CheckType.EQUAL_TO;
        }
        else if(checkType.equals(">=")){
            return CheckType.GREATER_THAN_OR_EQUAL_TO;
        }
        else if(checkType.equals("<=")){
            return CheckType.LESS_THAN_OR_EQUAL_TO;
        }
        else if(checkType.equals("!=")){
            return CheckType.NOT_EQUAL_TO;
        }
        return null;
    }
}
