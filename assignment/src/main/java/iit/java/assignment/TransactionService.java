package iit.java.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import iit.java.assignment.TransactionRepo;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;

    public Long addTransaction(Transaction transaction) {
        return transactionRepo.save(transaction).getId();
    }

    // Method to retrieve all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    // Method to retrieve a transaction by its ID
    public Transaction getTransactionById(Long id) {
        Optional<Transaction> optionalTransaction = transactionRepo.findById(id);
        return optionalTransaction.orElse(null); // Return transaction if found, otherwise null
    }

    // Method to update an existing transaction
    public void updateTransaction(Transaction transaction) {
        if (transactionRepo.existsById(transaction.getId())) {
            transactionRepo.save(transaction); // Save the updated transaction
        }
    }

    public void deleteTransaction(Long id) {
            transactionRepo.deleteById(id);
    }
    public List<Transaction> getRecentTransactions(int limit) {
        // Use sublist to limit the number of transactions
        List<Transaction> allTransactions = transactionRepo.findTopN(limit);
        return allTransactions.size() > limit ? allTransactions.subList(0, limit) : allTransactions;
    }

    public double calculateTotalByType(String type) {
        return transactionRepo.findByType(type)
                .stream()
                .map(Transaction::getAmount)
                .reduce(0.0, Double::sum);
    }
    // Method to get filtered transactions based on provided criteria
    public List<Transaction> getFilteredTransactions(String amount, String category) {
        List<Transaction> allTransactions = getAllTransactions();

        return allTransactions.stream()
                .filter(transaction -> (amount == null || String.valueOf(transaction.getAmount()).contains(amount)) &&
                        (category == null || transaction.getCategory().toLowerCase().contains(category.toLowerCase())))
                .collect(Collectors.toList());
    }

}
