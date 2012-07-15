package org.eu.base.ws.query.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by IntelliJ IDEA.
 * User: Strikki
 * Date: 2/6/12
 * Time: 12:45 PM
 */
@Entity
public class SimpleTable {
    @Id
    @GeneratedValue
    @XmlAttribute
    @Column
    Long id;
    @XmlAttribute(name = "fullname")
    @Column(name = "dbname")
    String name;
    @XmlAttribute
    @Column
    Integer integer;
    @XmlAttribute
    @Column
    Double decimal;
    @XmlElement
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "simpleSubTableFK")
    private SimpleSubTable simpleSubTable;

    public SimpleSubTable getSimpleSubTable() {
        return simpleSubTable;
    }

    public void setSimpleSubTable(SimpleSubTable simpleSubTable) {
        this.simpleSubTable = simpleSubTable;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public Double getDecimal() {
        return decimal;
    }

    public void setDecimal(Double decimal) {
        this.decimal = decimal;
    }
}
