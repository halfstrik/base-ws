package org.eu.base.ws.query;

import org.eu.base.ws.XmlOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class QueryBuilder<T> {
    private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);
    private static Map<Class<?>, Object> instances = new HashMap<Class<?>, Object>();
    private final Class<T> type;
    // Idea: Change to Map<String, Path> - seems it's not necessary.
    private Map<String, DatabaseField> jaxbFieldsByAliases = new HashMap<String, DatabaseField>();


    protected QueryBuilder(Class<T> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public static <T> QueryBuilder<T> getInstance(Class<T> type) {
        if (instances.containsKey(type)) {
            return (QueryBuilder<T>) instances.get(type);
        } else {
            QueryBuilder<T> queryBuilder = new QueryBuilder<T>(type);
            queryBuilder.initiate();
            instances.put(type, queryBuilder);
            return queryBuilder;
        }
    }

    // Idea : Consider to use @Column name annotation | Used only as marker, no internal meaning for us!
    protected void initiate() {
        for (Field f : type.getDeclaredFields()) {
            if (f.getAnnotation(XmlAttribute.class) != null && f.getAnnotation(Column.class) != null) {
                XmlAttribute attribute = f.getAnnotation(XmlAttribute.class);
                String xmlName = attribute.name().isEmpty() || attribute.name().equals("##default") ? f.getName() : attribute.name();
                jaxbFieldsByAliases.put(xmlName, new DatabaseField(f.getName(), f.getType()));
            }
            if (f.getAnnotation(XmlElement.class) != null && f.getAnnotation(Transient.class) == null) {
                XmlElement element = f.getAnnotation(XmlElement.class);
                String xmlName = element.name().isEmpty() || element.name().equals("##default") ? f.getName() : element.name();
                for (Map.Entry<String, DatabaseField> inner : QueryBuilder.getInstance(f.getType()).getJaxbFieldsByAliases().entrySet()) {
                    jaxbFieldsByAliases.put(xmlName + '.' + inner.getKey(), DatabaseField.valueOf(f.getName(), inner.getValue()));
                }
            }
        }
        for (Method m : type.getDeclaredMethods()) {
            if (m.getAnnotation(XmlAttribute.class) != null && m.getAnnotation(Column.class) != null) {
                XmlAttribute attribute = m.getAnnotation(XmlAttribute.class);
                String dbName = BeanUtils.getPropertyNameByMethodName(m.getName());
                String xmlName = attribute.name().isEmpty() || attribute.name().equals("##default") ? dbName : attribute.name();
                jaxbFieldsByAliases.put(xmlName, new DatabaseField(dbName, m.getReturnType()));
            }
            if (m.getAnnotation(XmlElement.class) != null && m.getAnnotation(Transient.class) == null) {
                XmlElement element = m.getAnnotation(XmlElement.class);
                String dbName = BeanUtils.getPropertyNameByMethodName(m.getName());
                String xmlName = element.name().isEmpty() || element.name().equals("##default") ? dbName : element.name();
                jaxbFieldsByAliases.put(xmlName, new DatabaseField(dbName, m.getReturnType()));
                for (Map.Entry<String, DatabaseField> inner : QueryBuilder.getInstance(m.getReturnType()).getJaxbFieldsByAliases().entrySet()) {
                    jaxbFieldsByAliases.put(xmlName + '.' + inner.getKey(), DatabaseField.valueOf(dbName, inner.getValue()));
                }
            }
            if (m.getAnnotation(XmlOverride.class) != null && m.getAnnotation(XmlTransient.class) != null) {
                XmlOverride override = m.getAnnotation(XmlOverride.class);
                String dbName = BeanUtils.getPropertyNameByMethodName(m.getName());
                String xmlName = override.name().isEmpty() ? dbName : override.name();
                jaxbFieldsByAliases.put(xmlName, new DatabaseField(dbName, m.getReturnType()));
                for (Map.Entry<String, DatabaseField> inner : QueryBuilder.getInstance(m.getReturnType()).getJaxbFieldsByAliases().entrySet()) {
                    jaxbFieldsByAliases.put(xmlName + '.' + inner.getKey(), DatabaseField.valueOf(dbName, inner.getValue()));
                }
            }
        }
        log.info("jaxb annotated methods & fields list {} for {}", jaxbFieldsByAliases, type);
    }

    protected List<FilterParameter> getParameterList(MultivaluedMap<String, String> params) {
        List<FilterParameter> list = getParameterRawList(params);
        return getParameterExistingList(list);
    }

    protected List<FilterParameter> getParameterRawList(MultivaluedMap<String, String> params) {
        List<FilterParameter> list = new ArrayList<FilterParameter>();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            for (String value : entry.getValue()) {
                if (FilterParameter.isParameter(entry.getKey(), value)) {
                    list.add(FilterParameter.valueOf(entry.getKey(), value));
                }
            }
        }
        return list;
    }

    protected List<FilterParameter> getParameterExistingList(List<FilterParameter> params) {
        for (FilterParameter param : new ArrayList<FilterParameter>(params)) {
            if (!jaxbFieldsByAliases.containsKey(param.getName())) {
                log.debug("jaxb annotated property {} not found on class {}", param.getName(), type);
                params.remove(param);
            }
        }
        return params;
    }

    protected Map<String, DatabaseField> getJaxbFieldsByAliases() {
        return Collections.unmodifiableMap(jaxbFieldsByAliases);
    }

    private Predicate getPredicate(CriteriaBuilder cb, Root<T> root, FilterParameter param) throws ParseException {
        return getPredicate(cb, root, param.getName(), param.getOperation(), param.getValue());
    }

    private Path<T> getPath(Root<T> root, String name) {
        Path<T> path = null;
        for (String subName : name.split("[.\n]")) {
            if (path == null) {
                path = root.get(subName);
            } else {
                path = path.get(subName);
            }
        }
        return path;
    }

    @SuppressWarnings("unchecked")
    protected Predicate getPredicate(CriteriaBuilder cb, Root<T> root, String name,
                                     FilterParameter.Operation operation, String value) throws ParseException {
        Path<T> path = getPath(root, jaxbFieldsByAliases.get(name).getName());
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        switch (operation) {
            case EQ:
                return cb.equal(path.as(jaxbFieldsByAliases.get(name).getType()), value);
            case NEQ:
                return cb.notEqual(path.as(jaxbFieldsByAliases.get(name).getType()), value);
            case LIKE:
                return cb.like(path.as((Class<String>) jaxbFieldsByAliases.get(name).getType()), value);
            case GT:
                return cb.gt(path.as((Class<? extends Number>) jaxbFieldsByAliases.get(name).getType()), format.parse(value));
            case LT:
                return cb.lt(path.as((Class<? extends Number>) jaxbFieldsByAliases.get(name).getType()), format.parse(value));
            case GE:
                return cb.ge(path.as((Class<? extends Number>) jaxbFieldsByAliases.get(name).getType()), format.parse(value));
            case LE:
                return cb.le(path.as((Class<? extends Number>) jaxbFieldsByAliases.get(name).getType()), format.parse(value));
            default:
                throw new AssertionError("Attention! No one operation matches!");
        }
    }

    protected Map<String, List<Predicate>> getPredicateByNameMap(CriteriaBuilder cb, Root<T> root, List<FilterParameter> params) throws ParseException {
        Map<String, List<Predicate>> result = new HashMap<String, List<Predicate>>();
        for (FilterParameter param : params) {
            if (!result.containsKey(param.getName())) {
                result.put(param.getName(), new ArrayList<Predicate>());
            }
            Predicate predicate = getPredicate(cb, root, param);
            result.get(param.getName()).add(predicate);
        }
        return result;
    }

    protected Predicate getPredicateFromMap(CriteriaBuilder cb, Map<String, List<Predicate>> map) {
        List<Predicate> predicateOrList = new ArrayList<Predicate>();
        for (List<Predicate> entry : map.values()) {
            predicateOrList.add(entry.size() > 1 ?
                    cb.or((Predicate[]) entry.toArray(new Predicate[entry.size()])) : entry.get(0));
        }
        if (predicateOrList.size() > 1) {
            return cb.and((Predicate[]) predicateOrList.toArray(new Predicate[predicateOrList.size()]));
        } else if (predicateOrList.size() == 1) {
            return predicateOrList.get(0);
        } else {
            return null;
        }
    }

    protected List<AggregationParameter> getExpressionInfoList(MultivaluedMap<String, String> params) {
        List<AggregationParameter> list = getExpressionInfoRawList(params);
        return getExpressionInfoExistingList(list);
    }

    protected List<AggregationParameter> getExpressionInfoRawList(MultivaluedMap<String, String> params) {
        List<AggregationParameter> list = new ArrayList<AggregationParameter>();
        for (String entry : params.keySet()) {
            if (AggregationParameter.isParameter(entry)) {
                list.add(AggregationParameter.valueOf(entry));
            }
        }
        return list;
    }

    protected List<AggregationParameter> getExpressionInfoExistingList(List<AggregationParameter> expressions) {
        for (AggregationParameter expr : new ArrayList<AggregationParameter>(expressions)) {
            if (!jaxbFieldsByAliases.containsKey(expr.getName())) {
                log.debug("jaxb annotated property {} not found on class {}", expr.getName(), type);
                expressions.remove(expr);
            }
        }
        return expressions;
    }

    protected Expression<? extends Number> getExpression(CriteriaBuilder cb, Root<T> root, AggregationParameter aggregationParameter) {
        return getExpression(cb, root, aggregationParameter.getName(), aggregationParameter.getOperation());
    }

    // It is possible to provide a necessary type as a parameter. Now we hardcoded Double type - it's work at least for Integer and Double.
    protected Expression<? extends Number> getExpression(CriteriaBuilder cb, Root<T> root, String name,
                                                         AggregationParameter.Operation operation) {
        Path<T> path = getPath(root, jaxbFieldsByAliases.get(name).getName());
        switch (operation) {
            case MAX:
                return cb.max(path.as(Double.class));
            case MIN:
                return cb.min(path.as(Double.class));
            case AVG:
                return cb.avg(path.as(Double.class));
            default:
                throw new AssertionError("Attention! No one operation matches!");
        }
    }

// Now we support only one aggregation expression per query - if need more - do like in this example:
//    protected List<Expression> getExpressionList(CriteriaBuilder cb, Root<T> root, List<AggregationParameter> expressions) {
//        List<Expression> list = new ArrayList<Expression>();
//        for(AggregationParameter ei : expressions){
//            list.add(getExpression(cb,root,ei));
//        }
//        return list;
//    }

    public TypedQuery<T> buildQuery(EntityManager entityManager, MultivaluedMap<String, String> paramsStr) throws ParseException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(type);
        Root<T> root = criteriaQuery.from(type);

//        List<AggregationParameter> eiList = getExpressionInfoList(paramsStr);
//        if (eiList.size() > 0) {
//            Expression<? extends Number> e = getExpression(cb, root, eiList.get(0));
//            criteriaQuery.select(e);
//        }

        List<FilterParameter> params = getParameterList(paramsStr);
        Map<String, List<Predicate>> map = getPredicateByNameMap(cb, root, params);
        Predicate wherePredicate = getPredicateFromMap(cb, map);
        if (wherePredicate != null) {
            criteriaQuery.where(wherePredicate);
        }
        return entityManager.createQuery(criteriaQuery);
    }
}

