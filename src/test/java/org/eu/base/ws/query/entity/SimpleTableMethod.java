package org.eu.base.ws.query.entity;

import org.eu.base.ws.XmlOverride;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class SimpleTableMethod {
    Integer identifier;
    String name1;
    Integer age1;


    @Id
    @GeneratedValue
    @XmlAttribute(name = "XmlIdentifier")
    @Column
    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public void setName(String name) {
        this.name1 = name;
    }

    @Column
    @XmlElement(name = "xmlname")
    public String getName() {
        return name1;
    }

    @Column
    @XmlTransient
    @XmlOverride(name = "age")
    public Integer getAge() {
        return age1;
    }

    public void setAge(Integer age1) {
        this.age1 = age1;
    }

    @XmlElement(name = "age")
    @Transient
    public String getWordAge() {
        return "word" + String.valueOf(age1);
    }
}
