package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.xml.bind.ValidationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.LowerTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.request.GetRequestProperties;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;

/**
 * lowerTransaction modifies the amount in an existing card transaction 
 * having status AUTHORIZED or CONFIRMED. If the amount is lowered by an 
 * amount equal to the transaction authorized amount, then after a 
 * successful request the transaction will get the status ANNULLED.
 * 
 * @author Kristian Grossman-Madsen
 */
public class LowerTransactionRequest extends HostedAdminRequest <LowerTransactionRequest> {

    /** Required. */
	public String transactionId;
	
    /** Required. Use minor currency (i.e. 1 SEK => 100 in minor currency) */
	public Integer amountToLower;
	
	public LowerTransactionRequest setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}
	
	public String getTransactionId() {
		return transactionId;
	}

	public LowerTransactionRequest setAmountToLower(Integer amountToLower) {
		this.amountToLower = amountToLower;
		return this;
	}	
	
	public double getAmountToLower() {
		return amountToLower;
	}

	public LowerTransactionRequest(ConfigurationProvider config) {
		super(config, "loweramount");
	}
	
	/**
	 * validates that all required attributes needed for the request are present in the builder object
	 * @return indicating which methods are missing, or empty String if no problems found
	 */
	public String validateRequest() {
        String errors = "";
        if (this.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }
        
        if (this.getTransactionId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}
        return errors;    
    }	

	/*
	 * returns xml for hosted webservice "loweramount" request
	 */
	public String getRequestMessageXml( ConfigurationProvider config ) {

		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(os, "UTF-8");

			xmlw.writeStartDocument("UTF-8", "1.0");
				xmlw.writeComment( GetRequestProperties.getLibraryAndPlatformPropertiesAsJson(config) );
				xmlw.writeStartElement("loweramount");
					xmlw.writeStartElement("transactionid");
						xmlw.writeCharacters(this.getTransactionId());
					xmlw.writeEndElement();
					xmlw.writeStartElement("amounttolower");
						xmlw.writeCharacters(Integer.toString((int)this.getAmountToLower()));						
					xmlw.writeEndElement();
				xmlw.writeEndElement();
			xmlw.writeEndDocument();
			xmlw.close();
		} catch (XMLStreamException e) {
			throw new SveaWebPayException("Error when building XML", e);
		}

		try {
			return new String(os.toByteArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new SveaWebPayException("Unsupported encoding UTF-8", e);
		}
	}

	/**
	 * returns the request fields to post to service
	 */
	public Hashtable<String,String> prepareRequest() {

    	// validate request and throw exception if validation fails
        String errors = validateRequest();
        
        if (!errors.equals("")) {
        	System.out.println(errors);
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        }
        
        // build inspectable request object and return
		Hashtable<String,String> requestFields = new Hashtable<>();

		String merchantId = this.config.getMerchantId(PAYMENTTYPE.HOSTED, this.getCountryCode());
		String secretWord = this.config.getSecretWord(PAYMENTTYPE.HOSTED, this.getCountryCode());		
		
    	String xmlMessage = getRequestMessageXml(this.config);
    	String xmlMessageBase64 = Base64Util.encodeBase64String(xmlMessage);
    	String macSha512 =  HashUtil.createHash(xmlMessageBase64 + secretWord, HASHALGORITHM.SHA_512);			

    	requestFields.put("message", xmlMessageBase64);
    	requestFields.put("mac", macSha512);
    	requestFields.put("merchantid", merchantId);
    	
		return requestFields;
	}	
	
	/**
	 * validate, prepare and do request
	 * @return ConfirmTransactionResponse
	 * @throws SveaWebPayException
	 */
	public LowerTransactionResponse doRequest() throws SveaWebPayException {

		try {
			// prepare request fields
	    	Hashtable<String, String> requestFields = this.prepareRequest();

	    	// send request 
	    	String xmlResponse = sendHostedAdminRequest(requestFields);
	
	    	// parse response	
			return new LowerTransactionResponse( getResponseMessageFromXml(xmlResponse), getResponseMacFromXml(xmlResponse), this.config.getSecretWord(PAYMENTTYPE.HOSTED, this.getCountryCode()) );
			
	    } catch (IllegalStateException ex) {
	        throw new SveaWebPayException("IllegalStateException", ex);
	    }
		catch (IOException ex) {
			//System.out.println(ex.toString());
			//System.out.println(((HttpResponseException)ex).getStatusCode());
	        throw new SveaWebPayException("IOException", ex);
	    }
	}
}
