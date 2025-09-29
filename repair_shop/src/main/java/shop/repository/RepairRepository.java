package shop.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.model.Repair;

public interface RepairRepository extends JpaRepository<Repair, Long> {
	List<Repair> findByRepairDateBetween(LocalDate startDate, LocalDate endDate);
    List<Repair> findByCustomer_Code(String code);
}