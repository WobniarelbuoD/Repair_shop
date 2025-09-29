package shop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shop.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
	@Query("SELECT p, COUNT(r) as repairCount " +
	           "FROM Product p JOIN p.repairs r " +
	           "GROUP BY p ORDER BY repairCount DESC")
	    List<Object[]> findTopRepairedProducts(Pageable pageable);
	
	    Optional<Product> findByName(String name);  
}