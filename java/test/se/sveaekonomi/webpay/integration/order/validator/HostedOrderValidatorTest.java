package se.sveaekonomi.webpay.integration.order.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Date;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.payment.FakeHostedPayment;
import se.sveaekonomi.webpay.integration.order.VoidValidator;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class HostedOrderValidatorTest {
    
    private OrderValidator orderValidator;
    
    public HostedOrderValidatorTest() {
        orderValidator = new HostedOrderValidator();
    }
    
    @Test
    public void validates_all_required_methods_for_usePayPageCardOnly_SE() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(
	        		Item.orderRow()
	                    .setQuantity(1.0)
	                    .setAmountExVat(4)
	                    .setAmountIncVat(5)
            )
            .setCountryCode(COUNTRYCODE.SE)
            .setCurrency(CURRENCY.SEK)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setValidator(new VoidValidator());
        
        assertEquals("", orderValidator.validate(order));
    }    
    
    @Test
    public void validates_missing_required_methods_for_usePayPageCardOnly_SE_addOrderRow() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//            .addOrderRow(
//	        		Item.orderRow()
//	                    .setQuantity(1.0)
//	                    .setAmountExVat(4)
//	                    .setAmountIncVat(5)
//            )
            .setCountryCode(COUNTRYCODE.SE)
            .setCurrency(CURRENCY.SEK)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setValidator(new VoidValidator());
        
        assertEquals("MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n", orderValidator.validate(order));
    }    
    
    @Test
    public void validates_missing_required_methods_for_usePayPageCardOnly_SE_setCountryCode() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(
	        		Item.orderRow()
	                    .setQuantity(1.0)
	                    .setAmountExVat(4)
	                    .setAmountIncVat(5)
            )
//            .setCountryCode(COUNTRYCODE.SE)
            .setCurrency(CURRENCY.SEK)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setValidator(new VoidValidator());
        
        assertEquals("MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n", orderValidator.validate(order));
    }        
    
    @Test
    public void validates_missing_required_methods_for_usePayPageCardOnly_SE_setCurrency() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(
	        		Item.orderRow()
	                    .setQuantity(1.0)
	                    .setAmountExVat(4)
	                    .setAmountIncVat(5)
            )
            .setCountryCode(COUNTRYCODE.SE)
//            .setCurrency(CURRENCY.SEK)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setValidator(new VoidValidator());
        
        assertEquals("MISSING VALUE - Currency is required. Use setCurrency(...).\n", orderValidator.validate(order));
    }    
    
    @Test
    public void validates_missing_required_methods_for_usePayPageCardOnly_SE_setClientOrderNumber() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(
	        		Item.orderRow()
	                    .setQuantity(1.0)
	                    .setAmountExVat(4)
	                    .setAmountIncVat(5)
            )
            .setCountryCode(COUNTRYCODE.SE)
            .setCurrency(CURRENCY.SEK)
