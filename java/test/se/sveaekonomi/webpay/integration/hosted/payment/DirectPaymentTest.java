package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DirectPaymentTest {

    @Test
    public void testConfigureExcludedPaymentMethodsSe() {
        List<String> excluded  = WebPay.createOrder(SveaConfig.getDefaultConfig()) 
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .usePayPageDirectBankOnly()
                .configureExcludedPaymentMethods()
                .getExcludedPaymentMethods();
        
        assertEquals(18, excluded.size());
    }
    
    @Test
    public void testConfigureExcludedPaymentMethodsNo() {
        List<String> excluded  = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .setCountryCode(COUNTRYCODE.NO)
                .usePayPageDirectBankOnly()
                .configureExcludedPaymentMethods()
                .getExcludedPaymentMethods();
        
        assertEquals(22, excluded.size());
    }
    
    @Test
    public void testBuildDirectBankPayment() {
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addFee(TestingTool.createExVatBasedShippingFee())
            .addFee(TestingTool.createExVatBasedInvoiceFee())
            .addDiscount(TestingTool.createRelativeDiscount())
            .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .usePayPageDirectBankOnly()
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
        
        String base64Payment = form.getXmlMessageBase64();
        String html = Base64Util.decodeBase64String(base64Payment);
        String amount = html.substring(html.indexOf("<amount>") + 8, html.indexOf("</amount>"));
        
        assertEquals("21875", amount);
    }
    
    @Test
    public void testBuildDirectBankPaymentNotSE() {
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addFee(TestingTool.createExVatBasedShippingFee())
            .addFee(TestingTool.createExVatBasedInvoiceFee())
            .addDiscount(TestingTool.createRelativeDiscount())
            .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
            .setCountryCode(COUNTRYCODE.DE)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePayPageDirectBankOnly()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String base64Payment = form.getXmlMessageBase64();
        String html = Base64Util.decodeBase64String(base64Payment);
        String amount = html.substring(html.indexOf("<amount>") + 8, html.indexOf("</amount>"));
        
        assertEquals("21875", amount);
    }
}
