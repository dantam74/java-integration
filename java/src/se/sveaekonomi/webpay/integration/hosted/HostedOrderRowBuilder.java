package se.sveaekonomi.webpay.integration.hosted;

public class HostedOrderRowBuilder {

    private String sku;
    private String name;
    private String description;
    private long amount;
    private long vat;
    private Double quantity;
    private String unit;
    
    public HostedOrderRowBuilder() {
        sku = "";
        name = "";
        description = "";
    }
    
    public String getSku() {
        return sku;
    }
    
    public HostedOrderRowBuilder setSku(String sku) {
        this.sku = sku;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public HostedOrderRowBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getDescription() {
        return description;
    }
    
    public HostedOrderRowBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public Long getAmount() {
        return amount;
    }
    
    public HostedOrderRowBuilder setAmount(long amount) {
        this.amount = amount;
        return this;
    }
    
    public Long getVat() {
        return vat;
    }
    
    public HostedOrderRowBuilder setVat(long vat) {
        this.vat = vat;
        return this;
    }
    
    public Double getQuantity() {
        return quantity;
    }
    
    public HostedOrderRowBuilder setQuantity(Double quantity) {
        this.quantity = quantity;
        return this;
    }
    
    /**
     * @return unit (i.e. "pcs", "st" etc)
     */
    public String getUnit() {
        return unit;
    }
    
    /**
     * @param unit of (i.e. "pcs", "st" etc)
     */
    public HostedOrderRowBuilder setUnit(String unit) {
        this.unit = unit;
        return this;
    }
}
