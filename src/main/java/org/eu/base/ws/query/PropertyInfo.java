package org.eu.base.ws.query;

public class PropertyInfo {
    String name;
    Class<?> type;

    public PropertyInfo(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public static PropertyInfo valueOf(String parent, PropertyInfo propertyInfo) {
        return new PropertyInfo(parent + "." + propertyInfo.name, propertyInfo.type);
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public String toString() {
        return "PropertyInfo{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyInfo)) return false;

        PropertyInfo propertyInfo = (PropertyInfo) o;

        return name.equals(propertyInfo.name) && type.equals(propertyInfo.type);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
