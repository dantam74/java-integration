package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Hashtable;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

public class ConfirmTransactionRequestTest extends TestCase {

	private DeliverOrderBuilder order;
	private ConfirmTransactionRequest request;
	
	@Before
	public void setUp() {	
		order = new DeliverOrderBuilder(SveaConfig.getDefaultConfig())
			.setCountryCode(COUNTRYCODE.SE)
			.setTransactionId( "123456" )
		;
		
		request = new ConfirmTransactionRequest(SveaConfig.getDefaultConfig())
			.setCountryCode(order.getCountryCode())
			.setTransactionId( Long.toString(order.getOrderId()) )
			.setCaptureDate("14-12-01");
	}
	
    @Test
    public void test_ConfirmTransactionRequest_class_exists() {    	   	        
        assertThat( request, instanceOf(ConfirmTransactionRequest.class) );
        assertThat( request, instanceOf(HostedAdminRequest.class) );
    }    
    
    @Test 
    public void test_getRequestMessageXml() {    	
    	
		// uncomment to get expectedXmlMessage
		//String secretWord = this.order.getConfig().getSecretWord(PAYMENTTYPE.HOSTED, request.getCountryCode()); // uncomment to get below xml  	 
		//String expectedXmlMessage = request.getRequestMessageXml(); // uncomment to get below xml
		//System.out.println(expectedXmlMessage); // uncomment to get below xml
		//String expectedXmlMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--Message generated by Integration package Java--><confirm><transactionid>123456</transactionid><capturedate>14-12-01</capturedate></confirm>";
	
    	String expectedXmlMessage = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"Java Integration Package\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"2.0.2\"}-->" +
			"<confirm>" +
				"<transactionid>123456</transactionid>" +
				"<capturedate>14-12-01</capturedate>" + 
			"</confirm>"
		;
    	assertEquals( expectedXmlMessage, request.getRequestMessageXml( SveaConfig.getDefaultConfig()) );    
    }
    
    @Test
    public void test_prepareRequest() {

		String merchantId = this.order.getConfig().getMerchantId(PAYMENTTYPE.HOSTED, request.getCountryCode());

		// uncomment to get expectedXmlMessageBase64
		//String secretWord = this.order.getConfig().getSecretWord(PAYMENTTYPE.HOSTED, request.getCountryCode());   	     	
		//String expectedXmlMessage = request.getRequestMessageXml();
		//String expectedXmlMessageBase64 = Base64Util.encodeBase64String(expectedXmlMessage);
		//System.out.println(expectedXmlMessageBase64);
    	String expectedXmlMessageBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48IS0teyJYLVN2ZWEtSW50ZWdyYXRpb24tVmVyc2lvbiI6IkludGVncmF0aW9uIHBhY2thZ2UgZGVmYXVsdCBTdmVhVGVzdENvbmZpZ3VyYXRpb25Qcm92aWRlci4iLCJYLVN2ZWEtSW50ZWdyYXRpb24tUGxhdGZvcm0iOiJJbnRlZ3JhdGlvbiBwYWNrYWdlIGRlZmF1bHQgU3ZlYVRlc3RDb25maWd1cmF0aW9uUHJvdmlkZXIuIiwiWC1TdmVhLUxpYnJhcnktTmFtZSI6IkphdmEgSW50ZWdyYXRpb24gUGFja2FnZSIsIlgtU3ZlYS1JbnRlZ3JhdGlvbi1Db21wYW55IjoiSW50ZWdyYXRpb24gcGFja2FnZSBkZWZhdWx0IFN2ZWFUZXN0Q29uZmlndXJhdGlvblByb3ZpZGVyLiIsIlgtU3ZlYS1MaWJyYXJ5LVZlcnNpb24iOiIyLjAuMiJ9LS0+PGNvbmZpcm0+PHRyYW5zYWN0aW9uaWQ+MTIzNDU2PC90cmFuc2FjdGlvbmlkPjxjYXB0dXJlZGF0ZT4xNC0xMi0wMTwvY2FwdHVyZWRhdGU+PC9jb25maXJtPg==";
    	
    	//String expectedMacSha512 =  HashUtil.createHash(expectedXmlMessageBase64 + secretWord, HASHALGORITHM.SHA_512); // uncomment to get below xml  
		//System.out.println(expectedMacSha512); // uncomment to get below xml    	
    	String expectedMacSha512 = "db9ec3054d29ffc225aba76b9c751bbfc340a4da67bc7fe8eaa6de74ff26d2fd8534e6d9ff537055e90b284cb2c066e34efe01fa17f20f9f17ca684c9649242e";    	
    	
    	Hashtable<String, String> requestFields = this.request.prepareRequest();
    	assertEquals( expectedXmlMessageBase64, requestFields.get("message") );
    	assertEquals( expectedMacSha512, requestFields.get("mac") );
    	assertEquals( merchantId, requestFields.get("merchantid") );
    }
    
}
