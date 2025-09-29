package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import shop.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCode(String code);
    List<Customer> findByName(String name);

    @Query("SELECT c, SUM(r.finalPrice) as totalProfit " +
           "FROM Customer c JOIN c.repairs r " +
           "GROUP BY c ORDER BY totalProfit DESC")
    List<Object[]> findTopCustomersByProfit(Pageable pageable);
}