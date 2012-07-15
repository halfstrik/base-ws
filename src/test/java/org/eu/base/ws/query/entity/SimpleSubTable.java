package org.eu.base.ws.query.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: Strikki
 * Date: 2/6/12
 * Time: 12:45 PM
 */
@Entity
public class SimpleSubTable {
    @Id
    @GeneratedValue
    private Long idInternalUseOnly;
    @XmlAttribute
    @Column
    String name;

    public Long getIdInternalUseOnly() {
        return idInternalUseOnly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
