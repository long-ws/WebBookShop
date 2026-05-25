package beans;

public class TopShippingMethod {
    private String methodName;
    private String providerType;
    private int totalOrders;
    private double totalRevenue;
    private double usagePercent;
    private int status;

    public TopShippingMethod() {}

    public TopShippingMethod(String methodName, String providerType, int totalOrders, double totalRevenue, double usagePercent, int status) {
        this.methodName = methodName;
        this.providerType = providerType;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.usagePercent = usagePercent;
        this.status = status;
    }

    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public double getUsagePercent() { return usagePercent; }
    public void setUsagePercent(double usagePercent) { this.usagePercent = usagePercent; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
