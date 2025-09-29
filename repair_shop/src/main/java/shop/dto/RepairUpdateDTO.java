package shop.dto;

import java.util.List;

public class RepairUpdateDTO {
    private String name;
    private String status;
    private Double finalPrice;
    private String description;
    private List<Long> partIds; // list of part IDs to update parts

    // Getters and setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(Double finalPrice) { this.finalPrice = finalPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Long> getPartIds() { return partIds; }
    public void setPartIds(List<Long> partIds) { this.partIds = partIds; }
}