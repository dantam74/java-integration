package se.sveaekonomi.webpay.integration.response.webservice;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.response.Response;

public class GetAddressesResponse extends Response {
    
    private String legalName;
    private String securityNumber;
    private String addressLine1;
    private String addressLine2;
    private String postcode;
    private String postarea;
    private String businessType;
    private String addressSelector;
    private String firstName;
    private String lastName;
    
    public GetAddressesResponse(NodeList soapMessage) {
        super();
        setValues(soapMessage);
    }
    
    public String getLegalName() {
        return legalName;
    }
    
    public String getSecurityNumber() {
        return securityNumber;
    }
    
    public String getAddressLine1() {
        return addressLine1;
    }
    
    public String getAddressLine2() {
        return addressLine2;
    }
    
    public String getPostcode() {
        return postcode;
    }
    
    public String getPostarea() {
        return postarea;
    }
    
    public String getBusinessType() {
        return businessType;
    }
    
    public String getAddressSelector() {
        return addressSelector;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    private void setValues(NodeList soapMessage) {
        try {
            int size = soapMessage.getLength();
            
            for (int i = 0; i < size; i++) {
                Element node = (Element) soapMessage.item(i);
                // mandatory
                this.setOrderAccepted(Boolean.parseBoolean(getTagValue(node, "Accepted")));
                
                legalName = getTagValue(node, "LegalName");
                securityNumber = getTagValue(node, "SecurityNumber");
                addressLine1 = getTagValue(node, "AddressLine1");
                addressLine2 = getTagValue(node, "AddressLine2");
                postcode = getTagValue(node, "Postcode");
                postarea = getTagValue(node, "Postarea");
                businessType = getTagValue(node, "BusinessType");
                addressSelector = getTagValue(node, "AddressSelector");
                firstName = getTagValue(node, "FirstName");
                lastName = getTagValue(node, "LastName");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String getTagValue(Element elementNode, String tagName) {
        NodeList nodeList = elementNode.getElementsByTagName(tagName);
        Element element = (Element) nodeList.item(0);
        
        if (element != null && element.hasChildNodes()) {
            NodeList textList = element.getChildNodes();
            return ((Node) textList.item(0)).getNodeValue().trim();
        }
        
        return null;
    }
}
