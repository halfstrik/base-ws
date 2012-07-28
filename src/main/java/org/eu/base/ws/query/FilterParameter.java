package org.eu.base.ws.query;

class FilterParameter {
    enum Operation {EQ, NEQ, GT, LT, GE, LE, LIKE}

    final static private Integer PARAMETER_NAME_POSITION = 0;
    final static private Integer PARAMETER_OPERATION_POSITION = 1;
    private String name;
    private Operation operation;
    private String value;

    FilterParameter(String name, Operation operation, String value) {
        this.name = name;
        this.operation = operation;
        this.value = value;
    }

    public static boolean isParameter(String nameAndOperation, String value) {
        if (nameAndOperation == null || nameAndOperation.split("~").length != 2 || value == null)
            return false;

        String name = nameAndOperation.split("~")[PARAMETER_NAME_POSITION];
        String operation = nameAndOperation.split("~")[PARAMETER_OPERATION_POSITION];

        if (name.isEmpty())
            return false;

        for (Operation c : Operation.values()) {
            if (c.name().equals(operation)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Standard format for query parameters in URL string:
     * http://host/addr?name~operand=value&...
     *
     * @param nameAndOperation e.g. field1~EQ
     * @param value            e.g. 100
     * @return FilterParameter instance
     */
    public static FilterParameter valueOf(String nameAndOperation, String value) {
        String name = nameAndOperation.split("~")[PARAMETER_NAME_POSITION];
        Operation operation = Operation.valueOf(nameAndOperation.split("~")[PARAMETER_OPERATION_POSITION]);
        return new FilterParameter(name, operation, value);
    }

    public String getName() {
        return name;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FilterParameter: name '" + name + "' " +
                "operand '" + operation + "' " +
                "value '" + value + "'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterParameter that = (FilterParameter) o;

        if (!name.equals(that.name))
            return false;
        if (operation != that.operation)
            return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + operation.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
