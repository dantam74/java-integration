package se.sveaekonomi.webpay.integration.order.handle;

import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.CreditTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.calculation.MathUtil;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class CreditOrderRowsBuilder extends OrderBuilder<CreditOrderRowsBuilder>{ 
	
	/**
	 * @author Kristian Grossman-Madsen
	 */

	    private ConfigurationProvider config;
	    private COUNTRYCODE countryCode;

	    private ArrayList<Integer> rowIndexesToCredit;
		private ArrayList<NumberedOrderRowBuilder> numberedOrderRows;
//		private ArrayList<OrderRowBuilder> newCreditOrderRows;
	    private String orderId;
	    
		public CreditOrderRowsBuilder( ConfigurationProvider config ) {
			this.config = config;
			this.rowIndexesToCredit = new ArrayList<Integer>();
			this.numberedOrderRows = new ArrayList<NumberedOrderRowBuilder>();
		}

		public ConfigurationProvider getConfig() {
			return config;
		}

		public CreditOrderRowsBuilder setConfig(ConfigurationProvider config) {
			this.config = config;
			return this;
		}

		public COUNTRYCODE getCountryCode() {
			return countryCode;
		}

		public CreditOrderRowsBuilder setCountryCode(COUNTRYCODE countryCode) {
			this.countryCode = countryCode;
			return this;
		}

		public ArrayList<Integer> getRowsToCredit() {
			return rowIndexesToCredit;
		}

		public CreditOrderRowsBuilder setRowToCredit( int rowIndexToCredit ) {
			this.rowIndexesToCredit.add(rowIndexToCredit);
			return this;
		}

		public CreditOrderRowsBuilder setRowsToCredit(ArrayList<Integer> rowIndexesToDeliver) {
			this.rowIndexesToCredit = rowIndexesToDeliver;
			return this;
		}

		public ArrayList<NumberedOrderRowBuilder> getNumberedOrderRows() {
			return numberedOrderRows;
		}

		public CreditOrderRowsBuilder addNumberedOrderRows(ArrayList<NumberedOrderRowBuilder> numberedOrderRows) {
			this.numberedOrderRows = numberedOrderRows;
			return this;
		}

		public String getOrderId() {
			return orderId;
		}

		public CreditOrderRowsBuilder setOrderId(String orderId) {
			this.orderId = orderId;
			return this;
		}
		
		/**
		 * optional, card only -- alias for setOrderId
		 * @param transactionId as string, i.e. as transactionId is returned in HostedPaymentResponse
		 */
	    public CreditOrderRowsBuilder setTransactionId( String transactionId) {        
	        return setOrderId( transactionId );
	    }   
		

		public CreditTransactionRequest creditCardOrderRows() {
		
	    	// validate request and throw exception if validation fails
	        String errors = validateCreditCardOrderRows(); 
	        if (!errors.equals("")) {
	            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
	        }
			
			// calculate credited order rows total, incvat row sum over creditedOrderRows + newOrderRows
			double creditedOrderTotal = 0.0;
			for( Integer rowIndex : new HashSet<Integer>(rowIndexesToCredit) ) {
				NumberedOrderRowBuilder creditedRow = numberedOrderRows.get(rowIndex-1);	// -1 as NumberedOrderRows is one-indexed
				creditedOrderTotal +=  creditedRow.getAmountExVat() * (1+creditedRow.getVatPercent()/100.0) * creditedRow.getQuantity();
			}			
			
			CreditTransactionRequest creditTransactionRequest = new CreditTransactionRequest( this.getConfig() );
			creditTransactionRequest.setCountryCode( this.getCountryCode() );
			creditTransactionRequest.setTransactionId( this.getOrderId() );
			creditTransactionRequest.setCreditAmount((int)MathUtil.bankersRound(creditedOrderTotal) * 100);
			
			return creditTransactionRequest;				
		}
		
		// validates required attributes
	    public String validateCreditCardOrderRows() {
	        String errors = "";
	        if (this.getCountryCode() == null) {
	            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
	        }
	        
	        if (this.getOrderId() == null) {
	            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
	    	}
	        
	        if( this.rowIndexesToCredit.size() == 0 ) {
	        	errors += "MISSING VALUE - rowIndexesToCredit is required for creditCardOrderRows(). Use methods setRowToCredit() or setRowsToCredit().\n";
	    	}
	        
	        if( this.numberedOrderRows.size() == 0 ) {
	        	errors += "MISSING VALUE - numberedOrderRows is required for creditCardOrderRows(). Use setNumberedOrderRow() or setNumberedOrderRows().\n";
	    	}

	        return errors;  
	    }	
	
	
	
}
