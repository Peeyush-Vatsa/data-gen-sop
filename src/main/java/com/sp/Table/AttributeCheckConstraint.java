package com.sp.Table;

public class AttributeCheckConstraint {
    private String attributeName;
    private CheckType checkType;
    private int value;

    public AttributeCheckConstraint(String attributeName, CheckType checkType, int value) {
        this.attributeName = attributeName;
        this.checkType = checkType;
        this.value = value;
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
}
