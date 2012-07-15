package org.eu.base.ws.query;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.eu.base.ws.query.entity.SimpleSubTable;
import org.eu.base.ws.query.entity.SimpleTable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MultivaluedMap;
import java.sql.Connection;
import java.text.ParseException;
import java.util.*;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Strikki
 * Date: 3/31/12
 * Time: 2:58 PM
 */
public class QueryBuilderTest {
    EntityManagerFactory emf;
    Connection connection;
    MultivaluedMap<String, String> inputQueryParams;

    final Integer MIN_INT = -10, MAX_INT = 10;
    final Double MIN_NUMB = -5.55, MAX_NUMB = 8.6;
    final Date NOW_DATE = new Date(), JAN_70 = new Date(1);

    @Before
    public void initiate() {
        connection = DatabaseUtility.startup();

        emf = Persistence.createEntityManagerFactory("specTestHSQLDB");
        EntityManager em = emf.createEntityManager();
        SimpleTable st1 = new SimpleTable();
        st1.setName("First");
        st1.setInteger(MIN_INT);
        st1.setDecimal(MIN_NUMB);
        SimpleSubTable sub1 = new SimpleSubTable();
        sub1.setName("subFirst");
        st1.setSimpleSubTable(sub1);
        SimpleTable st2 = new SimpleTable();
        st2.setName("Second");
        st2.setInteger(MAX_INT);
        st2.setDecimal(MAX_NUMB);
        em.getTransaction().begin();
        em.persist(st1);
        em.persist(st2);
        em.getTransaction().commit();
    }

    @Before
    public void prepareData() {
        inputQueryParams = new MultivaluedMapImpl();
        List<String> valueList1 = new ArrayList<String>();
        valueList1.add("First");
        inputQueryParams.put("fullname~EQ", valueList1);
        List<String> valueList2 = new ArrayList<String>();
        valueList2.add("subFirst");
        inputQueryParams.put("simpleSubTable.name~EQ", valueList2);
    }

    @After
    public void tearDown() {
        DatabaseUtility.shutdown(connection);
    }

    @Test
    public void init() throws NoSuchFieldException {
        QueryBuilder<SimpleTable> queryBuilder = new QueryBuilder<SimpleTable>(SimpleTable.class);
        queryBuilder.initiate();
        Map<String, PropertyInfo> actual = new HashMap<String, PropertyInfo>(queryBuilder.getJaxbFieldsByAliases());

        Map<String, PropertyInfo> expected = new HashMap<String, PropertyInfo>();
        expected.put("id", new PropertyInfo("id", Long.class));
        expected.put("fullname", new PropertyInfo("name", String.class));
        expected.put("integer", new PropertyInfo("integer", Integer.class));
        expected.put("decimal", new PropertyInfo("decimal", Double.class));
        expected.put("simpleSubTable.name", new PropertyInfo("simpleSubTable.name", String.class));

        assertEquals(expected, actual);
    }

    @Test
    public void parameterRawTest() {
        QueryBuilder<SimpleTable> queryBuilder = QueryBuilder.getInstance(SimpleTable.class);
        List<ParameterInfo> actual = queryBuilder.getParameterRawList(inputQueryParams);

        List<ParameterInfo> expected = new ArrayList<ParameterInfo>();
        expected.add(new ParameterInfo("simpleSubTable.name", ParameterInfo.Operation.EQ, "subFirst"));
        expected.add(new ParameterInfo("fullname", ParameterInfo.Operation.EQ, "First"));
        assertEquals(expected, actual);
    }

    @Test
    public void parameterTest() {
        QueryBuilder<SimpleTable> queryBuilder = QueryBuilder.getInstance(SimpleTable.class);
        List<ParameterInfo> actual = queryBuilder.getParameterList(inputQueryParams);

        List<ParameterInfo> expected = new ArrayList<ParameterInfo>();
        expected.add(new ParameterInfo("simpleSubTable.name", ParameterInfo.Operation.EQ, "subFirst"));
        expected.add(new ParameterInfo("fullname", ParameterInfo.Operation.EQ, "First"));
        assertEquals(expected, actual);
    }

