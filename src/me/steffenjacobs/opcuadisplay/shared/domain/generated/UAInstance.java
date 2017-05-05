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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für UAInstance complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="UAInstance">
 *   &lt;complexContent>
 *     &lt;extension base="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UANode">
 *       &lt;attribute name="ParentNodeId" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodeId" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UAInstance")
@XmlSeeAlso({
    UAObject.class,
    UAVariable.class,
    UAMethod.class,
    UAView.class
})
public class UAInstance
    extends UANode
{

    @XmlAttribute(name = "ParentNodeId")
    protected String parentNodeId;

    /**
     * Ruft den Wert der parentNodeId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentNodeId() {
        return parentNodeId;
    }

    /**
     * Legt den Wert der parentNodeId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentNodeId(String value) {
        this.parentNodeId = value;
    }

}
