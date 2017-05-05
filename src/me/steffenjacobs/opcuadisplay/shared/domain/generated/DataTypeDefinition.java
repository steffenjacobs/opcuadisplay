//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.05.05 um 05:23:03 PM CEST 
//


package me.steffenjacobs.opcuadisplay.shared.domain.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DataTypeDefinition complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DataTypeDefinition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Field" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}DataTypeField" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Name" use="required" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}QualifiedName" />
 *       &lt;attribute name="BaseType" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}QualifiedName" default="" />
 *       &lt;attribute name="SymbolicName" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}SymbolicName" default="" />
 *       &lt;attribute name="IsUnion" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataTypeDefinition", propOrder = {
    "field"
})
public class DataTypeDefinition {

    @XmlElement(name = "Field")
    protected List<DataTypeField> field;
    @XmlAttribute(name = "Name", required = true)
    protected String name;
    @XmlAttribute(name = "BaseType")
    protected String baseType;
    @XmlAttribute(name = "SymbolicName")
    protected List<String> symbolicName;
    @XmlAttribute(name = "IsUnion")
    protected Boolean isUnion;

    /**
     * Gets the value of the field property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the field property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataTypeField }
     * 
     * 
     */
    public List<DataTypeField> getField() {
        if (field == null) {
            field = new ArrayList<DataTypeField>();
        }
        return this.field;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der baseType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseType() {
        if (baseType == null) {
            return "";
        } else {
            return baseType;
        }
    }

    /**
     * Legt den Wert der baseType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseType(String value) {
        this.baseType = value;
    }

    /**
     * Gets the value of the symbolicName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the symbolicName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSymbolicName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSymbolicName() {
        if (symbolicName == null) {
            symbolicName = new ArrayList<String>();
        }
        return this.symbolicName;
    }

    /**
     * Ruft den Wert der isUnion-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsUnion() {
        if (isUnion == null) {
            return false;
        } else {
            return isUnion;
        }
    }

    /**
     * Legt den Wert der isUnion-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsUnion(Boolean value) {
        this.isUnion = value;
    }

}
