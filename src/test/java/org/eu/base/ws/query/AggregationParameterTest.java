package org.eu.base.ws.query;

import org.junit.Test;

import static junit.framework.Assert.*;

public class AggregationParameterTest {
    @Test
    public void testCreation() {
        String nameAndOper = "field1~MAX";
        assertTrue(AggregationParameter.isParameter(nameAndOper));
        AggregationParameter actual = AggregationParameter.valueOf(nameAndOper);

        AggregationParameter expected = new AggregationParameter("field1", AggregationParameter.Operation.MAX);

        assertEquals(expected, actual);
    }

    @Test
    public void testIsParameter() {
        assertTrue(AggregationParameter.isParameter("field1~MAX"));
        assertFalse(AggregationParameter.isParameter("field1~max"));
        assertFalse(AggregationParameter.isParameter("field1~d2"));
        assertFalse(AggregationParameter.isParameter("field1~"));
        assertFalse(AggregationParameter.isParameter("~EQ"));
        assertFalse(AggregationParameter.isParameter("field1~MAX~false"));
        assertFalse(AggregationParameter.isParameter(""));
        assertFalse(AggregationParameter.isParameter(null));
    }


}
