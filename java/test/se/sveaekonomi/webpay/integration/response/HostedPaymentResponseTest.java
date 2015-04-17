package se.sveaekonomi.webpay.integration.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.SveaResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

public class HostedPaymentResponseTest {

    public final SveaConfig config = new SveaConfig();
    
    @Test
    public void testDirectBankResponse() {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY2OTg5Ij4NCiAgICA8cGF5bWVudG1ldGhvZD5EQk5PUkRFQVNFPC9wYXltZW50bWV0aG9kPg0KICAgIDxtZXJjaGFudGlkPjExNzU8L21lcmNoYW50aWQ+DQogICAgPGN1c3RvbWVycmVmbm8+MzczNzgyMzk4N19pZF8wMDE8L2N1c3RvbWVycmVmbm8+DQogICAgPGFtb3VudD41MDA8L2Ftb3VudD4NCiAgICA8Y3VycmVuY3k+U0VLPC9jdXJyZW5jeT4NCiAgPC90cmFuc2FjdGlvbj4NCiAgPHN0YXR1c2NvZGU+MDwvc3RhdHVzY29kZT4NCjwvcmVzcG9uc2U+";
        
        SveaResponse response = new SveaResponse(testXMLResponseBase64,  null);
        
        assertEquals("566989", response.getTransactionId());
        assertEquals("DBNORDEASE", response.getPaymentMethod());
        assertEquals("1175", response.getMerchantId());
        assertEquals("3737823987_id_001", response.getClientOrderNumber());
        assertEquals(5, response.getAmount(), 0);
        assertEquals("SEK", response.getCurrency());
        assertEquals("0 (ORDER_ACCEPTED)", response.getResultCode());
    }
    
    @Test
    public void testCreatePaymentResponse() {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY2OTIzIj4NCiAgICA8cGF5bWVudG1ldGhvZD5LT1JUQ0VSVDwvcGF5bWVudG1ldGhvZD4NCiAgICA8bWVyY2hhbnRpZD4xMTc1PC9tZXJjaGFudGlkPg0KICAgIDxjdXN0b21lcnJlZm5vPnRlc3RfMTM1OTQ2MDU3NjQ5MTwvY3VzdG9tZXJyZWZubz4NCiAgICA8YW1vdW50PjUwMDwvYW1vdW50Pg0KICAgIDxjdXJyZW5jeT5TRUs8L2N1cnJlbmN5Pg0KICAgIDxjYXJkdHlwZT5WSVNBPC9jYXJkdHlwZT4NCiAgICA8bWFza2VkY2FyZG5vPjQ0NDQzM3h4eHh4eDMzMDA8L21hc2tlZGNhcmRubz4NCiAgICA8ZXhwaXJ5bW9udGg+MDM8L2V4cGlyeW1vbnRoPg0KICAgIDxleHBpcnl5ZWFyPjIwPC9leHBpcnl5ZWFyPg0KICAgIDxhdXRoY29kZT4xNTI1ODc8L2F1dGhjb2RlPg0KICA8L3RyYW5zYWN0aW9uPg0KICA8c3RhdHVzY29kZT4wPC9zdGF0dXNjb2RlPg0KPC9yZXNwb25zZT4=";
        
        SveaResponse response = new SveaResponse(testXMLResponseBase64,  null);
        
        assertTrue(response.isOrderAccepted());
        assertEquals("566923", response.getTransactionId());
        assertEquals("test_1359460576491", response.getClientOrderNumber());
        assertEquals(5.00, response.getAmount(), 0);
        assertEquals("SEK", response.getCurrency());
        assertEquals("VISA", response.getCardType());
        assertEquals("03", response.getExpiryMonth());
        assertEquals("20", response.getExpiryYear());
        assertEquals("152587", response.getAuthCode());
    }
    
