package shop.dto;

public class CustomerProfitDTO {
    private String code;
    private String name;
    private Double totalProfit;

    public CustomerProfitDTO(String code, String name, Double totalProfit) {
        this.code = code;
        this.name = name;
        this.totalProfit = totalProfit;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public Double getTotalProfit() { return totalProfit; }
}