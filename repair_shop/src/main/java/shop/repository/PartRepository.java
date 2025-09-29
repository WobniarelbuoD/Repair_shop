package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.model.Part;
import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {
    Optional<Part> findByName(String name);
}