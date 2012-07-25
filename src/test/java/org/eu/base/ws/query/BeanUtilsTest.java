package org.eu.base.ws.query;

import org.junit.Assert;
import org.junit.Test;

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
