package se.sveaekonomi.webpay.integration.webservice.handleorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaDeliverOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class DeliverOrderTest {
    
    private DeliverOrderBuilder order;
    
    @Before
    public void setUp() {
        order = WebPay.deliverOrder(SveaConfig.getDefaultConfig());
    }
    
    @Test
    public void testBuildRequest() {
        DeliverOrderBuilder request = order
            .setOrderId(54086L);
        
        assertEquals(54086L, request.getOrderId());
    }
    
    @Test
    public void testDeliverInvoice() {
        SveaRequest<SveaDeliverOrder> request = order.addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
        .addFee(TestingTool.createExVatBasedShippingFee())
        .addDiscount(Item.fixedDiscount()
            .setAmountIncVat(10))
        .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
        .setOrderId(54086L)
        .setNumberOfCreditDays(1)
        .setCreditInvoice("id")
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .deliverInvoiceOrder()
        .prepareRequest();
        
        //First order row is a product
        assertEquals("1", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).ArticleNumber);
        assertEquals("Prod: Specification", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).Description);
        assertEquals(100.00, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(2, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).NumberOfUnits, 0);
        assertEquals("st", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).Unit);
        assertEquals(25, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).VatPercent, 0);
        assertEquals(0, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(0).DiscountPercent, 0);
        
        //Second order row is shipment
        assertEquals("33", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).ArticleNumber);
        assertEquals("shipping: Specification", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).Description);
        assertEquals(50, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(1, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).NumberOfUnits, 0);
        assertEquals("st", request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).Unit);
        assertEquals(25, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(1).DiscountPercent, 0);
        
        //discount
        assertEquals(-8.0, request.request.deliverOrderInformation.deliverInvoiceDetails.OrderRows.get(2).PricePerUnit, 0);
        
        assertEquals(1, request.request.deliverOrderInformation.deliverInvoiceDetails.NumberofCreditDays);
        assertEquals("Post", request.request.deliverOrderInformation.deliverInvoiceDetails.InvoiceDistributionType);
        assertTrue(request.request.deliverOrderInformation.deliverInvoiceDetails.IsCreditInvoice);
        assertEquals("id", request.request.deliverOrderInformation.deliverInvoiceDetails.InvoiceIdToCredit);
        assertEquals("54086", request.request.deliverOrderInformation.sveaOrderId);
        assertEquals("Invoice", request.request.deliverOrderInformation.orderType);
    }
}
