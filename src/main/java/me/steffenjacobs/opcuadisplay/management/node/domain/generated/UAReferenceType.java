//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.05.05 um 05:23:03 PM CEST 
//


package me.steffenjacobs.opcuadisplay.management.node.domain.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für UAReferenceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="UAReferenceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UAType">
 *       &lt;sequence>
 *         &lt;element name="InverseName" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}LocalizedText" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Symmetric" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UAReferenceType", propOrder = {
    "inverseName"
})
public class UAReferenceType
    extends UAType
{

    @XmlElement(name = "InverseName")
    protected List<LocalizedText> inverseName;
    @XmlAttribute(name = "Symmetric")
    protected Boolean symmetric;

    /**
     * Gets the value of the inverseName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inverseName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInverseName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocalizedText }
     * 
     * 
     */
    public List<LocalizedText> getInverseName() {
        if (inverseName == null) {
            inverseName = new ArrayList<LocalizedText>();
        }
        return this.inverseName;
    }

    /**
     * Ruft den Wert der symmetric-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSymmetric() {
        if (symmetric == null) {
            return false;
        } else {
            return symmetric;
        }
    }

    /**
     * Legt den Wert der symmetric-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSymmetric(Boolean value) {
        this.symmetric = value;
    }

}
