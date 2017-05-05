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
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java-Klasse für UAVariable complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="UAVariable">
 *   &lt;complexContent>
 *     &lt;extension base="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UAInstance">
 *       &lt;sequence>
 *         &lt;element name="Value" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax' minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Translation" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}TranslationType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="DataType" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodeId" default="i=24" />
 *       &lt;attribute name="ValueRank" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}ValueRank" default="-1" />
 *       &lt;attribute name="ArrayDimensions" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}ArrayDimensions" default="" />
 *       &lt;attribute name="AccessLevel" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}AccessLevel" default="1" />
 *       &lt;attribute name="UserAccessLevel" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}AccessLevel" default="1" />
 *       &lt;attribute name="MinimumSamplingInterval" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}Duration" default="0" />
 *       &lt;attribute name="Historizing" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UAVariable", propOrder = {
    "value",
    "translation"
})
public class UAVariable
    extends UAInstance
{

    @XmlElement(name = "Value")
    protected UAVariable.Value value;
    @XmlElement(name = "Translation")
    protected List<TranslationType> translation;
    @XmlAttribute(name = "DataType")
    protected String dataType;
    @XmlAttribute(name = "ValueRank")
    protected Integer valueRank;
    @XmlAttribute(name = "ArrayDimensions")
    protected List<String> arrayDimensions;
    @XmlAttribute(name = "AccessLevel")
    protected Short accessLevel;
    @XmlAttribute(name = "UserAccessLevel")
    protected Short userAccessLevel;
    @XmlAttribute(name = "MinimumSamplingInterval")
    protected Double minimumSamplingInterval;
    @XmlAttribute(name = "Historizing")
    protected Boolean historizing;

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link UAVariable.Value }
     *     
     */
    public UAVariable.Value getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link UAVariable.Value }
     *     
     */
    public void setValue(UAVariable.Value value) {
        this.value = value;
    }

    /**
     * Gets the value of the translation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the translation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTranslation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TranslationType }
     * 
     * 
     */
    public List<TranslationType> getTranslation() {
        if (translation == null) {
            translation = new ArrayList<TranslationType>();
        }
        return this.translation;
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
     * Gets the value of the arrayDimensions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arrayDimensions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArrayDimensions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getArrayDimensions() {
        if (arrayDimensions == null) {
            arrayDimensions = new ArrayList<String>();
        }
        return this.arrayDimensions;
    }

    /**
     * Ruft den Wert der accessLevel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public short getAccessLevel() {
        if (accessLevel == null) {
            return ((short) 1);
        } else {
            return accessLevel;
        }
    }

    /**
     * Legt den Wert der accessLevel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setAccessLevel(Short value) {
        this.accessLevel = value;
    }

    /**
     * Ruft den Wert der userAccessLevel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public short getUserAccessLevel() {
        if (userAccessLevel == null) {
            return ((short) 1);
        } else {
            return userAccessLevel;
        }
    }

    /**
     * Legt den Wert der userAccessLevel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setUserAccessLevel(Short value) {
        this.userAccessLevel = value;
    }

    /**
     * Ruft den Wert der minimumSamplingInterval-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getMinimumSamplingInterval() {
        if (minimumSamplingInterval == null) {
            return  0.0D;
        } else {
            return minimumSamplingInterval;
        }
    }

    /**
     * Legt den Wert der minimumSamplingInterval-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinimumSamplingInterval(Double value) {
        this.minimumSamplingInterval = value;
    }

    /**
     * Ruft den Wert der historizing-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isHistorizing() {
        if (historizing == null) {
            return false;
        } else {
            return historizing;
        }
    }

    /**
     * Legt den Wert der historizing-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHistorizing(Boolean value) {
        this.historizing = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;any processContents='lax' minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class Value {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Ruft den Wert der any-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Element }
         *     {@link Object }
         *     
         */
        public Object getAny() {
            return any;
        }

        /**
         * Legt den Wert der any-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Element }
         *     {@link Object }
         *     
         */
        public void setAny(Object value) {
            this.any = value;
        }

    }

}
