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
 * <p>Java-Klasse für DataTypeField complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DataTypeField">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Description" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}LocalizedText" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Definition" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}DataTypeDefinition" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="SymbolicName" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}SymbolicName" />
 *       &lt;attribute name="DataType" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodeId" default="i=24" />
 *       &lt;attribute name="ValueRank" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}ValueRank" default="-1" />
 *       &lt;attribute name="Value" type="{http://www.w3.org/2001/XMLSchema}int" default="-1" />
 *       &lt;attribute name="IsOptional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataTypeField", propOrder = {
    "description",
    "definition"
})
public class DataTypeField {

    @XmlElement(name = "Description")
    protected List<LocalizedText> description;
    @XmlElement(name = "Definition")
    protected DataTypeDefinition definition;
    @XmlAttribute(name = "Name", required = true)
    protected String name;
    @XmlAttribute(name = "SymbolicName")
    protected List<String> symbolicName;
    @XmlAttribute(name = "DataType")
    protected String dataType;
    @XmlAttribute(name = "ValueRank")
    protected Integer valueRank;
    @XmlAttribute(name = "Value")
    protected Integer value;
    @XmlAttribute(name = "IsOptional")
    protected Boolean isOptional;

    /**
     * Gets the value of the description property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocalizedText }
     * 
     * 
     */
    public List<LocalizedText> getDescription() {
        if (description == null) {
            description = new ArrayList<LocalizedText>();
        }
        return this.description;
    }

    /**
     * Ruft den Wert der definition-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DataTypeDefinition }
     *     
     */
    public DataTypeDefinition getDefinition() {
        return definition;
    }

    /**
     * Legt den Wert der definition-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DataTypeDefinition }
     *     
     */
    public void setDefinition(DataTypeDefinition value) {
        this.definition = value;
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
     * Ruft den Wert der dataType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataType() {
        if (dataType == null) {
            return "i=24";
        } else {
            return dataType;
        }
    }

    /**
     * Legt den Wert der dataType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataType(String value) {
        this.dataType = value;
    }

    /**
     * Ruft den Wert der valueRank-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getValueRank() {
        if (valueRank == null) {
            return -1;
        } else {
            return valueRank;
        }
    }

    /**
     * Legt den Wert der valueRank-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setValueRank(Integer value) {
        this.valueRank = value;
    }

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getValue() {
        if (value == null) {
            return -1;
        } else {
            return value;
        }
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Ruft den Wert der isOptional-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsOptional() {
        if (isOptional == null) {
            return false;
        } else {
            return isOptional;
        }
    }

    /**
     * Legt den Wert der isOptional-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsOptional(Boolean value) {
        this.isOptional = value;
    }

}
