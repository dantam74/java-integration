package se.sveaekonomi.webpay.integration;

import java.util.List;
import java.util.Map;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.calculation.Helper;
import se.sveaekonomi.webpay.integration.webservice.getaddresses.GetAddresses;
import se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams.GetPaymentPlanParams;

/**
 * @author klar-sar, Kristian Grossman-Madsen
 */
public class WebPay {
    
    /**
     * Use WebPay.createOrder() to create an order using invoice, payment plan, card, or direct bank payment methods. 
     * You may also send the customer to the PayPage, where they may select from all available payment methods.
     *
     * See the CreateOrderBuilder class for more info on methods used to specify the order builder contents, including
     * order rows items et al, and then specifying which payment method to use, followed by sending the request to Svea
     * using doRequest, and parsing the response received from Svea.
     *
     * Invoice and payment plan orders will perform a synchronous payment request, and will return a response 
     * object immediately following the doRequest call.
     * 
     * Hosted payment methods like Card, Direct Bank and any payment methods accessed via the PayPage, are asynchronous.
     * Having selecting an asynchronous payment method you generally use a request class method to get a payment form 
     * object in return. The form is then posted to Svea, where the customer is redirected to the card payment provider 
     * service or bank. After the customer completes the payment, a response is sent back to your provided return url, 
     * where it can be processed and inspected.
     * 
     * Card, Direct Bank, and other hosted methods accessed via PayPage are asynchronous. Asynchronous payment methods
     * provide an html form containing a formatted message to send to Svea, which in turn will send a request response 
     * to the specified return url, where the response can be parsed using the SveaResponse class. You should also be
     * prepared to receive the request response on the specified alternative callback url which is used, amongst others,
     * if i.e. the customer does not return to the store after the order payment have been completed.
     * 
     * To create an invoice or partpayment order using useInvoicePayment or usePaymentPlanPayment, you do not need to 
     * explicitly specify which payment methods are available. 
     * 
     * When creating a card or direct bank order, you can minimize the number of steps in the checkout process by 
     * explicitly specifying i.e. usePaymentMethod(PAYMENTMETHOD.KORTCERT) instead of going through useCardPayment.
     * 
     * Get an order builder instance using the WebPay.deliverOrder entrypoint, then provide more information about the 
     * transaction using DeliverOrderBuilder methods: 
     * 
     * When redirecting the customer to the PayPage, you can use methods in PayPagePayment, i.e. excludePaymentMethods, 
     * to first specify which available payment methods to show or exclude, followed by the doRequest call.
     * 
     * ...
     *      CreateOrderBuilder orderbuilder = WebPay.createOrder(config)
     *       	.addOrderRow()              		// required, see WebPayItem.orderRow() for order row specification
     *          .addFee()         			   		// optional, see WebPayItem for invoice, shipping fee
     *          .addDiscount()          			// optional, see WebPayItem for fixed, relative discount
	 *		    .addCustomerDetails()    			// required for invoice and payment plan payments, card getPaymentUrl. See WebPayItem for individual, company customer
     *          .setCountryCode()               	// required
     *          .setOrderDate()            			// required for invoice and payment plan payments
     *          .setCurrency()                 		// required for card payment, direct bank & PayPage payments only. Ignored for invoice and payment plan.
     *          .setClientOrderNumber()    			// required for card payment, direct payment, PaymentMethod & PayPage payments, max length 30 chars.
     *          .setCustomerReference()    			// optional, ignored for card & direct bank orders, max length 30 chars.
     *      ;
     *      
     *      // then select a synchronous payment method (invoice, part payment) request class and send request
     *      response = orderbuilder.useInvoicePayment().doRequest();    	// returns CreateOrderResponse
     *      response = orderbuilder.usePaymentPlanPayment().doRequest();	// returns CreateOrderResponse
     *      
     *      // or select an asynchronous payment method (card, direct bank et al.) request class
     *      request = orderbuilder
     *      	.usePaymentMethod(PAYMENTMETHOD.KORTCERT)	// returns HostedPayment<?>, use WebPay.listPaymentMethods() to fetch available payment methods
	 *			.usePayPage()								// returns PayPagePayment
     *      	.usePayPageCardOnly()						// returns CardPayment
     *      	.usePayPageDirectBankOnly()					// returns DirectPayment
     *      ;
     *      // then perform any additional asynchronous request settings needed and receive request information
     *      request.
	 *			.setReturnUrl()						// required
     *      	.setCallbackUrl()					// optional but recommended
     *      	.setCancelUrl()						// optional, applies to paypage only
     *      	.setPayPageLanguageCode()			// optional, defaults to english
     *      	.setSubscriptionType()				// optional, card only, used to setup recurring payments
     *      	.setSubscriptionId()				// required for card doRecur request
     *      ;
     *      form = request.getPaymentForm();		// returns PaymentForm object containing request html form
     *      url = request.getPaymentUrl();			// returns PaymentUrl object containing url to prepared payment request
     *      response = request.doRecur();			// performs synchronous request, returns RecurTransactionResponse
     * ...
     * 
     */
	public static CreateOrderBuilder createOrder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }
    	
        return new CreateOrderBuilder(config);
    }  
    
    /**
     * Use the WebPay.deliverOrder() entrypoint when you deliver an order to the customer. 
     * Supports Invoice, Payment Plan and Card orders. (Direct Bank orders are not supported.)
     * 
     * The deliver order request should generally be sent to Svea once the ordered 
     * items have been sent out, or otherwise delivered, to the customer. 
     * 
     * For invoice and partpayment orders, the deliver order request triggers the 
     * invoice being sent out to the customer by Svea. (This assumes that your account
     * has auto-approval of invoices turned on, please contact Svea if unsure). 
     * 
     * For card orders, the deliver order request confirms the card transaction, 
     * which in turn allows nightly batch processing of the transaction by Svea.  
     * (Delivering card orders is only needed if your account has auto-confirm
     * turned off, please contact Svea if unsure.)
     * 
     * To deliver an invoice, partpayment or card order in full, you do not need to 
     * specify order rows. To partially deliver an order, the recommended way is to
     * use WebPayAdmin.deliverOrderRows().
     *  
     * Get an order builder instance using the WebPay.deliverOrder entrypoint, then
     * provide more information about the transaction using DeliverOrderBuilder methods: 
     * 
     * ...
     * 		DeliverOrderBuilder request = WebPay.deliverOrder(config)
     *          .setOrderId()                  // invoice or payment plan only, required
     *          .setTransactionId()            // card only, optional, alias for setOrderId 
     *          .setCountryCode()              // required
     *          .setInvoiceDistributionType()  // invoice only, required
     *          .setNumberOfCreditDays()       // invoice only, optional
     *          .setCaptureDate()              // card only, optional
     *          .addOrderRow()                 // deprecated, optional -- use WebPayAdmin.deliverOrderRows instead
     *          .setCreditInvoice()            // deprecated, optional -- use WebPayAdmin.creditOrderRows instead
     *      ;
     *      // then select the corresponding request class and send request
     *      response = request.deliverInvoiceOrder().doRequest();       // returns DeliverOrderResponse
     *      response = request.deliverPaymentPlanOrder().doRequest();   // returns DeliverOrderResponse
     *      response = request.deliverCardOrder().doRequest();          // returns ConfirmTransactionResponse
     * ...
     * 
     * @return DeliverOrderBuilder
     */
    public static DeliverOrderBuilder deliverOrder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }
    	
        return new DeliverOrderBuilder(config);
    }
    
	/**    
	 *   ### 6.3 WebPay.getAddresses() <a name="i63"></a>
	 *   <!-- WebPay.getAddresses() docblock below, replace @see with apidoc links -->
	 *   Use the WebPay.getAddresses() entrypoint to fetch a list of addresses associated with a given customer identity. Company addresses additionally has an AddressSelector attribute that uniquely identifies the address. Only applicable 
	 *   for SE, NO and DK invoice and part payment orders. Note that in Norway, company customers only are supported.
	 *
	 *   Get an instance using the WebPayAdmin.getAddresses entrypoint, then provide more information about the customer and send the request using the GetAddresses methods:
	 *
	 *   Use setCountryCode() to supply the country code that corresponds to the account credentials used for the address lookup. Note that this means that you cannot look up a user in a foreign country, this is a consequence of the fact 
	 *   that the invoice and partpayment payment methods don't support foreign orders.
	 *
	 *   Use setCustomerIdentifier() to provide the exact credentials needed to identify the customer according to country:
	 *
	 *   	* SE: Personnummer (private individual) or Organisationsnummer (company or other legal entity)
	 *   	* NO: Organisasjonsnummer (company or other legal entity)
	 *   	* DK: Cpr.nr (private individual) or CVR-nummer (company or other legal entity)
	 *
	 *   Then use either getIndividualAddresses() or getCompanyAddresses() to get an instance of the GetAddresses request.
	 *
	 *   The final doRequest() will then send the getAddresses request to Svea and return a GetAddressResponse. Then use methods getIndividualCustomers() or getCompanyCustomers() to get a list of IndividualCustomer or CompanyCustomer addresses.
	 *
	 *   #### 6.3.1 Request example
	 *   ```java
	 *   		GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
	 *   	   		.setCountryCode()             		// required -- supply the country code that corresponds to the account credentials used
	 *       		.setCustomerIdentifier() 			// required -- social security number, company vat number etc. used to identify customer
	 *   	    	.getIndividualAddresses()           // required -- lookup the address of a private individual customer
	 *   	    	//.getCompanyCustomer()    			// required -- lookup the address of a company customer
	 *   		;
	 *
	 *   		// then select the corresponding request class and send request
	 *   		GetAddressesResponse response = request.doRequest();
	 *   	
	 *   		// get the list of customer addresses from the response using either getIndividualCustomers() or getCompanyCustomers()
	 *   		ArrayList<IndividualCustomer> addresses = response.getIndividualCustomers();
	 *      		ArrayList<CompanyCustomer> addresses = response.getCompanyCustomers(); 
	 *   ```
	 *
	 *   #### 6.3.2 Deprecated methods
	 *   The following methods are deprecated starting with 1.6.1 of the package:
	 *
	 *   ```java
	 *   		//GetAddresses
	 *   		.setIndividual()                    // deprecated -- lookup the address of a private individual, set to i.e. social security number)
	 *   		.setCompany()                       // deprecated -- lookup the addresses associated with a legal entity (i.e. company)
	 *   		.setOrderTypeInvoice()              // deprecated -- supply the method that corresponds to the account credentials used for the address lookup
	 *   		.setOrderTypePaymentPlan()          // deprecated -- supply the method that corresponds to the account credentials used for the address lookup
	 *   		
	 *   		//GetAddressResponse
	 *   		.getXXX()							// deprecated -- get value of customer attribute XXX for first associated customer address
	 *   ```
	 *
	 *   (Note that if your integration is currently set to use different (test/production) credentials for invoice and payment plan you may need to use the 
	 *   deprecated methods setOrderTypeInvoice() or setOrderTupePaymentPlan() to explicity state which ConfigurationProvider credentials to be use in request.)
	 */
    public static GetAddresses getAddresses(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }
    	
        return new GetAddresses(config);
    }



	/**
     * Use getPaymentPlanParams() to fetch all campaigns associated with a given client number before creating the payment plan payment.
     * 
     * ```java
     * ...
     * 	// set countrycode and supply client credentials
     * 	GetPaymentPlanParams request = WebPay.getPaymentPlanParams(config)
     * 		.setCountryCode()				// required
     * 	;
     * 
     * 	// receive response and get CampaignCodes from response
     * 	PaymentPlanParamsResponse response = request->doRequest();
     *  List<CampaignCode> campaignCodes = response.getCampaignCodes(); 
     * ...
     * ```
     * See also class CampaignCode for individual campaign attributes.
     */
    public static GetPaymentPlanParams getPaymentPlanParams(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }
    	
        return new GetPaymentPlanParams(config);
    }
    
    // deprecated below    
    /** @deprecated -- use Helper.paymentPlanPricePerMonth() instead */
    public static List<Map<String, String>> paymentPlanPricePerMonth(Double amount, PaymentPlanParamsResponse params, Boolean ignoreMaxAndMinFlag ) {
        return Helper.paymentPlanPricePerMonth(amount, params, ignoreMaxAndMinFlag);
    }
    /** @deprecated -- use Helper.paymentPlanPricePerMonth() instead */
    public static List<Map<String, String>> paymentPlanPricePerMonth(Double amount, PaymentPlanParamsResponse params) {
        return Helper.paymentPlanPricePerMonth(amount, params, false);
    }

    /**
     * Start building request to close order.
     * @param config
     * @return CloseOrderBuilder
     * @deprecated Since 1.6.0 -- use WebPayAdmin.cancelOrder() instead 
     */
    public static CloseOrderBuilder closeOrder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }
    	
        return new CloseOrderBuilder(config);
    }

    
    // TODO remove in 2.0    
    /**
     * Start build order request to create an order for all payments.
     * @return CreateOrderBuilder
     * 
     * @deprecated A configuration must be provided. For testing purposes use {@link SveaConfig.GetDefaultConfig()}.
     */
    @Deprecated
    public static CreateOrderBuilder createOrder() {
        return createOrder(null); 
    }
    /**
     * Start building request to close order.
     * @return CloseOrderBuilder
     * 
     * @deprecated A configuration must be provided. For testing purposes use {@link SveaConfig.GetDefaultConfig()}.
     */
    @Deprecated
    public static CloseOrderBuilder closeOrder() {
        return closeOrder(null);
    }
    /**
     * Starts building request for deliver order.
     * @return DeliverOrderBuilder
     * 
     * @deprecated A configuration must be provided. For testing purposes use {@link SveaConfig.GetDefaultConfig()}.
     */
    @Deprecated
    public static DeliverOrderBuilder deliverOrder() {
        return deliverOrder(null);
    }
    /**
     * Get payment plan parameters to present to customer before creating a payment plan payment request
     * @return GetPaymentPlanParams
     * 
     * @deprecated A configuration must be provided. For testing purposes use {@link SveaConfig.GetDefaultConfig()}.
     */
    @Deprecated
    public static GetPaymentPlanParams getPaymentPlanParams() {
        return getPaymentPlanParams(null);
    }
    /**
     * Start building request for getting addresses.
     * @return GetAddresses
     * 
     * @deprecated A configuration must be provided. For testing purposes use {@link SveaConfig.GetDefaultConfig()}.
     */
    @Deprecated
    public static GetAddresses getAddresses() {
        return getAddresses(null);
    }
}
