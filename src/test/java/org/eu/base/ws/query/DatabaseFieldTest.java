package org.eu.base.ws.query;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DatabaseFieldTest {
    @Test
    public void testValueOf() throws Exception {
        DatabaseField relativeChild = new DatabaseField("child", Object.class);
        DatabaseField absoluteChild = DatabaseField.valueOf("parent", relativeChild);

        assertEquals("parent.child", absoluteChild.getName());
        assertEquals(Object.class, absoluteChild.getType());

    }
}