//            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setValidator(new VoidValidator());
        
        assertEquals("MISSING VALUE - ClientOrderNumber is required. Use setClientOrderNumber(...).\n", orderValidator.validate(order));
    } 
    
    ////  
    
    
    @Test
    public void testFailOnNullClientOrderNumber() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n"
                    + "MISSING VALUE - ClientOrderNumber is required. Use setClientOrderNumber(...).\n"
                    + "MISSING VALUE - Currency is required. Use setCurrency(...).\n"
                    + "MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                .setValidator(new VoidValidator())
                .build();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnEmptyClientOrderNumber() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" 
                + "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(Item.orderRow()
                .setQuantity(1.0)
                .setAmountExVat(100)
                .setVatPercent(25))
            .setCurrency(TestingTool.DefaultTestCurrency)
            .setClientOrderNumber("")
            .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                .setValidator(new VoidValidator())
                .build();
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailOnMissingReturnUrl() {
        String expectedMessage = "MISSING VALUE - Return url is required, setReturnUrl(...).\n";
        
        try {
            CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                   .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                   .setCurrency(TestingTool.DefaultTestCurrency)
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addFee(Item.shippingFee())
                .addDiscount(Item.fixedDiscount())
                .addDiscount(Item.relativeDiscount());
            
            FakeHostedPayment payment = new FakeHostedPayment(order);
            payment.calculateRequestValues();
            
            //Fail on no exception
            fail();
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }
    
    @Test
    public void succeedOnGoodValuesSe() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setValidator(new VoidValidator())
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setCountryCode(COUNTRYCODE.SE)
            .setCurrency(CURRENCY.SEK)
            .addOrderRow(TestingTool.createMiniOrderRow())
            .addCustomerDetails(TestingTool.createMiniCompanyCustomer());
        
        orderValidator = new HostedOrderValidator();
        assertTrue(orderValidator.validate(order).isEmpty());
    }
    
    @Test
    public void testValidateNLCustomerIdentity() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setCountryCode(COUNTRYCODE.NL)
                .setCurrency(CURRENCY.EUR)
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer());
        
        orderValidator = new HostedOrderValidator();
        assertTrue(orderValidator.validate(order).isEmpty());
    }
    
    @Test
    public void testValidateDECustomerIdentity() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setCountryCode(COUNTRYCODE.DE)
                .setCurrency(CURRENCY.EUR)
                .addOrderRow(TestingTool.createMiniOrderRow())
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer());
        orderValidator = new HostedOrderValidator();
        assertTrue(orderValidator.validate(order).isEmpty());
    }
    
    @Test
    public void testFailVatPercentIsMissing() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n"
            + "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUE - At least one of the values must be set in combination with AmountExVat: AmountIncVat or VatPercent for Orderrow. Use one of: setAmountIncVat() or setVatPercent().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(Item.orderRow()
                        .setQuantity(1.0)
                        .setAmountExVat(100))
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setClientOrderNumber("")
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                .setValidator(new VoidValidator())
                .build();
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailAmountExVatIsMissing() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" +
            "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUE - At least one of the values must be set in combination with VatPercent: AmountIncVat or AmountExVat for Orderrow. Use one of: setAmountExVat() or setAmountIncVat().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(Item.orderRow()
                        .setQuantity(1.0)
                        .setVatPercent(25))
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setClientOrderNumber("")
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                        .setValidator(new VoidValidator())
                .build();
        
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailAmountExVatAndVatPercentIsMissing() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" +
            "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUE - At least one of the values must be set in combination with AmountIncVat: AmountExVat or VatPercent for Orderrow. Use one of: setAmountExVat() or setVatPercent().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(Item.orderRow()
                        .setQuantity(1.0)
                        .setAmountIncVat(125))
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setClientOrderNumber("")
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                        .setValidator(new VoidValidator())
                .build();
        
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testValidateFailOrderIsNull() {
        String expectedMessage = "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n" +
            "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n"
            + "MISSING VALUES - AmountExVat, Quantity and VatPercent are required for Orderrow. Use setAmountExVat(), setQuantity() and setVatPercent().\n";
        
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(null)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .setClientOrderNumber("")
                .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
                .setValidator(new VoidValidator())
                .build();
        
        orderValidator = new HostedOrderValidator();
        
        assertEquals(expectedMessage, orderValidator.validate(order));
    }
    
    @Test
    public void testFailMissingIdentityInHostedNL() {
        String expectedMsg = "MISSING VALUE - Initials is required for individual customers when countrycode is NL. Use setInitials().\n"
                + "MISSING VALUE - Birth date is required for individual customers when countrycode is NL. Use setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is NL. Use setName().\n" 
                + "MISSING VALUE - Street address and house number is required for all customers when countrycode is NL. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is NL. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is NL. Use setZipCode().\n";
        
        try {
            WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addDiscount(TestingTool.createRelativeDiscount())
                .addCustomerDetails(Item.individualCustomer())
                .setCountryCode(COUNTRYCODE.NL)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .usePaymentMethod(PAYMENTMETHOD.INVOICE)
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
            
            //Fail on no exception
            fail();
        } catch (Exception e) {
            assertEquals(expectedMsg, e.getCause().getMessage());
        }
    }
    
    @Test
    public void testFailMissingIdentityInHostedDE() {
        String expectedMsg = "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use setBirthDate().\n"
                + "MISSING VALUE - Name is required for individual customers when countrycode is DE. Use setName().\n"
                + "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use setStreetAddress().\n"
                + "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use setLocality().\n"
                + "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use setCustomerZipCode().\n";
        
        try {
            WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addDiscount(TestingTool.createRelativeDiscount())
                .addCustomerDetails(Item.individualCustomer())
                .setCountryCode(COUNTRYCODE.DE)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .usePaymentMethod(PAYMENTMETHOD.INVOICE)
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
            
            //Fail on no exception
            fail();
        } catch (Exception e) {
            assertEquals(expectedMsg, e.getCause().getMessage());
        }
    }
}
