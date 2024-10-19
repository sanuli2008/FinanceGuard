package iit.java.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    // Using @Query to specify a custom query for ordering and limiting results
    @Query(value = "SELECT t FROM Transaction t ORDER BY t.transactionDate DESC")
    List<Transaction> findTopN(@Param("limit") int limit);

    List<Transaction> findByType(String type);

}
