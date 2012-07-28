package org.eu.base.ws.query;

class DatabaseField {
    String name;
    Class<?> type;

    public DatabaseField(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public static DatabaseField valueOf(String parent, DatabaseField databaseField) {
        return new DatabaseField(parent + "." + databaseField.getName(), databaseField.getType());
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public String toString() {
        return "DatabaseField{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatabaseField)) return false;

        DatabaseField databaseField = (DatabaseField) o;

        return name.equals(databaseField.name) && type.equals(databaseField.type);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
