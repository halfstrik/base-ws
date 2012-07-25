package org.eu.base.ws.query;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ParameterInfoTest {
    @Test
    public void creation() {
        String nameAndOper = "field1~EQ";
        String value = "first";
        ParameterInfo actual = ParameterInfo.valueOf(nameAndOper, value);

        ParameterInfo expected = new ParameterInfo("field1", ParameterInfo.Operation.EQ, "first");

        assertEquals(expected, actual);
    }
}
