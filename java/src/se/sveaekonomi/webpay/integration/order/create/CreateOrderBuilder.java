package se.sveaekonomi.webpay.integration.order.create;

import java.sql.Date;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.payment.CardPayment;
import se.sveaekonomi.webpay.integration.hosted.payment.DirectPayment;
import se.sveaekonomi.webpay.integration.hosted.payment.HostedPayment;
import se.sveaekonomi.webpay.integration.hosted.payment.PayPagePayment;
import se.sveaekonomi.webpay.integration.hosted.payment.PaymentMethodPayment;
import se.sveaekonomi.webpay.integration.order.CreateBuilderCommand;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.CustomerIdentity;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.validator.OrderValidator;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.webservice.payment.InvoicePayment;
import se.sveaekonomi.webpay.integration.webservice.payment.PaymentPlanPayment;

/**
 * Builds the order up in the fluent api by using create order methods.
 * End by choosing payment type.
 *  
 * @author klar-sar
 */
public class CreateOrderBuilder extends OrderBuilder<CreateOrderBuilder> {
    
    private OrderValidator validator;
    
    private String clientOrderNumber;
    private String customerReference;
    private Date orderDate;
    private COUNTRYCODE countryCode;
    private String currency;
    private String campaignCode;
    private Boolean sendAutomaticGiroPaymentForm;
    public CustomerIdentity<?> customerIdentity; 
    
    public CreateOrderBuilder(ConfigurationProvider config) {
        this.config = config;
    }
    
    public OrderValidator getValidator() {
        return validator;
    }
    
    public CreateOrderBuilder setValidator(OrderValidator validator) {
        this.validator = validator;
        return this;
    }
    
    /**
     * @return Unique order number from client 
     */
    public String getClientOrderNumber() {
        return clientOrderNumber;
    }
    
    public CreateOrderBuilder setClientOrderNumber(String clientOrderNumber) {
        this.clientOrderNumber = clientOrderNumber;
        return this;
    }
    
    public String getCustomerReference() {
        return customerReference;
    }
    
    public CreateOrderBuilder setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
        return this;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }
    
    public CreateOrderBuilder setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
        return this;
    }
    
    public COUNTRYCODE getCountryCode() {
        return countryCode;
    }
    
    public CreateOrderBuilder setCountryCode(COUNTRYCODE countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    
    public String getCurrency() {
        if (currency == null)
            return null;
        else
            return currency.toString();
    }
    
    public CreateOrderBuilder setCurrency(CURRENCY currency) {
        this.currency = currency.toString();
        return this;
    }
    
    public String getCampaignCode() {
        return campaignCode;
    }
    
    public CreateOrderBuilder setCampaignCode(String campaignCode) {
        this.campaignCode = campaignCode;
        return this;
    }
    
    public Boolean getSendAutomaticGiroPaymentForm() {
        return sendAutomaticGiroPaymentForm;
    }
    
    public CreateOrderBuilder setSendAutomaticGiroPaymentForm(Boolean sendAutomaticGiroPaymentForm) {
        this.sendAutomaticGiroPaymentForm = sendAutomaticGiroPaymentForm;
        return this;
    }
    
    /**
     * Start creating card payment via PayPage. Returns Payment form to integrate in shop.
     * @return CardPayment
     */
    public CardPayment usePayPageCardOnly() {
        return new CardPayment(this);
    }
    
    /**
     * Start creating direct bank payment via PayPage. Returns Payment form to integrate in shop.
     * @return DirectPayment
     */
    public DirectPayment usePayPageDirectBankOnly() {
        return new DirectPayment(this);
    }
    
    /**
     * Start creating payment through PayPage. You will need to customize the PayPage.
     * 
     * @return PayPagePayment
     */
    public PayPagePayment usePayPage() {
        return new PayPagePayment(this);
    }
    
    /**
     * Start creating payment with a specific payment method. This method will directly direct to the specified payment method.
     * Payment methods are found in appendix in our documentation.
     * 
     * @param type paymentMethod
     * @return PaymentMethodPayment
     */
    public HostedPayment<?> usePaymentMethod(PAYMENTMETHOD paymentMethod) {
        return new PaymentMethodPayment(this, paymentMethod);
    }
    
    /**
     * Start create invoicePayment
     * @return InvoicePayment
     */
    public InvoicePayment useInvoicePayment() {
        return new InvoicePayment(this);
    }
    
    /**
     * Start creating payment plan payment
     * @param campaignCode
     * @return PaymentPlanPayment
     */
    public PaymentPlanPayment usePaymentPlanPayment(String campaignCode) {
        try {
            if (campaignCode.equals("")) {
                throw new ValidationException("MISSING VALUE - Campaign code must be set. Add parameter in .usePaymentPlanPayment(campaignCode)");
            } else if (this.customerIdentity.getClass().equals(CompanyCustomer.class)) {
                throw new ValidationException("ERROR - CompanyCustomer is not allowed to use payment plan option.");
            }
        } catch(ValidationException e) {
            throw new SveaWebPayException(e.getMessage(), e);
        }
        
        return this.usePaymentPlanPayment(campaignCode, false);
    }
    
    /**
     * Start creating payment plan payment
     * @param campaignCode
     * @param sendAutomaticGiroPaymentForm
     * @return PaymentPlanPayment
     */
    public PaymentPlanPayment usePaymentPlanPayment(String campaignCode, Boolean sendAutomaticGiroPaymentForm) {
        this.campaignCode = campaignCode;
        this.sendAutomaticGiroPaymentForm = sendAutomaticGiroPaymentForm;
        return new PaymentPlanPayment(this);
    }
    
    public CreateOrderBuilder addCustomerDetails(CustomerIdentity<?> customerIdentity) {
        this.customerIdentity = customerIdentity;
        return this;
    }
    
    public boolean getIsCompanyIdentity() {
        if (customerIdentity instanceof CompanyCustomer) {
            return true;
        } else {
            return false;
        }
    }
    
    public CompanyCustomer getCompanyCustomer() {
        if (this.customerIdentity instanceof CompanyCustomer) {
            return (CompanyCustomer)this.customerIdentity;
        } else {
            return null;
        }
    }
    
    public IndividualCustomer getIndividualCustomer() {
        if (this.customerIdentity instanceof IndividualCustomer) {
            return (IndividualCustomer)this.customerIdentity;
        } else {
            return null;
        }
    }
    
    public CustomerIdentity<?> getCustomerIdentity() {
        return this.customerIdentity;
    }
    
    public CreateOrderBuilder build() {
        validator.validate(this);
        return this;
    }
    
    public <T> T run(CreateBuilderCommand<T> runner) {
        return runner.run(this);
    }
}