    @Test
    public void predicateTest() throws ParseException {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Root<SimpleTable> root = cb.createQuery().from(SimpleTable.class);
        QueryBuilder<SimpleTable> queryBuilder = QueryBuilder.getInstance(SimpleTable.class);
        Predicate actual = queryBuilder.getPredicate(cb, root, "simpleSubTable.name", ParameterInfo.Operation.EQ, "value");
    }

    @Test
    public void predicateByNameTest() throws ParseException {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Root<SimpleTable> root = cb.createQuery().from(SimpleTable.class);
        QueryBuilder<SimpleTable> queryBuilder = QueryBuilder.getInstance(SimpleTable.class);
        List<ParameterInfo> actual = queryBuilder.getParameterList(inputQueryParams);
        Map<String, List<Predicate>> actualMap = queryBuilder.getPredicateByNameMap(cb, root, actual);
        Predicate result = queryBuilder.getPredicateFromMap(cb, actualMap);
    }

    @Test
    public void baselineTest() throws ParseException {
        EntityManager em = emf.createEntityManager();
        List<SimpleTable> actual = QueryBuilder.getInstance(SimpleTable.class).buildQuery(em, inputQueryParams).getResultList();

        TypedQuery<SimpleTable> query = em.createQuery("from SimpleTable s where simpleSubTable.name = 'subFirst' and name = 'First'", SimpleTable.class);
        List<SimpleTable> expected = query.getResultList();

        assertEquals(expected, actual);
    }

    @Test
    public void emptyParamsTest() throws ParseException {
        EntityManager em = emf.createEntityManager();
        List<SimpleTable> actual = QueryBuilder.getInstance(SimpleTable.class).buildQuery(em, new MultivaluedMapImpl()).getResultList();

        TypedQuery<SimpleTable> query = em.createQuery("from SimpleTable s", SimpleTable.class);
        List<SimpleTable> expected = query.getResultList();

        assertEquals(expected, actual);
    }

    @Test
    public void numericWhereTest() throws ParseException {
        EntityManager em = emf.createEntityManager();
        MultivaluedMap<String, String> inputQueryParams = new MultivaluedMapImpl();
        List<String> valueList1 = new ArrayList<String>();
        valueList1.add("1");
        inputQueryParams.put("integer~LE", valueList1);
        List<SimpleTable> actual = QueryBuilder.getInstance(SimpleTable.class).buildQuery(em, inputQueryParams).getResultList();

        TypedQuery<SimpleTable> query = em.createQuery("from SimpleTable s where s.integer <= 1", SimpleTable.class);
        List<SimpleTable> expected = query.getResultList();

        assertEquals(expected, actual);
    }

    @Test
    public void aggregationTest() throws ParseException {
        EntityManager em = emf.createEntityManager();
        MultivaluedMap<String, String> inputQueryParams = new MultivaluedMapImpl();
        inputQueryParams.put("integer~MAX", new ArrayList<String>());
        Object actual = QueryBuilder.getInstance(SimpleTable.class).buildQuery(em, inputQueryParams).getSingleResult();


        em = emf.createEntityManager();
        TypedQuery<Integer> query = em.createQuery("select max(s.integer) from SimpleTable s", Integer.class);
        Integer expected = query.getSingleResult();

        assertEquals(expected.doubleValue(), actual);

        TypedQuery<Double> queryDouble = em.createQuery("select max(s.decimal) from SimpleTable s", Double.class);
        Double expectedDouble = queryDouble.getSingleResult();

        inputQueryParams.clear();
        inputQueryParams.put("decimal~MAX", new ArrayList<String>());
        Object actualDouble = QueryBuilder.getInstance(SimpleTable.class).buildQuery(em, inputQueryParams).getSingleResult();

        assertEquals(expectedDouble, actualDouble);
    }
}
