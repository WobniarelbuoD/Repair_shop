package shop.dto;

import java.util.ArrayList;
import java.util.List;

public class RepairRequestDTO {
    private CustomerDTO customer;
    private String product_name;
    private List<PartDTO> parts = new ArrayList<>(); // initialize to avoid null
    private double final_price;
    private String description;
    private String status;

    // Getters and setters
    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public List<PartDTO> getParts() {
        return parts;
    }

    public void setParts(List<PartDTO> parts) {
        this.parts = parts;
    }

    public double getFinalPrice() {
        return final_price;
    }

    public void setFinalPrice(double final_price) {
        this.final_price = final_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Inner class for Customer data
    public static class CustomerDTO {
        private String customer_code;
        private String customer_name;

        public String getCustomer_code() {
            return customer_code;
        }

        public void setCustomer_code(String customer_code) {
            this.customer_code = customer_code;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }
    }

    // Inner class for Part data
    public static class PartDTO {
        private String part_name;
        private Double part_price; // nullable, optional for existing parts

        public String getPart_name() {
            return part_name;
        }

        public void setPart_name(String part_name) {
            this.part_name = part_name;
        }

        public Double getPart_price() {
            return part_price;
        }

        public void setPart_price(Double part_price) {
            this.part_price = part_price;
        }
    }
}