    @Test
    public void testPayPageDirectBankResponse() {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY3MDU2Ij4NCiAgICA8cGF5bWVudG1ldGhvZD5EQk5PUkRFQVNFPC9wYXltZW50bWV0aG9kPg0KICAgIDxtZXJjaGFudGlkPjExNzU8L21lcmNoYW50aWQ+DQogICAgPGN1c3RvbWVycmVmbm8+dGVzdF8xMzU5NjIwMzExNTg0PC9jdXN0b21lcnJlZm5vPg0KICAgIDxhbW91bnQ+NTAwPC9hbW91bnQ+DQogICAgPGN1cnJlbmN5PlNFSzwvY3VycmVuY3k+DQogIDwvdHJhbnNhY3Rpb24+DQogIDxzdGF0dXNjb2RlPjA8L3N0YXR1c2NvZGU+DQo8L3Jlc3BvbnNlPg==";
        
        SveaResponse response = new SveaResponse(testXMLResponseBase64, null);
        
        assertTrue(response.isOrderAccepted());
        assertEquals("0 (ORDER_ACCEPTED)", response.getResultCode());
        assertEquals("567056", response.getTransactionId());
        assertEquals("1175", response.getMerchantId());
        assertEquals(5, response.getAmount(), 0);
        assertEquals("test_1359620311584", response.getClientOrderNumber());
        assertEquals("SEK", response.getCurrency());
        assertEquals("DBNORDEASE", response.getPaymentMethod());
    }
    
    @Test
    public void testPayPageDirectBankInterruptedResponse() {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY3MDYyIj4NCiAgICA8cGF5bWVudG1ldGhvZD5EQk5PUkRFQVNFPC9wYXltZW50bWV0aG9kPg0KICAgIDxtZXJjaGFudGlkPjExNzU8L21lcmNoYW50aWQ+DQogICAgPGN1c3RvbWVycmVmbm8+dGVzdF8xMzU5NjIzMDIyMTQzPC9jdXN0b21lcnJlZm5vPg0KICAgIDxhbW91bnQ+NTAwPC9hbW91bnQ+DQogICAgPGN1cnJlbmN5PlNFSzwvY3VycmVuY3k+DQogIDwvdHJhbnNhY3Rpb24+DQogIDxzdGF0dXNjb2RlPjEwNzwvc3RhdHVzY29kZT4NCjwvcmVzcG9uc2U+DQo=";
        
        SveaResponse response = new SveaResponse(testXMLResponseBase64,  null);
        
        assertEquals("107 (DENIED_BY_BANK)", response.getResultCode());
    }
    
    @Test
    public void testPayPageCardPaymentResponse() {
        String testXMLResponseBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY3MDU4Ij4NCiAgICA8cGF5bWVudG1ldGhvZD5LT1JUQ0VSVDwvcGF5bWVudG1ldGhvZD4NCiAgICA8bWVyY2hhbnRpZD4xMTc1PC9tZXJjaGFudGlkPg0KICAgIDxjdXN0b21lcnJlZm5vPnRlc3RfMTM1OTYyMTQ2NTk5MDwvY3VzdG9tZXJyZWZubz4NCiAgICA8YW1vdW50PjUwMDwvYW1vdW50Pg0KICAgIDxjdXJyZW5jeT5TRUs8L2N1cnJlbmN5Pg0KICAgIDxjYXJkdHlwZT5WSVNBPC9jYXJkdHlwZT4NCiAgICA8bWFza2VkY2FyZG5vPjQ0NDQzM3h4eHh4eDMzMDA8L21hc2tlZGNhcmRubz4NCiAgICA8ZXhwaXJ5bW9udGg+MDM8L2V4cGlyeW1vbnRoPg0KICAgIDxleHBpcnl5ZWFyPjIwPC9leHBpcnl5ZWFyPg0KICAgIDxhdXRoY29kZT43NjQ4Nzc8L2F1dGhjb2RlPg0KICA8L3RyYW5zYWN0aW9uPg0KICA8c3RhdHVzY29kZT4wPC9zdGF0dXNjb2RlPg0KPC9yZXNwb25zZT4NCg==";
        
        SveaResponse response = new SveaResponse(testXMLResponseBase64,  null);
        
        assertEquals("567058", response.getTransactionId());
        assertEquals("KORTCERT", response.getPaymentMethod());
        assertEquals("1175", response.getMerchantId());
        assertEquals("test_1359621465990", response.getClientOrderNumber());
        assertEquals(5, response.getAmount(), 0);
        assertEquals("SEK", response.getCurrency());
        assertEquals("VISA", response.getCardType());
        assertEquals("444433xxxxxx3300", response.getMaskedCardNumber());
        assertEquals("03", response.getExpiryMonth());
        assertEquals("20", response.getExpiryYear());
        assertEquals("764877", response.getAuthCode());
        assertEquals("0 (ORDER_ACCEPTED)", response.getResultCode());
    }
    
