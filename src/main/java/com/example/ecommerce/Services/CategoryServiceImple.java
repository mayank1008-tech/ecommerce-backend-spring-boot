package com.example.ecommerce.Services;

import com.example.ecommerce.Exceptions.APIexception;
import com.example.ecommerce.Model.Category;
import com.example.ecommerce.Repository.CategoryRepository;
import com.example.ecommerce.payload.CategoryDTO;
import com.example.ecommerce.payload.CategoryResponse;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.ecommerce.Exceptions.ResourceNotFound;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImple implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,  String sortBy, String sortOrder) {

        Sort sorted = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pagedetails = PageRequest.of(pageNumber,pageSize, sorted);
        Page<Category> categoryPage = categoryRepository.findAll(pagedetails);
        List<Category> categories = categoryPage.getContent();

        if(categories.isEmpty()){
            throw new APIexception("No category created till now!!!");
        }

        List<CategoryDTO> categoryDTOs= categories.stream()
                .map(Category -> modelMapper.map(Category, CategoryDTO.class))
                .toList();
        CategoryResponse res =  new CategoryResponse();
        res.setContent( categoryDTOs);

        //To set JSON metdata
        res.setPageNumber(categoryPage.getNumber());
        res.setPageSize(categoryPage.getSize());
        res.setTotalElements(categoryPage.getTotalElements());
        res.setTotalPages(categoryPage.getTotalPages());
        res.setLastPage(categoryPage.isLast());
        return res;
    }
//*******************************************************************************************

//Old way without DTOS
//    @Override
//    public void createCategory(Category category) {
//        Category temp = categoryRepository.findByCategoryName(category.getCategoryName());
//        if(temp != null) {
//            throw new APIexception("Category with categoryName "+category.getCategoryName()+" already exists");
//        }
//        else{
//        categoryRepository.save(category);}
//}


//New way using DTO
    @Override
    public void createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO,Category.class);
        Category temp = categoryRepository.findByCategoryName(category.getCategoryName());
        if(temp != null) {
            throw new APIexception("Category with categoryName "+categoryDTO.getCategoryName()+" already exists");
        }
        else{
            categoryRepository.save(category);
        }
    }
//*************************************************************************************************
    @Override
    public String removeCategory(Long id) {
        Optional<Category> fetchedcategory = categoryRepository.findById(id); //Checks only given id
        Category temp = fetchedcategory   //Checks if not null
                .orElseThrow(() -> new ResourceNotFound("Category","categoryid",id));  //Our excep
        categoryRepository.delete(temp);
        return "category with category id:- "+temp.getCategoryId()+" is removed";
    }
//**************************************************************************************************

//Old way without DTO
//    @Override
//    public Category updateCategory(Category category, Long id) {
//        //Checks only given id
//        Category temp = categoryRepository.findById(id) //Checks if not null
//                .orElseThrow(() -> new ResourceNotFound("Category","categoryid",id));
//
////        List<Category> categories = categoryRepository.findAll();  //This method checks all rows
////        Category temp  = categories.stream()
////                .filter(c -> c.getCategoryId().equals(id))
////                .findFirst().orElseThrow(()->new ResponseStatusException
////                        (HttpStatus.NOT_FOUND, "Category not found"));
////
////        Category updated=temp;
//        if(temp!=null){
//            temp.setCategoryName(category.getCategoryName());
//            temp = categoryRepository.save(temp);
//        }
//        return temp;
//    }

//  New way using DTO
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long id) {
        //Checks only given id
        Category temp = categoryRepository.findById(id) //Checks if not null
                .orElseThrow(() -> new ResourceNotFound("Category","categoryid",id));

        if(temp!=null){
            temp.setCategoryName(categoryDTO.getCategoryName());
            temp = categoryRepository.save(temp);
        }
        CategoryDTO savedCategoryDto = modelMapper.map(temp,CategoryDTO.class);
        return savedCategoryDto;
    }
}
