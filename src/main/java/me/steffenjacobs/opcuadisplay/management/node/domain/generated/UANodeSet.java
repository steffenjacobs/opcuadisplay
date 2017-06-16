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
import javax.xml.bind.annotation.XmlElements;
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
 *         &lt;element name="NamespaceUris" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UriTable" minOccurs="0"/>
 *         &lt;element name="ServerUris" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UriTable" minOccurs="0"/>
 *         &lt;element name="Models" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}ModelTable" minOccurs="0"/>
 *         &lt;element name="Aliases" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}AliasTable" minOccurs="0"/>
 *         &lt;element name="Extensions" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}ListOfExtensions" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="UAObject" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UAObject"/>
 *           &lt;element name="UAVariable" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UAVariable"/>
 *           &lt;element name="UAMethod" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UAMethod"/>
 *           &lt;element name="UAView" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UAView"/>
 *           &lt;element name="UAObjectType" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UAObjectType"/>
 *           &lt;element name="UAVariableType" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UAVariableType"/>
 *           &lt;element name="UADataType" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UADataType"/>
 *           &lt;element name="UAReferenceType" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UAReferenceType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="LastModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "namespaceUris",
    "serverUris",
    "models",
    "aliases",
    "extensions",
    "uaObjectOrUAVariableOrUAMethod"
})
@XmlRootElement(name = "UANodeSet")
public class UANodeSet {

    @XmlElement(name = "NamespaceUris")
    protected UriTable namespaceUris;
    @XmlElement(name = "ServerUris")
    protected UriTable serverUris;
    @XmlElement(name = "Models")
    protected ModelTable models;
    @XmlElement(name = "Aliases")
    protected AliasTable aliases;
    @XmlElement(name = "Extensions")
    protected ListOfExtensions extensions;
    @XmlElements({
        @XmlElement(name = "UAObject", type = UAObject.class),
        @XmlElement(name = "UAVariable", type = UAVariable.class),
        @XmlElement(name = "UAMethod", type = UAMethod.class),
        @XmlElement(name = "UAView", type = UAView.class),
        @XmlElement(name = "UAObjectType", type = UAObjectType.class),
        @XmlElement(name = "UAVariableType", type = UAVariableType.class),
        @XmlElement(name = "UADataType", type = UADataType.class),
        @XmlElement(name = "UAReferenceType", type = UAReferenceType.class)
    })
    protected List<UANode> uaObjectOrUAVariableOrUAMethod;
    @XmlAttribute(name = "LastModified")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastModified;

    /**
     * Ruft den Wert der namespaceUris-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link UriTable }
     *     
     */
    public UriTable getNamespaceUris() {
        return namespaceUris;
    }

    /**
     * Legt den Wert der namespaceUris-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link UriTable }
     *     
     */
    public void setNamespaceUris(UriTable value) {
        this.namespaceUris = value;
    }

    /**
     * Ruft den Wert der serverUris-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link UriTable }
     *     
     */
    public UriTable getServerUris() {
        return serverUris;
    }

    /**
     * Legt den Wert der serverUris-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link UriTable }
     *     
     */
    public void setServerUris(UriTable value) {
        this.serverUris = value;
    }

    /**
     * Ruft den Wert der models-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ModelTable }
     *     
     */
    public ModelTable getModels() {
        return models;
    }

    /**
     * Legt den Wert der models-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ModelTable }
     *     
     */
    public void setModels(ModelTable value) {
        this.models = value;
    }

    /**
     * Ruft den Wert der aliases-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AliasTable }
     *     
     */
    public AliasTable getAliases() {
        return aliases;
    }

    /**
     * Legt den Wert der aliases-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AliasTable }
     *     
     */
    public void setAliases(AliasTable value) {
        this.aliases = value;
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
     * Gets the value of the uaObjectOrUAVariableOrUAMethod property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uaObjectOrUAVariableOrUAMethod property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUAObjectOrUAVariableOrUAMethod().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UAObject }
     * {@link UAVariable }
     * {@link UAMethod }
     * {@link UAView }
     * {@link UAObjectType }
     * {@link UAVariableType }
     * {@link UADataType }
     * {@link UAReferenceType }
     * 
     * 
     */
    public List<UANode> getUAObjectOrUAVariableOrUAMethod() {
        if (uaObjectOrUAVariableOrUAMethod == null) {
            uaObjectOrUAVariableOrUAMethod = new ArrayList<UANode>();
        }
        return this.uaObjectOrUAVariableOrUAMethod;
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

}
