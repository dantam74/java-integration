package se.sveaekonomi.webpay.integration.hosted;

import junit.framework.Assert;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class HostedPaymentsRequestTest {

    @Test
    public void testDoCardPaymentRequest() {
        PaymentForm form = WebPay.createOrder()
        .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
        .addFee(TestingTool.createExVatBasedShippingFee())
        .addFee(TestingTool.createExVatBasedInvoiceFee())
        .addDiscount(TestingTool.createRelativeDiscount())
        .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(TestingTool.DefaultTestCurrency)
        .usePayPageCardOnly()
        .setReturnUrl("http://myurl.se")
        .getPaymentForm();
        
        Assert.assertNotNull(form);
    }
}
