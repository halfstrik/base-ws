package org.eu.base.ws;

import org.eu.base.ws.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.servlet.ServletContext;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;

/**
 * @author jump
 */
public class BaseService<T> {

    private static final Logger log = LoggerFactory.getLogger(BaseService.class);
    private final Class<T> type;

    public BaseService(Class<T> type) {
        this.type = type;
    }

    private Object getIdentity(Object obj) {
        try {
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.getAnnotation(Id.class) != null)
                    return f.get(obj);
            }
            for (Method m : obj.getClass().getMethods()) {
                if (m.getAnnotation(Id.class) != null)
                    return m.invoke(obj, (Object[]) null);
            }
            log.error("Failed to get identity from {}", obj.getClass());
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Failed to get identity from {} due to {}",
                    obj.getClass(), ex);
            return null;
        }
    }

    public List<T> list(ServletContext context, UriInfo uriInfo) throws ParseException {
        EntityManagerFactory emf = (EntityManagerFactory) context.getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        log.debug("EntityManager: {} class {}", em, type);
        try {
            return QueryBuilder.getInstance(type).buildQuery(em, uriInfo.getQueryParameters()).getResultList();
        } finally {

            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }

    public void updateList(ServletContext context, List<T> list) {
        EntityManagerFactory emf = (EntityManagerFactory) context.getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (T s : list) {
                Object id = getIdentity(s);
                if (id != null) {
                    if (em.find(s.getClass(), id) != null)
                        em.merge(s);
                    else
                        em.persist(s);
                }
            }
            em.getTransaction().commit();

        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }


    public void removeList(ServletContext context, List<T> list) {
        EntityManagerFactory emf = (EntityManagerFactory) context.getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (T s : list) {
                Object id = getIdentity(s);
                if (id != null) {
                    s = (T) em.find(s.getClass(), id);
                    if (s != null) em.remove(s);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }

    public T get(ServletContext context, UriInfo uriInfo) throws ParseException {
        EntityManagerFactory emf = (EntityManagerFactory) context.getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        try {
            return QueryBuilder.getInstance(type).buildQuery(em, uriInfo.getQueryParameters()).getSingleResult();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }

    public void update(ServletContext context, Object obj) {
        EntityManagerFactory emf = (EntityManagerFactory) context.getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Object id = getIdentity(obj);
            if (id != null) {
                if (em.find(obj.getClass(), id) != null)
                    em.merge(obj);
                else
                    em.persist(obj);
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }

    public void remove(ServletContext context, Object obj) {
        EntityManagerFactory emf = (EntityManagerFactory) context.getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Object id = getIdentity(obj);
            if (id != null) {
                if (em.find(obj.getClass(), id) != null)
                    em.remove(obj);
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }

    public Integer getAggregatedResult(ServletContext context, String property, String aggregate) {
        EntityManagerFactory emf = (EntityManagerFactory) context.getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        log.debug("EntityManager: {} class {}", em, type);
        try {
            throw new NotImplementedException();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }
}
