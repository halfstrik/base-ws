package org.eu.base.ws.query;

/**
 * Created by IntelliJ IDEA.
 * User: Strikki
 * Date: 3/16/12
 * Time: 11:43 PM
 */
public class ParameterInfo {
    public enum Operation {EQ, NEQ, GT, LT, GE, LE, LIKE}

    private String name;
    private Operation operation;
    private String value;

    public ParameterInfo(String name, Operation operation, String value) {
        this.name = name;
        this.operation = operation;
        this.value = value;
    }

    public static boolean isParameter(String nameAndOperation) {
        String operation = nameAndOperation.split("~")[1];
        return operation.equals("EQ") || operation.equals("NEQ") || operation.equals("GT") || operation.equals("LT") ||
                operation.equals("GE") || operation.equals("LE") || operation.equals("LIKE");
    }

    /**
     * Standard format for query parameters in URL string:
     * http://host/addr?name~operand=value&...
     *
     * @param nameAndOperation e.g. field1~EQ
     * @param value            e.g. 100
     * @return ParameterInfo instance
     */
    public static ParameterInfo valueOf(String nameAndOperation, String value) {
        String name = nameAndOperation.split("~")[0];
        Operation operation = Operation.valueOf(nameAndOperation.split("~")[1]);
        return new ParameterInfo(name, operation, value);
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
        return "ParameterInfo: name '" + name + "' " +
                "operand '" + operation + "' " +
                "value '" + value + "'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterInfo)) return false;

        ParameterInfo parameterInfo = (ParameterInfo) o;

        if (!name.equals(parameterInfo.name)) return false;
        if (operation != parameterInfo.operation) return false;
        if (value != null ? !value.equals(parameterInfo.value) : parameterInfo.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + operation.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
