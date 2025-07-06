package com.minis.beans.factory.config;

import java.util.*;

public class ArgumentValues {
    private final Map<Integer, ArgumentValue> indexedArgumentValues = new HashMap<>();
    private final List<ArgumentValue> genericArgumentValues = new ArrayList<>();
    private void addArgumentValue(Integer key, ArgumentValue argumentValue) {
        this.indexedArgumentValues.put(key, argumentValue);
    }
    public boolean hasIndexedArgumentValues(int index) {
        return this.indexedArgumentValues.containsKey(index);
    }
    public ArgumentValue getIndexedArgumentValue(int index) {
        return this.indexedArgumentValues.get(index);
    }
    public void addGenericArgumentValue(Object value, String type) {
        this.genericArgumentValues.add(new ArgumentValue(value, type));
    }
    private void addGenericArgumentValue(ArgumentValue newValue) {
        if (newValue.getName() != null) {
            for (Iterator<ArgumentValue> it = this.genericArgumentValues.iterator(); it.hasNext();) {
                ArgumentValue currentValue = it.next();
                if (newValue.getValue().equals(currentValue.getValue())) {
                    it.remove();
                }
            }
        }
        this.genericArgumentValues.add(newValue);
    }
    public ArgumentValue getGenericArgumentValue(String requiredName) {
        for (ArgumentValue argumentValue : this.genericArgumentValues) {
            if (argumentValue.getName() != null && (requiredName == null || !argumentValue.getName().equals(requiredName))) {
                continue;
            }
            return argumentValue;
        }
        return null;
    }
    public int getArgumentCount() {
        return this.genericArgumentValues.size();
    }
    public boolean isEmpty() {
        return this.genericArgumentValues.isEmpty();
    }
}
