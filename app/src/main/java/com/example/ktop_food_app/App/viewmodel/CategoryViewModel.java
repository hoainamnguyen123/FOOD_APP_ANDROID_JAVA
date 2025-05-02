package com.example.ktop_food_app.App.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ktop_food_app.App.model.data.entity.Category;
import com.example.ktop_food_app.App.model.data.remote.FirebaseCategoryData;
import com.example.ktop_food_app.App.model.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final CategoryRepository categoryRepository;
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CategoryViewModel() {
        categoryRepository = new CategoryRepository();
        loadCategories();
    }

    private void loadCategories() {
        categoryRepository.getCategoryList(new FirebaseCategoryData.CategoryCallback() {
            @Override
            public void onCategoriesLoaded(List<Category> categoryList) {
                categories.setValue(categoryList);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    public LiveData<List<Category>> getCategoriesLiveData() {
        return categories;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
