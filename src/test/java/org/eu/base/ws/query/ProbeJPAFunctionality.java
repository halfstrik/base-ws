package org.eu.base.ws.query;

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
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProbeJPAFunctionality {
    EntityManagerFactory emf;
    Connection connection;

    final Integer MIN_INT = -10, MAX_INT = 10;
    final Number MIN_NUMB = -5.55, MAX_NUMB = 8.6;
    final Date NOW_DATE = new Date(), JAN_70 = new Date(1);

    @Before
    public void initiate() {
        connection = DatabaseUtility.startup();

        emf = Persistence.createEntityManagerFactory("specTestHSQLDB");
        EntityManager em = emf.createEntityManager();
        SimpleTable st1 = new SimpleTable();
        st1.setName("First");
        st1.setInteger(MIN_INT);
        SimpleSubTable sub1 = new SimpleSubTable();
        sub1.setName("subFirst");
        st1.setSimpleSubTable(sub1);
        SimpleTable st2 = new SimpleTable();
        st2.setName("Second");
        st2.setInteger(MAX_INT);
        em.getTransaction().begin();
        em.persist(st1);
        em.persist(st2);
        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        DatabaseUtility.shutdown(connection);
    }

    @Test
    public void selectionTest() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<SimpleTable> query = em.createQuery("from SimpleTable s", SimpleTable.class);
        List<SimpleTable> list = query.getResultList();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
        Root<SimpleTable> from = criteriaQuery.from(SimpleTable.class);
        CriteriaQuery<Object> select = criteriaQuery.select(from);
        TypedQuery<Object> typedQuery = em.createQuery(select);
        List<Object> resultList = typedQuery.getResultList();
        assertEquals(list, resultList);
    }

    @Test
    public void aggregationTest() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Integer> query = em.createQuery("select min(s.integer) from SimpleTable s", Integer.class);
        Integer expectedMin = query.getSingleResult();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
        Root<SimpleTable> from = criteriaQuery.from(SimpleTable.class);
        criteriaQuery.select(criteriaBuilder.min(from.get("integer").as(Integer.class)));
        Number actualMin = em.createQuery(criteriaQuery).getSingleResult();

        assertEquals(expectedMin, actualMin);
    }

    @Test
    public void nestedSelectTest() {
        EntityManager em = emf.createEntityManager();

        TypedQuery<SimpleTable> query1 = em.createQuery("from SimpleTable s where s.simpleSubTable.name = 'subFirst'", SimpleTable.class);
        List<SimpleTable> expected = query1.getResultList();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<SimpleTable> criteriaQuery = criteriaBuilder.createQuery(SimpleTable.class);
        Root<SimpleTable> from = criteriaQuery.from(SimpleTable.class);
        Predicate predicate = criteriaBuilder.equal(from.get("simpleSubTable").get("name").as(String.class), "subFirst");
        CriteriaQuery<SimpleTable> query = criteriaQuery.select(from).where(predicate);
        List<SimpleTable> actual = em.createQuery(query).getResultList();

        assertEquals(expected, actual);
    }

    @Test
    public void numberSelectionTest() {
        EntityManager em = emf.createEntityManager();

        TypedQuery<SimpleTable> query1 = em.createQuery("from SimpleTable s where s.integer <= '1'", SimpleTable.class);
        List<SimpleTable> expected = query1.getResultList();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<SimpleTable> criteriaQuery = criteriaBuilder.createQuery(SimpleTable.class);
        Root<SimpleTable> from = criteriaQuery.from(SimpleTable.class);
        Predicate predicate = criteriaBuilder.le(from.get("integer").as(Integer.class), 1);
        CriteriaQuery<SimpleTable> query = criteriaQuery.select(from).where(predicate);
        List<SimpleTable> actual = em.createQuery(query).getResultList();

        assertEquals(expected, actual);
    }
}
