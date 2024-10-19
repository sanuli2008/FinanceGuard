package iit.java.assignment;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    CategoryService categoryService;

    @RequestMapping("/Registration")
    public String showRegistration(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "Registration";
    }
    @PostMapping("/registeredUser")
    public String showRegisteredUser(@Valid @ModelAttribute User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "Registration";
        }
        System.out.println(userService.addUser(user));
        return "redirect:/user/UserLogin";
    }
    @GetMapping("/UserLogin")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new User());  // Make sure User object is passed to the view
        return "UserLogin";  // This should be the name of your HTML file without extension
    }
    @PostMapping("/loggedIn")
    public String showLoggedIn(@ModelAttribute User user, BindingResult result, Model model) {
        // Check if username or password is null or empty
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty()) {
            model.addAttribute("error", "Username and password are required.");
            return "UserLogin";
        }

        if (!userService.authenticateUser(user.getUsername(), user.getPassword())) {
            model.addAttribute("error", "Invalid credentials.");
            return "UserLogin";
        }

        return "redirect:/user/Dashboard";
    }

    @GetMapping("/Dashboard")
    public String showDashboard(Model model) {
        List<Transaction> transactions = transactionService.getRecentTransactions(6); // Get 6 most recent transactions
        double totalIncome = transactionService.calculateTotalByType("Income");
        double totalExpenses = transactionService.calculateTotalByType("Expense");

        model.addAttribute("transactions", transactions);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpenses", totalExpenses);

        return "Dashboard"; // Ensure this matches your HTML template name
    }


    @RequestMapping("/TransactionEntry")
    public String showTransactionEntry(Model model) {
        Transaction transaction = new Transaction();
        List<Category> categories = categoryService.getAllCategories(); // Fetch categories
        model.addAttribute("transaction", transaction);
        model.addAttribute("categories", categories); // Add categories to the model
        return "TransactionEntry";
    }

    @PostMapping("/EnteredTrans")
    public String showEnteredTrans(@Valid @ModelAttribute Transaction transaction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "TransactionEntry";
        }
        System.out.println(transactionService.addTransaction(transaction));
        return "redirect:/user/Dashboard";
    }

    @GetMapping("/ManageCategories")
    public String manageCategories(@RequestParam(required = false) String categoryName,
                                   @RequestParam(required = false) String categoryType,
                                   Model model) {
        List<Category> categories;

        if (categoryName != null && !categoryName.isEmpty()) {
            categories = categoryService.findByName(categoryName); // Method to filter by name
        } else if (categoryType != null && !categoryType.isEmpty()) {
            categories = categoryService.findByType(categoryType); // Method to filter by type
        } else {
            categories = categoryService.findAll(); // Method to retrieve all categories
        }

        model.addAttribute("categories", categories);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("categoryType", categoryType);
        return "ManageCategories"; // Your Thymeleaf template name
    }


    @GetMapping("/editCategory/{id}")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "EditCategory";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@Valid @ModelAttribute Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("category", category);
            return "EditCategory"; // Return to the edit form if validation fails
        }
        categoryService.updateCategory(category);
        return "redirect:/user/ManageCategories"; // Redirect to category list
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/user/ManageCategories";
    }
    @RequestMapping("/AddCategory")
    public String showAddCategory(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);
        return "AddCategory";
    }
    @PostMapping("/AddedCat")
    public String showAddedCat(@Valid @ModelAttribute Category category, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Add the category object to the model to repopulate the form with user inputs
            model.addAttribute("category", category);
            return "AddCategory"; // Return to the same form view with error messages
        }
        categoryService.addCategory(category);
        return "redirect:/user/ManageCategories";
    }


    @GetMapping("/TransactionHistory")
    public String showTransactionHistory(@RequestParam(required = false) String amount,
                                         @RequestParam(required = false) String category,
                                         Model model) {
        List<Transaction> transactions = transactionService.getFilteredTransactions(amount, category);
        model.addAttribute("transactions", transactions);
        return "transactionHistory"; // Ensure this matches your Thymeleaf template name
    }
    @GetMapping("/editTransactionForm/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);
        if (transaction == null) {
            // Handle the case when the transaction is not found
            return "redirect:/user/TransactionHistory";
        }
        List<Category> categories = categoryService.getAllCategories(); // Fetch categories
        model.addAttribute("transaction", transaction);
        model.addAttribute("categories", categories); // Add categories to the model
        return "EditTransaction"; // Thymeleaf template name for editing transactions
    }


    @PostMapping("/processEditTransaction")
    public String processEditForm(@Valid @ModelAttribute("transaction") Transaction transaction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "EditTransaction";
        }
        transactionService.updateTransaction(transaction);
        return "redirect:/user/TransactionHistory";
    }

    @GetMapping("/deleteTrans/{id}")
    public String deleteTrans(@PathVariable("id") Long id) {
        transactionService.deleteTransaction(id); // Delete the transaction
        return "redirect:/user/TransactionHistory"; // Redirect to the transaction history page
    }
    @GetMapping("/edit/{id}")
    public String editTransaction(@PathVariable("id") Long id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);
        if (transaction == null) {
            // Handle case when the transaction is not found
            return "redirect:/user/TransactionHistory";
        }
        model.addAttribute("transaction", transaction);
        return "EditTransaction"; // Correct Thymeleaf template name
    }

    @PostMapping("/edit")
    public String updateTransaction(@Valid @ModelAttribute("transaction") Transaction transaction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "EditTransaction"; // Return to the form if validation fails
        }
        transactionService.updateTransaction(transaction);
        return "redirect:/user/TransactionHistory";
    }
    @GetMapping("/deleteTransaction/{id}")
    public String deleteTransaction(@PathVariable("id") Long id) {
        transactionService.deleteTransaction(id); // Call service method to delete transaction
        return "redirect:/user/TransactionHistory"; // Redirect back to the dashboard after deletion
    }

}