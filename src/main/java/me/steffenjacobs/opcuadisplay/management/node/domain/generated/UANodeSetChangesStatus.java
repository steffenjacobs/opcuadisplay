//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.05.05 um 05:23:03 PM CEST 
//


package me.steffenjacobs.opcuadisplay.management.node.domain.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="NodesToAdd" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodeSetStatusList" minOccurs="0"/>
 *         &lt;element name="ReferencesToAdd" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodeSetStatusList" minOccurs="0"/>
 *         &lt;element name="NodesToDelete" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodeSetStatusList" minOccurs="0"/>
 *         &lt;element name="ReferencesToDelete" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodeSetStatusList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="LastModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="TransactionId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "nodesToAdd",
    "referencesToAdd",
    "nodesToDelete",
    "referencesToDelete"
})
@XmlRootElement(name = "UANodeSetChangesStatus")
public class UANodeSetChangesStatus {

    @XmlElement(name = "NodesToAdd")
    protected NodeSetStatusList nodesToAdd;
    @XmlElement(name = "ReferencesToAdd")
    protected NodeSetStatusList referencesToAdd;
    @XmlElement(name = "NodesToDelete")
    protected NodeSetStatusList nodesToDelete;
    @XmlElement(name = "ReferencesToDelete")
    protected NodeSetStatusList referencesToDelete;
    @XmlAttribute(name = "Version")
    protected String version;
    @XmlAttribute(name = "LastModified")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastModified;
    @XmlAttribute(name = "TransactionId", required = true)
    protected String transactionId;

    /**
     * Ruft den Wert der nodesToAdd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NodeSetStatusList }
     *     
     */
    public NodeSetStatusList getNodesToAdd() {
        return nodesToAdd;
    }

    /**
     * Legt den Wert der nodesToAdd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeSetStatusList }
     *     
     */
    public void setNodesToAdd(NodeSetStatusList value) {
        this.nodesToAdd = value;
    }

    /**
     * Ruft den Wert der referencesToAdd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NodeSetStatusList }
     *     
     */
    public NodeSetStatusList getReferencesToAdd() {
        return referencesToAdd;
    }

    /**
     * Legt den Wert der referencesToAdd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeSetStatusList }
     *     
     */
    public void setReferencesToAdd(NodeSetStatusList value) {
        this.referencesToAdd = value;
    }

    /**
     * Ruft den Wert der nodesToDelete-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NodeSetStatusList }
     *     
     */
    public NodeSetStatusList getNodesToDelete() {
        return nodesToDelete;
    }

    /**
     * Legt den Wert der nodesToDelete-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeSetStatusList }
     *     
     */
    public void setNodesToDelete(NodeSetStatusList value) {
        this.nodesToDelete = value;
    }

    /**
     * Ruft den Wert der referencesToDelete-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NodeSetStatusList }
     *     
     */
    public NodeSetStatusList getReferencesToDelete() {
        return referencesToDelete;
    }

    /**
     * Legt den Wert der referencesToDelete-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeSetStatusList }
     *     
     */
    public void setReferencesToDelete(NodeSetStatusList value) {
        this.referencesToDelete = value;
    }

    /**
     * Ruft den Wert der version-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Legt den Wert der version-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Ruft den Wert der lastModified-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastModified() {
        return lastModified;
    }

    /**
     * Legt den Wert der lastModified-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastModified(XMLGregorianCalendar value) {
        this.lastModified = value;
    }

    /**
     * Ruft den Wert der transactionId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Legt den Wert der transactionId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

}
