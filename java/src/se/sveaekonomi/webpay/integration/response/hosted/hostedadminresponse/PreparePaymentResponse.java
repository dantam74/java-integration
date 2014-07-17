package se.sveaekonomi.webpay.integration.response.hosted.hostedadminresponse;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

/**
 * Handles individual responses from hosted admin webservice requests
 * 
 * @author Kristian Grossman-Madsen
 */
public class PreparePaymentResponse extends HostedAdminResponse {

	private String rawResponse;
	private String id;
    private String created;
	
	public PreparePaymentResponse(String responseXmlBase64, String secretWord) {
		super(responseXmlBase64, secretWord);
		this.rawResponse = this.xml;
		
		this.setValues();
	}

	/** 
	 * implement this to parse xml and set attributes according to response attributes 
	 */
	void setValues() {		
				
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document d1 = builder.parse(new InputSource(new StringReader(xml)));
			NodeList nodeList = d1.getElementsByTagName("response");
			int size = nodeList.getLength();

			for (int i = 0; i < size; i++) {
				Element element = (Element) nodeList.item(i);

				int status = Integer.parseInt(getTagValue(element, "statuscode"));
				if (status == 0) {
					this.setOrderAccepted(true);
					this.setResultCode("0 (ORDER_ACCEPTED)");
				} else {
					this.setOrderAccepted(false);
					setErrorParams(status);
				}
				
				if( this.isOrderAccepted() ) {	// don't attempt to parse a bad response

					this.id = getTagValue(element, "id");
					this.created = getTagValue(element, "created");
				}
			}
		} catch (ParserConfigurationException e) {
			throw new SveaWebPayException("ParserConfigurationException", e);
		} catch (SAXException e) {
			throw new SveaWebPayException("SAXException", e);
		} catch (IOException e) {
			throw new SveaWebPayException("IOException", e);
		}
	}
	
    public String getRawResponse() {
		return rawResponse;
	}

    public String getId() {
		return id;
	}

    public String getCreated() {
		return created;
	}
}

