package shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import shop.dto.ErrorResponse;
import shop.dto.RepairRequestDTO;
import shop.dto.RepairRequestDTO.PartDTO;
import shop.dto.RepairUpdateDTO;
import shop.model.Customer;
import shop.model.Part;
import shop.model.Product;
import shop.model.Repair;
import shop.repository.CustomerRepository;
import shop.repository.PartRepository;
import shop.repository.ProductRepository;
import shop.repository.RepairRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    @Autowired
    private RepairRepository repairRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PartRepository partRepository;

    // Get all repairs
    @GetMapping
    public List<Repair> getAllRepairs() {
        return repairRepository.findAll();
    }

    // Get repair by ID
    @GetMapping("/{id}")
    public ResponseEntity<Repair> getRepairById(@PathVariable Long id) {
        return repairRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new repair
    @PostMapping
    @Transactional
    public ResponseEntity<?> createRepair(@RequestBody RepairRequestDTO repairDTO) {
        // --- Handle Customer ---
        String customerCode = repairDTO.getCustomer().getCustomer_code();
        String customerName = repairDTO.getCustomer().getCustomer_name();

        if (customerCode == null || customerCode.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Customer code must be provided", 400));
        }

        Optional<Customer> existingCustomerOpt = customerRepository.findByCode(customerCode);

        Customer customer;
        if (existingCustomerOpt.isPresent()) {
            // Customer exists, reuse it
            customer = existingCustomerOpt.get();
        } else {
            // Customer doesn't exist, create new
            if (customerName == null || customerName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Customer name must be provided for new customer", 400));
            }
            customer = new Customer();
            customer.setCode(customerCode); // important!
            customer.setName(customerName);
            customer = customerRepository.save(customer);
        }

        // --- Handle Product ---
        String productName = repairDTO.getProduct_name();
        if (productName == null || productName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Product name must be provided", 400));
        }
        Product product = productRepository.findByName(productName).orElseGet(() -> {
            Product p = new Product();
            p.setName(productName);
            return productRepository.save(p);
        });

        Double finalPrice = repairDTO.getFinalPrice();

        // --- Validate status ---
        String status = repairDTO.getStatus();
        if (status == null || status.trim().isEmpty()) {
            status = "Created";
        }

        List<Part> partsList = new ArrayList<>();
        for (RepairRequestDTO.PartDTO pDto : repairDTO.getParts()) {
            String partName = pDto.getPart_name();
            Double partPrice = pDto.getPart_price();

            Optional<Part> existingPartOpt = partRepository.findByName(partName);
            Part part;
            if (existingPartOpt.isPresent()) {
                part = existingPartOpt.get();
                if (partPrice != null && !partPrice.equals(part.getPrice())) {
                    part.setPrice(partPrice);
                    partRepository.save(part);
                }
            } else {
                if (partPrice == null) {
                    return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Part '" + partName + "' does not exist; 'part_price' must be provided for new parts", 400));
                }
                part = new Part();
                part.setName(partName);
                part.setPrice(partPrice);
                part = partRepository.save(part);
            }
            // Add to parts list
            partsList.add(part);
        }

        // --- Create the repair ---
        Repair repair = new Repair();
        repair.setName("Repair for " + customer.getName());
        repair.setCustomer(customer);
        repair.setProduct(product);
        repair.getParts().addAll(partsList);
        repair.setFinalPrice(finalPrice);
        repair.setDescription(repairDTO.getDescription());
        repair.setStatus(status);
        repair.setRepairDate(LocalDate.now());

        return ResponseEntity.ok(repairRepository.save(repair));
    }

    // Update repair
    @PutMapping("/{id}")
    public ResponseEntity<Repair> updateRepair(@PathVariable Long id,
                                               @RequestBody RepairUpdateDTO updateDTO) {
        Optional<Repair> optionalRepair = repairRepository.findById(id);
        if (!optionalRepair.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Repair repair = optionalRepair.get();

        // Update fields if provided
        if (updateDTO.getName() != null) {
            repair.setName(updateDTO.getName());
        }
        if (updateDTO.getStatus() != null) {
            repair.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getFinalPrice() != null) {
            repair.setFinalPrice(updateDTO.getFinalPrice());
        }
        if (updateDTO.getDescription() != null) {
            repair.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getPartIds() != null && !updateDTO.getPartIds().isEmpty()) {
            List<Part> parts = partRepository.findAllById(updateDTO.getPartIds());
            repair.getParts().clear();
            repair.getParts().addAll(parts);
        }

        return ResponseEntity.ok(repairRepository.save(repair));
    }

    // Delete repair
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepair(@PathVariable Long id) {
        if (repairRepository.existsById(id)) {
            repairRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // get repairs by customer code
    @GetMapping("/by-customer/{code}")
    public ResponseEntity<?> getRepairsByCustomerCode(@PathVariable String code) {
        List<Repair> repairs = repairRepository.findByCustomer_Code(code);
        if (repairs.isEmpty()) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse("No repairs found for customer code: " + code, 404));
        }
        return ResponseEntity.ok(repairs);
    }
   
    // Finish repair by id
    @PostMapping("/finish/{id}")
    public ResponseEntity<?> finishRepair(@PathVariable Long id) {
        Optional<Repair> optionalRepair = repairRepository.findById(id);
        if (optionalRepair.isPresent()) {
            Repair repair = optionalRepair.get();
            repair.setStatus("finished");  // set status to "finished"
            repairRepository.save(repair);
            return ResponseEntity.ok(repair);
        } else {
            return ResponseEntity.status(404)
                .body(new ErrorResponse("Repair not found with id: " + id, 404));
        }
    }
    
  // Start repair by id
    @PostMapping("/start/{id}")
    public ResponseEntity<?> startRepair(@PathVariable Long id) {
        Optional<Repair> optionalRepair = repairRepository.findById(id);
        if (optionalRepair.isPresent()) {
            Repair repair = optionalRepair.get();
            repair.setStatus("Repairing");  // set status to "Repairing"
            repairRepository.save(repair);
            return ResponseEntity.ok(repair);
        } else {
            return ResponseEntity.status(404)
                .body(new ErrorResponse("Repair not found with id: " + id, 404));
        }
    }
    
  // fixed repair by id
    @PostMapping("/fixed/{id}")
    public ResponseEntity<?> fixedRepair(@PathVariable Long id) {
        Optional<Repair> optionalRepair = repairRepository.findById(id);
        if (optionalRepair.isPresent()) {
            Repair repair = optionalRepair.get();
            repair.setStatus("Fixed");  // set status to "Repairing"
            repairRepository.save(repair);
            return ResponseEntity.ok(repair);
        } else {
            return ResponseEntity.status(404)
                .body(new ErrorResponse("Repair not found with id: " + id, 404));
        }
    }
    
    // add parts
    @PostMapping("/{id}/add-parts")
    public ResponseEntity<?> addPartsToRepair(
            @PathVariable Long id,
            @RequestBody List<PartDTO> partsDto) {

        Optional<Repair> repairOpt = repairRepository.findById(id);
        if (!repairOpt.isPresent()) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse("Repair not found with id: " + id, 404));
        }

        Repair repair = repairOpt.get();

        for (PartDTO pDto : partsDto) {
            String partName = pDto.getPart_name();
            Double partPrice = pDto.getPart_price();

            Part part;
            // Check if the part exists
            Optional<Part> existingPartOpt = partRepository.findByName(partName);
            if (existingPartOpt.isPresent()) {
                part = existingPartOpt.get();
                // Optionally update price if provided
                if (partPrice != null && !partPrice.equals(part.getPrice())) {
                    part.setPrice(partPrice);
                    partRepository.save(part);
                }
            } else {
                // Part does not exist, check if price is provided
                if (partPrice == null) {
                    return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Part '" + partName + "' does not exist; 'part_price' must be provided for new parts", 400));
                }
                // Create new part
                part = new Part();
                part.setName(partName);
                part.setPrice(partPrice);
                part = partRepository.save(part);
            }

            // Add the part to the repair if not already associated
            if (!repair.getParts().contains(part)) {
                repair.getParts().add(part);
            }
        }

        // Save the updated repair
        return ResponseEntity.ok(repairRepository.save(repair));
    }
    
    // get repairs by date
    @GetMapping("/by-date-range")
    public ResponseEntity<?> getRepairsByDateRange(@RequestParam String start, @RequestParam String end) {
        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            List<Repair> repairs = repairRepository.findByRepairDateBetween(startDate, endDate);
            return ResponseEntity.ok(repairs);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid date format. Use YYYY-MM-DD.", 400));
        }
    }
}