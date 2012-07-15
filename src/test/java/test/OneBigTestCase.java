package test;

import org.junit.Before;
import org.junit.Test;
import test.entity.SimpleTable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Strikki
 * Date: 2/6/12
 * Time: 11:11 PM
 */
public class OneBigTestCase {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("specTestPostgres");

    final Integer MIN_INT = -10, MAX_INT = 10;
    final Number MIN_NUMB = -5.55, MAX_NUMB = 8.6;
    final Date NOW_DATE = new Date(), JAN_70 = new Date(1);

    @Before
    public void initiate() {
        EntityManager em = emf.createEntityManager();
        SimpleTable st1 = new SimpleTable();
        st1.setName("First");
        st1.setInteger(MIN_INT);
        st1.setNumber(MIN_NUMB);
        st1.setDate(JAN_70);
        SimpleTable st2 = new SimpleTable();
        st2.setName("Second");
        st2.setInteger(MAX_INT);
        st2.setNumber(MAX_NUMB);
        st2.setDate(NOW_DATE);
        em.getTransaction().begin();
        em.persist(st1);
        em.persist(st2);
        em.getTransaction().commit();
    }

    @Test
    public void selectionTest() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<SimpleTable> query = em.createQuery("from test.entity.SimpleTable s", SimpleTable.class);
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
        TypedQuery<Integer> query = em.createQuery("select min(s.integer) from test.entity.SimpleTable s", Integer.class);
        Integer actualMin = query.getSingleResult();
        assertEquals(MIN_INT, actualMin);
    }
}
