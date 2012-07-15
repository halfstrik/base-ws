package org.eu.base.ws.query;

/**
 * Created with IntelliJ IDEA.
 * User: Strikki
 * Date: 4/23/12
 * Time: 1:52 AM
 */
public class ExpressionInfo {
    public enum Operation {MAX, MIN, AVG}

    private String name;
    private Operation operation;

    public ExpressionInfo(String name, Operation operation) {
        this.name = name;
        this.operation = operation;
    }

    public static boolean isExpression(String nameAndOperation) {
        String operation = nameAndOperation.split("~")[1];
        return operation.equals("MAX") || operation.equals("MIN") || operation.equals("AVG");
    }

    /**
     * Standard format for query parameters in URL string:
     * http://host/addr?name~operand=value&...
     *
     * @param nameAndOperation e.g. field1~AVG
     *                         //@param value       e.g. none (in case of expression)
     * @return ExpressionInfo instance
     */
    public static ExpressionInfo valueOf(String nameAndOperation) {
        String name = nameAndOperation.split("~")[0];
        Operation operation = Operation.valueOf(nameAndOperation.split("~")[1]);
        return new ExpressionInfo(name, operation);
    }

    public String getName() {
        return name;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return "ExpressionInfo{" +
                "name='" + name + '\'' +
                ", operation=" + operation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpressionInfo)) return false;

        ExpressionInfo that = (ExpressionInfo) o;

        if (!name.equals(that.name)) return false;
        if (operation != that.operation) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + operation.hashCode();
        return result;
    }
}