    @Test
    public void testSetErrorParamsCode101() {
        String responseXmlBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48cmVzcG9uc2U+DQogIDx0cmFuc2FjdGlvbiBpZD0iNTY3MDU4Ij4NCiAgICA8cGF5bWVudG1ldGhvZD5LT1JUQ0VSVDwvcGF5bWVudG1ldGhvZD4NCiAgICA8bWVyY2hhbnRpZD4xMTc1PC9tZXJjaGFudGlkPg0KICAgIDxjdXN0b21lcnJlZm5vPnRlc3RfMTM1OTYyMTQ2NTk5MDwvY3VzdG9tZXJyZWZubz4NCiAgICA8YW1vdW50PjUwMDwvYW1vdW50Pg0KICAgIDxjdXJyZW5jeT5TRUs8L2N1cnJlbmN5Pg0KICAgIDxjYXJkdHlwZT5WSVNBPC9jYXJkdHlwZT4NCiAgICA8bWFza2VkY2FyZG5vPjQ0NDQzM3h4eHh4eDMzMDA8L21hc2tlZGNhcmRubz4NCiAgICA8ZXhwaXJ5bW9udGg+MDM8L2V4cGlyeW1vbnRoPg0KICAgIDxleHBpcnl5ZWFyPjIwPC9leHBpcnl5ZWFyPg0KICAgIDxhdXRoY29kZT43NjQ4Nzc8L2F1dGhjb2RlPg0KICA8L3RyYW5zYWN0aW9uPg0KICA8c3RhdHVzY29kZT4xMDE8L3N0YXR1c2NvZGU+DQo8L3Jlc3BvbnNlPg==";
        
        SveaResponse response = new SveaResponse(responseXmlBase64,  null);
        
        assertEquals("Invalid XML.", response.getErrorMessage());
    }
    
    @Test
    public void test_HostedPaymentResponse_with_MAC_validation_success_with_correct_mac() {
    	
      // $message, $mac and $merchantid below was taken from server logs for a test card transaction to the merchant 1130
      String message = "PD94bWwgdmVyc2lvbj0nMS4wJyBlbmNvZGluZz0nVVRGLTgnPz48cmVzcG9uc2U+PHRyYW5zYWN0aW9uIGlkPSI1ODEzODAiPjxwYXltZW50bWV0aG9kPktPUlRDRVJUPC9wYXltZW50bWV0aG9kPjxtZXJjaGFudGlkPjExMzA8L21lcmNoYW50aWQ+PGN1c3RvbWVycmVmbm8+MzY8L2N1c3RvbWVycmVmbm8+PGFtb3VudD4xODU3ODwvYW1vdW50PjxjdXJyZW5jeT5TRUs8L2N1cnJlbmN5PjxjYXJkdHlwZT5WSVNBPC9jYXJkdHlwZT48bWFza2VkY2FyZG5vPjQ0NDQzM3h4eHh4eDExMDA8L21hc2tlZGNhcmRubz48ZXhwaXJ5bW9udGg+MDE8L2V4cGlyeW1vbnRoPjxleHBpcnl5ZWFyPjE1PC9leHBpcnl5ZWFyPjxhdXRoY29kZT40NTM2MjY8L2F1dGhjb2RlPjxjdXN0b21lcj48Zmlyc3RuYW1lLz48bGFzdG5hbWUvPjxpbml0aWFscy8+PGVtYWlsPnRlc3RAdGltLWludGVybmF0aW9uYWwubmV0PC9lbWFpbD48c3NuPjwvc3NuPjxhZGRyZXNzPktsb2NrYXJnYXRhbiA1QzwvYWRkcmVzcz48YWRkcmVzczIvPjxjaXR5PlbDpHN0ZXLDpXM8L2NpdHk+PGNvdW50cnk+U0U8L2NvdW50cnk+PHppcD43MjM0NDwvemlwPjxwaG9uZT40NjcwNDE2MDA5MDwvcGhvbmU+PHZhdG51bWJlci8+PGhvdXNlbnVtYmVyPjU8L2hvdXNlbnVtYmVyPjxjb21wYW55bmFtZS8+PGZ1bGxuYW1lLz48L2N1c3RvbWVyPjwvdHJhbnNhY3Rpb24+PHN0YXR1c2NvZGU+MDwvc3RhdHVzY29kZT48L3Jlc3BvbnNlPg==";
      String mac = "0411ed66739c251308b70c642fc5f7282f89050421408b74bdd909fb0c13c37c4c2efd6da3593dc388dd28952478aeb1ce5259caf33fd68d364fc4f82914e055";

      ConfigurationProvider config = SveaConfig.getDefaultConfig();
      String secretWord = config.getSecretWord(PAYMENTTYPE.HOSTED, COUNTRYCODE.SE); 
     
      HostedPaymentResponse response = new HostedPaymentResponse(message, mac, secretWord);

      assertTrue( response.isOrderAccepted() );   
    }    
    
