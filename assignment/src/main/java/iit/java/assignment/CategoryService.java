package iit.java.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategoryRepo categoryRepo;
    public Long addCategory(Category category) {
        return categoryRepo.save(category).getId();
    }
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public void updateCategory(Category category) {
        categoryRepo.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }
    public List<Category> findByName(String name) {
        return categoryRepo.findByNameContainingIgnoreCase(name);
    }

    public List<Category> findByType(String type) {
        return categoryRepo.findByType(type);
    }

    public List<Category> findAll() {
        return categoryRepo.findAll();
    }


}
