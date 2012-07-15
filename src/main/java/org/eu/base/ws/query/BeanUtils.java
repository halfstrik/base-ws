package org.eu.base.ws.query;

/**
 * Created with IntelliJ IDEA.
 * User: Strikki
 * Date: 6/20/12
 * Time: 11:48 AM
 */
public class BeanUtils {
    /**
     * Gets property name from getter name by cutting leading "get" or "is" and lowering the first letter of the rest
     *
     * @param methodName Getter name (e.g. "getFirstName" or "isHot")
     * @return Property name (e.g. "firstName" or "hot")
     */
    public static String getPropertyNameByMethodName(String methodName) {
        String propertyName = methodName.replaceFirst("^(get|is)", "");
        return propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
    }
}
