package org.eu.base.ws.query;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Strikki
 * Date: 6/20/12
 * Time: 12:09 PM
 */
public class BeanUtilsTest {
    @Test
    public void getBeanUtilsTest() {
        Assert.assertEquals("firstName", BeanUtils.getPropertyNameByMethodName("getFirstName"));
    }

    @Test
    public void isBeanUtilsTest() {
        Assert.assertEquals("hotName", BeanUtils.getPropertyNameByMethodName("isHotName"));
    }

}
