package org.eu.base.ws.query;

class AggregationParameter {
    enum Operation {MAX, MIN, AVG}

    final static private Integer PARAMETER_NAME_POSITION = 0;
    final static private Integer PARAMETER_OPERATION_POSITION = 1;
    private String name;
    private Operation operation;

    AggregationParameter(String name, Operation operation) {
        this.name = name;
        this.operation = operation;
    }

    public static boolean isParameter(String nameAndOperation) {
        if (nameAndOperation == null || nameAndOperation.split("~").length != 2)
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
     * @param nameAndOperation e.g. field1~AVG
     *                         //@param value       e.g. none (in case of expression)
     * @return AggregationParameter instance
     */
    public static AggregationParameter valueOf(String nameAndOperation) {
        String name = nameAndOperation.split("~")[PARAMETER_NAME_POSITION];
        Operation operation = Operation.valueOf(nameAndOperation.split("~")[PARAMETER_OPERATION_POSITION]);
        return new AggregationParameter(name, operation);
    }

    public String getName() {
        return name;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return "AggregationParameter{" +
                "name='" + name + '\'' +
                ", operation=" + operation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregationParameter)) return false;

        AggregationParameter that = (AggregationParameter) o;

        return name.equals(that.name) && operation == that.operation;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + operation.hashCode();
        return result;
    }
}