    @Test
    public void test_HostedPaymentResponse_with_MAC_validation_fails_with_bad_mac() {
    	
      // $message, $mac and $merchantid below was taken from server logs for a test card transaction to the merchant 1130
      String message = "PD94bWwgdmVyc2lvbj0nMS4wJyBlbmNvZGluZz0nVVRGLTgnPz48cmVzcG9uc2U+PHRyYW5zYWN0aW9uIGlkPSI1ODEzODAiPjxwYXltZW50bWV0aG9kPktPUlRDRVJUPC9wYXltZW50bWV0aG9kPjxtZXJjaGFudGlkPjExMzA8L21lcmNoYW50aWQ+PGN1c3RvbWVycmVmbm8+MzY8L2N1c3RvbWVycmVmbm8+PGFtb3VudD4xODU3ODwvYW1vdW50PjxjdXJyZW5jeT5TRUs8L2N1cnJlbmN5PjxjYXJkdHlwZT5WSVNBPC9jYXJkdHlwZT48bWFza2VkY2FyZG5vPjQ0NDQzM3h4eHh4eDExMDA8L21hc2tlZGNhcmRubz48ZXhwaXJ5bW9udGg+MDE8L2V4cGlyeW1vbnRoPjxleHBpcnl5ZWFyPjE1PC9leHBpcnl5ZWFyPjxhdXRoY29kZT40NTM2MjY8L2F1dGhjb2RlPjxjdXN0b21lcj48Zmlyc3RuYW1lLz48bGFzdG5hbWUvPjxpbml0aWFscy8+PGVtYWlsPnRlc3RAdGltLWludGVybmF0aW9uYWwubmV0PC9lbWFpbD48c3NuPjwvc3NuPjxhZGRyZXNzPktsb2NrYXJnYXRhbiA1QzwvYWRkcmVzcz48YWRkcmVzczIvPjxjaXR5PlbDpHN0ZXLDpXM8L2NpdHk+PGNvdW50cnk+U0U8L2NvdW50cnk+PHppcD43MjM0NDwvemlwPjxwaG9uZT40NjcwNDE2MDA5MDwvcGhvbmU+PHZhdG51bWJlci8+PGhvdXNlbnVtYmVyPjU8L2hvdXNlbnVtYmVyPjxjb21wYW55bmFtZS8+PGZ1bGxuYW1lLz48L2N1c3RvbWVyPjwvdHJhbnNhY3Rpb24+PHN0YXR1c2NvZGU+MDwvc3RhdHVzY29kZT48L3Jlc3BvbnNlPg==";
      String mac = "foobar";

      ConfigurationProvider config = SveaConfig.getDefaultConfig();
      String secretWord = config.getSecretWord(PAYMENTTYPE.HOSTED, COUNTRYCODE.SE); 
     
      HostedPaymentResponse response = new HostedPaymentResponse(message, mac, secretWord);

      assertFalse( response.isOrderAccepted() );   
      assertEquals( "311 (BAD_MAC)", response.getResultCode() );
      assertEquals( "Invalid value for mac.", response.getErrorMessage() );
    }    
}
