package com.minis.beans.factory.config;

import java.util.*;

public class ConstructorArgumentValues {
    private final List<ConstructorArgumentValue> constructorArgumentValueList = new ArrayList<>();
    public void addArgumentValue(ConstructorArgumentValue newValue) {
        this.constructorArgumentValueList.add(newValue);
    }

    public ConstructorArgumentValue getIndexedArgumentValue(int index) {
        return this.constructorArgumentValueList.get(index);
    }
    public int getArgumentCount() {
        return this.constructorArgumentValueList.size();
    }
    public boolean isEmpty() {
        return this.constructorArgumentValueList.isEmpty();
    }
}
