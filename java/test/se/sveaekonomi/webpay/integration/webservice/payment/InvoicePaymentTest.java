package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class InvoicePaymentTest {

    @Test
    public void testInvoiceWithIndividualCustomerFromSe() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addOrderRow(TestingTool.createExVatBasedOrderRow("2"))
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(TestingTool.DefaultTestCurrency)
            .useInvoicePayment()
            .prepareRequest();
        
        assertEquals("194605092222", request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber);
        assertEquals(COUNTRYCODE.SE, request.request.CreateOrderInformation.CustomerIdentity.CountryCode);
        assertEquals("Individual", request.request.CreateOrderInformation.CustomerIdentity.CustomerType);
    }
    
    @Test
    public void testInvoiceWithAuth() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setClientOrderNumber("nr26")
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(TestingTool.DefaultTestCurrency)
            .useInvoicePayment()
            .prepareRequest();
        
        assertEquals("sverigetest", request.request.Auth.Username);
        assertEquals("sverigetest", request.request.Auth.Password);
        assertEquals(79021, (int)request.request.Auth.ClientNumber);
    }
    
    @Test
    public void testSetAuth() {
        SveaRequest<SveaCreateOrder> request =  WebPay.createOrder()
        .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
        .addOrderRow(TestingTool.createExVatBasedOrderRow("2"))
        .addCustomerDetails(Item.individualCustomer()
            .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(TestingTool.DefaultTestCurrency)
        .useInvoicePayment()
        .prepareRequest();
        
        assertEquals(79021, request.request.Auth.ClientNumber, 0);
        assertEquals("sverigetest", request.request.Auth.Username);
        assertEquals("sverigetest", request.request.Auth.Password);
    }
    
    @Test
    public void testInvoiceWithIndividualCustomerFromNl() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
        .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
        .addCustomerDetails(TestingTool.createIndividualCustomer())
        .setCountryCode(COUNTRYCODE.NL)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(TestingTool.DefaultTestCurrency)
        .useInvoicePayment()
        .prepareRequest();
         
        //CustomerIdentity
        assertEquals("Tess Persson", request.request.CreateOrderInformation.CustomerIdentity.FullName);
        assertEquals("test@svea.com", request.request.CreateOrderInformation.CustomerIdentity.Email);
        assertEquals("0811111111", request.request.CreateOrderInformation.CustomerIdentity.PhoneNumber);
        assertEquals("123.123.123", request.request.CreateOrderInformation.CustomerIdentity.IpAddress);
        assertEquals("Testgatan", request.request.CreateOrderInformation.CustomerIdentity.Street);
        assertEquals("c/o Eriksson, Erik", request.request.CreateOrderInformation.CustomerIdentity.CoAddress);
        assertEquals("99999", request.request.CreateOrderInformation.CustomerIdentity.ZipCode);
        assertEquals("1", request.request.CreateOrderInformation.CustomerIdentity.HouseNumber);
        assertEquals("Stan", request.request.CreateOrderInformation.CustomerIdentity.Locality);
        assertEquals(COUNTRYCODE.NL, request.request.CreateOrderInformation.CustomerIdentity.CountryCode);
        assertEquals("Individual", request.request.CreateOrderInformation.CustomerIdentity.CustomerType);
        
        assertEquals("Tess", request.request.CreateOrderInformation.CustomerIdentity.IndividualIdentity.FirstName);
        assertEquals("Persson", request.request.CreateOrderInformation.CustomerIdentity.IndividualIdentity.LastName);
        assertEquals("SB", request.request.CreateOrderInformation.CustomerIdentity.IndividualIdentity.Initials);
        assertEquals("19231212", request.request.CreateOrderInformation.CustomerIdentity.IndividualIdentity.BirthDate);
    }
    
    @Test
    public void testInvoiceWithCompanyCustomerFromNl() {
         SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
        .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
        .addCustomerDetails(TestingTool.createIndividualCustomerNl())
        .addOrderRow(TestingTool.createExVatBasedOrderRow("2"))
        .setCountryCode(COUNTRYCODE.NL)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(TestingTool.DefaultTestCurrency)
        .useInvoicePayment()
        .prepareRequest();
        
        //CustomerIdentity
        assertEquals("test@svea.com", request.request.CreateOrderInformation.CustomerIdentity.Email);
        assertEquals("999999", request.request.CreateOrderInformation.CustomerIdentity.PhoneNumber);
        assertEquals("123.123.123", request.request.CreateOrderInformation.CustomerIdentity.IpAddress);
        assertEquals("Svea bakkerij 123", request.request.CreateOrderInformation.CustomerIdentity.FullName);
        assertEquals("Gatan", request.request.CreateOrderInformation.CustomerIdentity.Street);
        assertEquals("c/o Eriksson", request.request.CreateOrderInformation.CustomerIdentity.CoAddress);
        assertEquals("9999", request.request.CreateOrderInformation.CustomerIdentity.ZipCode);
        assertEquals("23", request.request.CreateOrderInformation.CustomerIdentity.HouseNumber);
        assertEquals("Stan", request.request.CreateOrderInformation.CustomerIdentity.Locality);
        assertEquals(COUNTRYCODE.NL, request.request.CreateOrderInformation.CustomerIdentity.CountryCode);
        assertEquals("Individual", request.request.CreateOrderInformation.CustomerIdentity.CustomerType);
    }
    
    @Test
    public void testInvoiceWithCompanyCustomerFromSe() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addOrderRow(TestingTool.createExVatBasedOrderRow("2"))
            .addCustomerDetails(Item.companyCustomer()
                .setNationalIdNumber("vat234"))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(TestingTool.DefaultTestCurrency)
            .useInvoicePayment()
            .prepareRequest();
        
        assertEquals("vat234", request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber);
        assertEquals(COUNTRYCODE.SE, request.request.CreateOrderInformation.CustomerIdentity.CountryCode);
        assertEquals("Company", request.request.CreateOrderInformation.CustomerIdentity.CustomerType);
    }
    
    @Test
    public void testInvoiceWithProductsRows() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
        .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
        .addFee(TestingTool.createExVatBasedShippingFee())
        .addFee(TestingTool.createExVatBasedInvoiceFee())
        .addCustomerDetails(Item.individualCustomer()
            .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(TestingTool.DefaultTestCurrency)
        .useInvoicePayment()
        .prepareRequest();
        
        //First order row is a product
        assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(0).ArticleNumber);
        assertEquals("Prod: Specification", request.request.CreateOrderInformation.OrderRows.get(0).Description);
        assertEquals(100.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(2, request.request.CreateOrderInformation.OrderRows.get(0).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(0).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(0).DiscountPercent);
        
        //Second order row is shipment
        assertEquals("33", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
        assertEquals("shipping: Specification", request.request.CreateOrderInformation.OrderRows.get(1).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(1).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(1).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(1).DiscountPercent);
        
        //Third order row is invoice fee
        assertEquals("", request.request.CreateOrderInformation.OrderRows.get(2).ArticleNumber);
        assertEquals("Svea fee: Fee for invoice", request.request.CreateOrderInformation.OrderRows.get(2).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(2).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(2).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(2).DiscountPercent);
    }
    
    @Test
    public void testInvoiceWithRelativeDiscountOnDifferentProductVat() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(1)
            .setAmountExVat(240.00)
            .setDescription("CD")
            .setVatPercent(25))
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(1)
            .setAmountExVat(188.68)
            .setDescription("Bok")
            .setVatPercent(6))
        .addDiscount(Item.relativeDiscount()
            .setDiscountId("1")
            .setDiscountPercent(20)
            .setDescription("RelativeDiscount"))
        .addCustomerDetails(Item.individualCustomer()
            .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(TestingTool.DefaultTestCurrency)
        .useInvoicePayment()
        .prepareRequest();
        
        //coupon row
        assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(2).ArticleNumber);
        assertEquals("RelativeDiscount", request.request.CreateOrderInformation.OrderRows.get(2).Description);
        assertEquals(-85.74, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(2).NumberOfUnits);
        assertEquals(16.64, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(2).DiscountPercent); 
    }
    
    @Test
    public void testInvoiceWithFixedDiscountOnDifferentProductVat() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
        .addOrderRow(Item.orderRow()
            .setArticleNumber("1")
            .setQuantity(1)
            .setAmountExVat(240.00)
            .setDescription("CD")
            .setVatPercent(25))
        .addOrderRow(Item.orderRow()
             .setArticleNumber("1")
             .setQuantity(1)
             .setAmountExVat(188.68)
             .setDescription("Bok")
             .setVatPercent(6))
        .addDiscount(Item.fixedDiscount()
             .setDiscountId("1")
             .setAmountIncVat(100.00)
             .setDescription("FixedDiscount"))
        .addCustomerDetails(Item.individualCustomer()
            .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(TestingTool.DefaultTestCurrency)
        .useInvoicePayment()
        .prepareRequest();
        
        //coupon row
        assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(2).ArticleNumber);
        assertEquals("FixedDiscount", request.request.CreateOrderInformation.OrderRows.get(2).Description);
        assertEquals(-85.74, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(2).NumberOfUnits);
        assertEquals(16.64, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(2).DiscountPercent);
    }
    
    @Test
    public void testInvoiceWithCreateOrderInformation() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
        .setClientOrderNumber("33")
        .setCurrency(TestingTool.DefaultTestCurrency)
        .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
        .addCustomerDetails(Item.individualCustomer()
            .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setOrderDate("2012-12-12")
        .useInvoicePayment()
        .prepareRequest();
        
        assertEquals("2012-12-12", request.request.CreateOrderInformation.OrderDate);
        assertEquals("33", request.request.CreateOrderInformation.ClientOrderNumber);
        assertEquals("Invoice", request.request.CreateOrderInformation.OrderType);
    }
    
    @Test
    public void testInvoiceUsingAmountIncVatWithVatPercent() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
        .addCustomerDetails(Item.companyCustomer()
            .setNationalIdNumber(TestingTool.DefaultTestCompanyNationalIdNumber))
        .addOrderRow(TestingTool.createIncVatBasedOrderRow("1"))
        .addFee(TestingTool.createIncVatBasedShippingFee())
        .addFee(TestingTool.createIncVatBasedInvoiceFee())
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency(TestingTool.DefaultTestCurrency)
        .useInvoicePayment()
        .prepareRequest();
        
        //First order row is a product
        assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(0).ArticleNumber);
        assertEquals("Prod: Specification", request.request.CreateOrderInformation.OrderRows.get(0).Description);
        assertEquals(100.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(2, request.request.CreateOrderInformation.OrderRows.get(0).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(0).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(0).DiscountPercent);
        
        //Second order row is shipment
        assertEquals("33", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
        assertEquals("shipping: Specification", request.request.CreateOrderInformation.OrderRows.get(1).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(1).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(1).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(1).DiscountPercent);
        
        //Third order row is invoice fee
        assertEquals("", request.request.CreateOrderInformation.OrderRows.get(2).ArticleNumber);
        assertEquals("Svea fee: Fee for invoice", request.request.CreateOrderInformation.OrderRows.get(2).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(2).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(2).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(2).DiscountPercent);
    }
    
    @Test
    public void testInvoiceUsingAmountIncVatWithAmountExVat() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
            .addOrderRow(TestingTool.createIncAndExVatOrderRow("1")) 
            .addFee(TestingTool.createIncAndExVatShippingFee()) 
            .addFee(TestingTool.createIncAndExVatInvoiceFee())
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
       .setCountryCode(TestingTool.DefaultTestCountryCode)
       .setOrderDate("2012-12-12")
       .setCurrency(TestingTool.DefaultTestCurrency)
       .useInvoicePayment()
       .prepareRequest();
        
        //First order row is a product
        assertEquals("1", request.request.CreateOrderInformation.OrderRows.get(0).ArticleNumber);
        assertEquals("Prod: Specification", request.request.CreateOrderInformation.OrderRows.get(0).Description);
        assertEquals(100.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(2, request.request.CreateOrderInformation.OrderRows.get(0).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(0).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(0).DiscountPercent);
        
        //Second order row is shipment
        assertEquals("33", request.request.CreateOrderInformation.OrderRows.get(1).ArticleNumber);
        assertEquals("shipping: Specification", request.request.CreateOrderInformation.OrderRows.get(1).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(1).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(1).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(1).DiscountPercent);
        
        //Third order row is invoice fee
        assertEquals("", request.request.CreateOrderInformation.OrderRows.get(2).ArticleNumber);
        assertEquals("Svea fee: Fee for invoice", request.request.CreateOrderInformation.OrderRows.get(2).Description);
        assertEquals(50, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(1, request.request.CreateOrderInformation.OrderRows.get(2).NumberOfUnits);
        assertEquals("st", request.request.CreateOrderInformation.OrderRows.get(2).Unit);
        assertEquals(25, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(0, request.request.CreateOrderInformation.OrderRows.get(2).DiscountPercent);
    }
   
    @Test
    public void testInvoiceRequestXML() {
        String xml = WebPay.createOrder()
        .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
        .addOrderRow(TestingTool.createExVatBasedOrderRow("2")) 
        .addCustomerDetails(Item.individualCustomer()
            .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(TestingTool.DefaultTestCurrency)
            .setCustomerReference("33")
            .useInvoicePayment()
            .getXML();
        
        final String expectedXML = "<web:request><web:Auth><web:ClientNumber>79021</web:ClientNumber><web:Username>sverigetest</web:Username><web:Password>sverigetest</web:Password></web:Auth><web:CreateOrderInformation><web:ClientOrderNumber>33</web:ClientOrderNumber><web:OrderRows><web:OrderRow><web:ArticleNumber>1</web:ArticleNumber><web:Description>Prod: Specification</web:Description><web:PricePerUnit>100.0</web:PricePerUnit><web:NumberOfUnits>2</web:NumberOfUnits><web:Unit>st</web:Unit><web:VatPercent>25.0</web:VatPercent><web:DiscountPercent>0</web:DiscountPercent></web:OrderRow><web:OrderRow><web:ArticleNumber>2</web:ArticleNumber><web:Description>Prod: Specification</web:Description><web:PricePerUnit>100.0</web:PricePerUnit><web:NumberOfUnits>2</web:NumberOfUnits><web:Unit>st</web:Unit><web:VatPercent>25.0</web:VatPercent><web:DiscountPercent>0</web:DiscountPercent></web:OrderRow></web:OrderRows><web:CustomerIdentity><web:NationalIdNumber>194605092222</web:NationalIdNumber><web:Email></web:Email><web:PhoneNumber></web:PhoneNumber><web:IpAddress></web:IpAddress><web:FullName></web:FullName><web:Street></web:Street><web:CoAddress></web:CoAddress><web:ZipCode></web:ZipCode><web:HouseNumber></web:HouseNumber><web:Locality></web:Locality><web:CountryCode>SE</web:CountryCode><web:CustomerType>Individual</web:CustomerType></web:CustomerIdentity><web:OrderDate>2012-12-12</web:OrderDate><web:AddressSelector></web:AddressSelector><web:CustomerReference>33</web:CustomerReference><web:OrderType>Invoice</web:OrderType></web:CreateOrderInformation></web:request>";
        
        assertEquals(expectedXML, xml);
    }
    
    @Test
    public void testCompanyIdRequest() {
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder()
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addCustomerDetails(Item.companyCustomer()
                    .setNationalIdNumber("4354kj"))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setClientOrderNumber("33")
                .setOrderDate("2012-12-12")
                .setCurrency(TestingTool.DefaultTestCurrency)
                .useInvoicePayment()
                .prepareRequest();
        
        assertEquals("79021", request.request.Auth.ClientNumber.toString()); 
        assertEquals("4354kj", request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber);
    }
}
