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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für UANode complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="UANode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DisplayName" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}LocalizedText" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}LocalizedText" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Category" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Documentation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="References" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}ListOfReferences" minOccurs="0"/>
 *         &lt;element name="Extensions" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}ListOfExtensions" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="NodeId" use="required" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodeId" />
 *       &lt;attribute name="BrowseName" use="required" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}QualifiedName" />
 *       &lt;attribute name="WriteMask" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}WriteMask" default="0" />
 *       &lt;attribute name="UserWriteMask" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}WriteMask" default="0" />
 *       &lt;attribute name="SymbolicName" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}SymbolicName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UANode", propOrder = {
    "displayName",
    "description",
    "category",
    "documentation",
    "references",
    "extensions"
})
@XmlSeeAlso({
    UAType.class,
    UAInstance.class
})
public class UANode {

    @XmlElement(name = "DisplayName")
    protected List<LocalizedText> displayName;
    @XmlElement(name = "Description")
    protected List<LocalizedText> description;
    @XmlElement(name = "Category")
    protected List<String> category;
    @XmlElement(name = "Documentation")
    protected String documentation;
    @XmlElement(name = "References")
    protected ListOfReferences references;
    @XmlElement(name = "Extensions")
    protected ListOfExtensions extensions;
    @XmlAttribute(name = "NodeId", required = true)
    protected String nodeId;
    @XmlAttribute(name = "BrowseName", required = true)
    protected String browseName;
    @XmlAttribute(name = "WriteMask")
    protected Long writeMask;
    @XmlAttribute(name = "UserWriteMask")
    protected Long userWriteMask;
    @XmlAttribute(name = "SymbolicName")
    protected List<String> symbolicName;

    /**
     * Gets the value of the displayName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the displayName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDisplayName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocalizedText }
     * 
     * 
     */
    public List<LocalizedText> getDisplayName() {
        if (displayName == null) {
            displayName = new ArrayList<LocalizedText>();
        }
        return this.displayName;
    }

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
     * Gets the value of the category property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the category property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCategory() {
        if (category == null) {
            category = new ArrayList<String>();
        }
        return this.category;
    }

    /**
     * Ruft den Wert der documentation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Legt den Wert der documentation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentation(String value) {
        this.documentation = value;
    }

    /**
     * Ruft den Wert der references-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ListOfReferences }
     *     
     */
    public ListOfReferences getReferences() {
        return references;
    }

    /**
     * Legt den Wert der references-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfReferences }
     *     
     */
    public void setReferences(ListOfReferences value) {
        this.references = value;
    }

    /**
     * Ruft den Wert der extensions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ListOfExtensions }
     *     
     */
    public ListOfExtensions getExtensions() {
        return extensions;
    }

    /**
     * Legt den Wert der extensions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfExtensions }
     *     
     */
    public void setExtensions(ListOfExtensions value) {
        this.extensions = value;
    }

    /**
     * Ruft den Wert der nodeId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Legt den Wert der nodeId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeId(String value) {
        this.nodeId = value;
    }

    /**
     * Ruft den Wert der browseName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrowseName() {
        return browseName;
    }

    /**
     * Legt den Wert der browseName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrowseName(String value) {
        this.browseName = value;
    }

    /**
     * Ruft den Wert der writeMask-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public long getWriteMask() {
        if (writeMask == null) {
            return  0L;
        } else {
            return writeMask;
        }
    }

    /**
     * Legt den Wert der writeMask-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setWriteMask(Long value) {
        this.writeMask = value;
    }

    /**
     * Ruft den Wert der userWriteMask-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public long getUserWriteMask() {
        if (userWriteMask == null) {
            return  0L;
        } else {
            return userWriteMask;
        }
    }

    /**
     * Legt den Wert der userWriteMask-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUserWriteMask(Long value) {
        this.userWriteMask = value;
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

}
