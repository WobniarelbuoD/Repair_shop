package shop.dto;

public class ProductRepairCountDTO {
    private String productName;
    private Long repairCount;

    public ProductRepairCountDTO(String productName, Long repairCount) {
        this.productName = productName;
        this.repairCount = repairCount;
    }

    public String getProductName() {
        return productName;
    }

    public Long getRepairCount() {
        return repairCount;
    }
}