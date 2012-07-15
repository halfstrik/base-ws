package org.eu.base.ws.query;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.eu.base.ws.query.entity.SimpleTableMethod;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.MultivaluedMap;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Strikki
 * Date: 6/20/12
 * Time: 11:09 AM
 */
public class QueryBuilderMethodTest {
    Connection connection;
    EntityManagerFactory emf;

    @Before
    public void initiate() {
        connection = DatabaseUtility.startup();

        emf = Persistence.createEntityManagerFactory("specTestHSQLDB");
        EntityManager em = emf.createEntityManager();
        SimpleTableMethod stm1 = new SimpleTableMethod();
        stm1.setName("the man");
        stm1.setAge(6);
        em.getTransaction().begin();
        em.persist(stm1);
        em.getTransaction().commit();
    }

    @Test
    public void methodTest() throws ParseException {
        EntityManager em = emf.createEntityManager();
        MultivaluedMap<String, String> inputQueryParams = new MultivaluedMapImpl();
        List<String> valueList1 = new ArrayList<String>();
        valueList1.add("the man");
        inputQueryParams.put("XmlName~EQ", valueList1);
        List<SimpleTableMethod> actual = QueryBuilder.getInstance(SimpleTableMethod.class).buildQuery(em, inputQueryParams).getResultList();

        TypedQuery<SimpleTableMethod> query = em.createQuery("from SimpleTableMethod s where s.name = 'the man'", SimpleTableMethod.class);
        List<SimpleTableMethod> expected = query.getResultList();

        assertEquals(expected, actual);
    }

    @Test
    public void overrideTest() throws ParseException {
        EntityManager em = emf.createEntityManager();
        MultivaluedMap<String, String> inputQueryParams = new MultivaluedMapImpl();
        List<String> valueList1 = new ArrayList<String>();
        valueList1.add("6");
        inputQueryParams.put("age~EQ", valueList1);
        List<SimpleTableMethod> actual = QueryBuilder.getInstance(SimpleTableMethod.class).buildQuery(em, inputQueryParams).getResultList();

        TypedQuery<SimpleTableMethod> query = em.createQuery("from SimpleTableMethod s where s.age = '6'", SimpleTableMethod.class);
        List<SimpleTableMethod> expected = query.getResultList();

        assertEquals(expected, actual);
    }
}
