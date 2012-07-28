package org.eu.base.ws.query;

import org.junit.Test;

import static junit.framework.Assert.*;

public class FilterParameterTest {
    @Test
    public void testCreation() {
        String nameAndOper = "field1~EQ";
        String value = "first";
        assertTrue(FilterParameter.isParameter(nameAndOper, value));
        FilterParameter actual = FilterParameter.valueOf(nameAndOper, value);

        FilterParameter expected = new FilterParameter("field1", FilterParameter.Operation.EQ, "first");

        assertEquals(expected, actual);
    }

    @Test
    public void testIsParameter() {
        assertTrue(FilterParameter.isParameter("field1~EQ", "ok"));
        assertFalse(FilterParameter.isParameter("field1~eq", "ok"));
        assertFalse(FilterParameter.isParameter("field1~d2", "ok"));
        assertFalse(FilterParameter.isParameter("field1~", "ok"));
        assertFalse(FilterParameter.isParameter("~EQ", "ok"));
        assertFalse(FilterParameter.isParameter("field1~EQ~false", "ok"));
        assertFalse(FilterParameter.isParameter("", "ok"));
        assertFalse(FilterParameter.isParameter(null, "ok"));

        assertFalse(FilterParameter.isParameter("field1~EQ", null));
    }
}
