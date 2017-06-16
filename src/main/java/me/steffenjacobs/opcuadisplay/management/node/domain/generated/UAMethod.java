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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für UAMethod complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="UAMethod">
 *   &lt;complexContent>
 *     &lt;extension base="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}UAInstance">
 *       &lt;attribute name="Executable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="UserExecutable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="MethodDeclarationId" type="{http://opcfoundation.org/UA/2011/03/UANodeSet.xsd}NodeId" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UAMethod")
public class UAMethod
    extends UAInstance
{

    @XmlAttribute(name = "Executable")
    protected Boolean executable;
    @XmlAttribute(name = "UserExecutable")
    protected Boolean userExecutable;
    @XmlAttribute(name = "MethodDeclarationId")
    protected String methodDeclarationId;

    /**
     * Ruft den Wert der executable-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isExecutable() {
        if (executable == null) {
            return true;
        } else {
            return executable;
        }
    }

    /**
     * Legt den Wert der executable-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExecutable(Boolean value) {
        this.executable = value;
    }

    /**
     * Ruft den Wert der userExecutable-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isUserExecutable() {
        if (userExecutable == null) {
            return true;
        } else {
            return userExecutable;
        }
    }

    /**
     * Legt den Wert der userExecutable-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUserExecutable(Boolean value) {
        this.userExecutable = value;
    }

    /**
     * Ruft den Wert der methodDeclarationId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethodDeclarationId() {
        return methodDeclarationId;
    }

    /**
     * Legt den Wert der methodDeclarationId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethodDeclarationId(String value) {
        this.methodDeclarationId = value;
    }

}
