//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.05.05 um 05:23:03 PM CEST 
//


package me.steffenjacobs.opcuadisplay.shared.domain.generated;

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
 *         &lt;element name="NamespaceUris" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UriTable" minOccurs="0"/>
 *         &lt;element name="ServerUris" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UriTable" minOccurs="0"/>
 *         &lt;element name="Aliases" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}AliasTable" minOccurs="0"/>
 *         &lt;element name="Extensions" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}ListOfExtensions" minOccurs="0"/>
 *         &lt;element name="NodesToAdd" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodesToAdd" minOccurs="0"/>
 *         &lt;element name="ReferencesToAdd" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}ReferencesToChange" minOccurs="0"/>
 *         &lt;element name="NodesToDelete" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodesToDelete" minOccurs="0"/>
 *         &lt;element name="ReferencesToDelete" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}ReferencesToChange" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="LastModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="TransactionId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="AcceptAllOrNothing" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
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
    "aliases",
    "extensions",
    "nodesToAdd",
    "referencesToAdd",
    "nodesToDelete",
    "referencesToDelete"
})
@XmlRootElement(name = "UANodeSetChanges")
public class UANodeSetChanges {

    @XmlElement(name = "NamespaceUris")
    protected UriTable namespaceUris;
    @XmlElement(name = "ServerUris")
    protected UriTable serverUris;
    @XmlElement(name = "Aliases")
    protected AliasTable aliases;
    @XmlElement(name = "Extensions")
    protected ListOfExtensions extensions;
    @XmlElement(name = "NodesToAdd")
    protected NodesToAdd nodesToAdd;
    @XmlElement(name = "ReferencesToAdd")
    protected ReferencesToChange referencesToAdd;
    @XmlElement(name = "NodesToDelete")
    protected NodesToDelete nodesToDelete;
    @XmlElement(name = "ReferencesToDelete")
    protected ReferencesToChange referencesToDelete;
    @XmlAttribute(name = "Version")
    protected String version;
    @XmlAttribute(name = "LastModified")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastModified;
    @XmlAttribute(name = "TransactionId", required = true)
    protected String transactionId;
    @XmlAttribute(name = "AcceptAllOrNothing")
    protected Boolean acceptAllOrNothing;

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
     * Ruft den Wert der nodesToAdd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NodesToAdd }
     *     
     */
    public NodesToAdd getNodesToAdd() {
        return nodesToAdd;
    }

    /**
     * Legt den Wert der nodesToAdd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NodesToAdd }
     *     
     */
    public void setNodesToAdd(NodesToAdd value) {
        this.nodesToAdd = value;
    }

    /**
     * Ruft den Wert der referencesToAdd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReferencesToChange }
     *     
     */
    public ReferencesToChange getReferencesToAdd() {
        return referencesToAdd;
    }

    /**
     * Legt den Wert der referencesToAdd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferencesToChange }
     *     
     */
    public void setReferencesToAdd(ReferencesToChange value) {
        this.referencesToAdd = value;
    }

    /**
     * Ruft den Wert der nodesToDelete-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NodesToDelete }
     *     
     */
    public NodesToDelete getNodesToDelete() {
        return nodesToDelete;
    }

    /**
     * Legt den Wert der nodesToDelete-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NodesToDelete }
     *     
     */
    public void setNodesToDelete(NodesToDelete value) {
        this.nodesToDelete = value;
    }

    /**
     * Ruft den Wert der referencesToDelete-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReferencesToChange }
     *     
     */
    public ReferencesToChange getReferencesToDelete() {
        return referencesToDelete;
    }

    /**
     * Legt den Wert der referencesToDelete-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferencesToChange }
     *     
     */
    public void setReferencesToDelete(ReferencesToChange value) {
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

    /**
     * Ruft den Wert der acceptAllOrNothing-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isAcceptAllOrNothing() {
        if (acceptAllOrNothing == null) {
            return false;
        } else {
            return acceptAllOrNothing;
        }
    }

    /**
     * Legt den Wert der acceptAllOrNothing-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcceptAllOrNothing(Boolean value) {
        this.acceptAllOrNothing = value;
    }

}
