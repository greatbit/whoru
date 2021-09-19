
package ru.greatbit.whoru.auth.providers;

import org.jvnet.jaxb2_commons.lang.CopyStrategy;
import org.jvnet.jaxb2_commons.lang.CopyTo;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBCopyStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBMergeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.MergeFrom;
import org.jvnet.jaxb2_commons.lang.MergeStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/**
 * <p>Java class for CognitoTokensResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CognitoTokensResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id_token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="access_token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="expires_in" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="token_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CognitoTokensResponse", propOrder = {
    "idToken",
    "accessToken",
    "expiresIn",
    "tokenType"
})
public class CognitoTokensResponse implements Serializable, Cloneable, CopyTo, Equals, HashCode, MergeFrom, ToString
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "id_token", required = true)
    protected String id_token;
    @XmlElement(name = "access_token", required = true)
    protected String access_token;
    @XmlElement(name = "expires_in")
    protected long expires_in;
    @XmlElement(name = "token_type", required = true)
    protected String token_type;

    /**
     * Default no-arg constructor
     * 
     */
    public CognitoTokensResponse() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public CognitoTokensResponse(final String id_token, final String access_token, final long expires_in, final String token_type) {
        this.id_token = id_token;
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.token_type = token_type;
    }

    /**
     * Gets the value of the idToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId_token() {
        return id_token;
    }

    /**
     * Sets the value of the idToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId_token(String value) {
        this.id_token = value;
    }

    /**
     * Gets the value of the accessToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccess_token() {
        return access_token;
    }

    /**
     * Sets the value of the accessToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccess_token(String value) {
        this.access_token = value;
    }

    /**
     * Gets the value of the expiresIn property.
     * 
     */
    public long getExpires_in() {
        return expires_in;
    }

    /**
     * Sets the value of the expiresIn property.
     * 
     */
    public void setExpires_in(long value) {
        this.expires_in = value;
    }

    /**
     * Gets the value of the tokenType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken_type() {
        return token_type;
    }

    /**
     * Sets the value of the tokenType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken_type(String value) {
        this.token_type = value;
    }

    public Object clone() {
        return copyTo(createNewInstance());
    }

    public Object copyTo(Object target) {
        final CopyStrategy strategy = JAXBCopyStrategy.INSTANCE;
        return copyTo(null, target, strategy);
    }

    public Object copyTo(ObjectLocator locator, Object target, CopyStrategy strategy) {
        final Object draftCopy = ((target == null)?createNewInstance():target);
        if (draftCopy instanceof CognitoTokensResponse) {
            final CognitoTokensResponse copy = ((CognitoTokensResponse) draftCopy);
            if (this.id_token != null) {
                String sourceIdToken;
                sourceIdToken = this.getId_token();
                String copyIdToken = ((String) strategy.copy(LocatorUtils.property(locator, "idToken", sourceIdToken), sourceIdToken));
                copy.setId_token(copyIdToken);
            } else {
                copy.id_token = null;
            }
            if (this.access_token != null) {
                String sourceAccessToken;
                sourceAccessToken = this.getAccess_token();
                String copyAccessToken = ((String) strategy.copy(LocatorUtils.property(locator, "accessToken", sourceAccessToken), sourceAccessToken));
                copy.setAccess_token(copyAccessToken);
            } else {
                copy.access_token = null;
            }
            long sourceExpiresIn;
            sourceExpiresIn = (true?this.getExpires_in(): 0L);
            long copyExpiresIn = strategy.copy(LocatorUtils.property(locator, "expiresIn", sourceExpiresIn), sourceExpiresIn);
            copy.setExpires_in(copyExpiresIn);
            if (this.token_type != null) {
                String sourceTokenType;
                sourceTokenType = this.getToken_type();
                String copyTokenType = ((String) strategy.copy(LocatorUtils.property(locator, "tokenType", sourceTokenType), sourceTokenType));
                copy.setToken_type(copyTokenType);
            } else {
                copy.token_type = null;
            }
        }
        return draftCopy;
    }

    public Object createNewInstance() {
        return new CognitoTokensResponse();
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof CognitoTokensResponse)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final CognitoTokensResponse that = ((CognitoTokensResponse) object);
        {
            String lhsIdToken;
            lhsIdToken = this.getId_token();
            String rhsIdToken;
            rhsIdToken = that.getId_token();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "idToken", lhsIdToken), LocatorUtils.property(thatLocator, "idToken", rhsIdToken), lhsIdToken, rhsIdToken)) {
                return false;
            }
        }
        {
            String lhsAccessToken;
            lhsAccessToken = this.getAccess_token();
            String rhsAccessToken;
            rhsAccessToken = that.getAccess_token();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "accessToken", lhsAccessToken), LocatorUtils.property(thatLocator, "accessToken", rhsAccessToken), lhsAccessToken, rhsAccessToken)) {
                return false;
            }
        }
        {
            long lhsExpiresIn;
            lhsExpiresIn = (true?this.getExpires_in(): 0L);
            long rhsExpiresIn;
            rhsExpiresIn = (true?that.getExpires_in(): 0L);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "expiresIn", lhsExpiresIn), LocatorUtils.property(thatLocator, "expiresIn", rhsExpiresIn), lhsExpiresIn, rhsExpiresIn)) {
                return false;
            }
        }
        {
            String lhsTokenType;
            lhsTokenType = this.getToken_type();
            String rhsTokenType;
            rhsTokenType = that.getToken_type();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "tokenType", lhsTokenType), LocatorUtils.property(thatLocator, "tokenType", rhsTokenType), lhsTokenType, rhsTokenType)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    public String toString() {
        final ToStringStrategy strategy = JAXBToStringStrategy.INSTANCE;
        final StringBuilder buffer = new StringBuilder();
        append(null, buffer, strategy);
        return buffer.toString();
    }

    public StringBuilder append(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        strategy.appendStart(locator, this, buffer);
        appendFields(locator, buffer, strategy);
        strategy.appendEnd(locator, this, buffer);
        return buffer;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        {
            String theIdToken;
            theIdToken = this.getId_token();
            strategy.appendField(locator, this, "idToken", buffer, theIdToken);
        }
        {
            String theAccessToken;
            theAccessToken = this.getAccess_token();
            strategy.appendField(locator, this, "accessToken", buffer, theAccessToken);
        }
        {
            long theExpiresIn;
            theExpiresIn = (true?this.getExpires_in(): 0L);
            strategy.appendField(locator, this, "expiresIn", buffer, theExpiresIn);
        }
        {
            String theTokenType;
            theTokenType = this.getToken_type();
            strategy.appendField(locator, this, "tokenType", buffer, theTokenType);
        }
        return buffer;
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        int currentHashCode = 1;
        {
            String theIdToken;
            theIdToken = this.getId_token();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "idToken", theIdToken), currentHashCode, theIdToken);
        }
        {
            String theAccessToken;
            theAccessToken = this.getAccess_token();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "accessToken", theAccessToken), currentHashCode, theAccessToken);
        }
        {
            long theExpiresIn;
            theExpiresIn = (true?this.getExpires_in(): 0L);
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "expiresIn", theExpiresIn), currentHashCode, theExpiresIn);
        }
        {
            String theTokenType;
            theTokenType = this.getToken_type();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "tokenType", theTokenType), currentHashCode, theTokenType);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

    public void mergeFrom(Object left, Object right) {
        final MergeStrategy strategy = JAXBMergeStrategy.INSTANCE;
        mergeFrom(null, null, left, right, strategy);
    }

    public void mergeFrom(ObjectLocator leftLocator, ObjectLocator rightLocator, Object left, Object right, MergeStrategy strategy) {
        if (right instanceof CognitoTokensResponse) {
            final CognitoTokensResponse target = this;
            final CognitoTokensResponse leftObject = ((CognitoTokensResponse) left);
            final CognitoTokensResponse rightObject = ((CognitoTokensResponse) right);
            {
                String lhsIdToken;
                lhsIdToken = leftObject.getId_token();
                String rhsIdToken;
                rhsIdToken = rightObject.getId_token();
                String mergedIdToken = ((String) strategy.merge(LocatorUtils.property(leftLocator, "idToken", lhsIdToken), LocatorUtils.property(rightLocator, "idToken", rhsIdToken), lhsIdToken, rhsIdToken));
                target.setId_token(mergedIdToken);
            }
            {
                String lhsAccessToken;
                lhsAccessToken = leftObject.getAccess_token();
                String rhsAccessToken;
                rhsAccessToken = rightObject.getAccess_token();
                String mergedAccessToken = ((String) strategy.merge(LocatorUtils.property(leftLocator, "accessToken", lhsAccessToken), LocatorUtils.property(rightLocator, "accessToken", rhsAccessToken), lhsAccessToken, rhsAccessToken));
                target.setAccess_token(mergedAccessToken);
            }
            {
                long lhsExpiresIn;
                lhsExpiresIn = (true?leftObject.getExpires_in(): 0L);
                long rhsExpiresIn;
                rhsExpiresIn = (true?rightObject.getExpires_in(): 0L);
                long mergedExpiresIn = ((long) strategy.merge(LocatorUtils.property(leftLocator, "expiresIn", lhsExpiresIn), LocatorUtils.property(rightLocator, "expiresIn", rhsExpiresIn), lhsExpiresIn, rhsExpiresIn));
                target.setExpires_in(mergedExpiresIn);
            }
            {
                String lhsTokenType;
                lhsTokenType = leftObject.getToken_type();
                String rhsTokenType;
                rhsTokenType = rightObject.getToken_type();
                String mergedTokenType = ((String) strategy.merge(LocatorUtils.property(leftLocator, "tokenType", lhsTokenType), LocatorUtils.property(rightLocator, "tokenType", rhsTokenType), lhsTokenType, rhsTokenType));
                target.setToken_type(mergedTokenType);
            }
        }
    }

    public CognitoTokensResponse withIdToken(String value) {
        setId_token(value);
        return this;
    }

    public CognitoTokensResponse withAccessToken(String value) {
        setAccess_token(value);
        return this;
    }

    public CognitoTokensResponse withExpiresIn(long value) {
        setExpires_in(value);
        return this;
    }

    public CognitoTokensResponse withTokenType(String value) {
        setToken_type(value);
        return this;
    }

}
