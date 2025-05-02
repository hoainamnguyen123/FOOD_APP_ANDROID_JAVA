package com.example.ktop_food_app.App.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ktop_food_app.App.model.data.entity.Food;
import com.example.ktop_food_app.App.model.data.remote.FirebaseFoodData;
import com.example.ktop_food_app.App.model.repository.FoodRepository;

import java.util.List;

public class FoodViewModel extends ViewModel {
    //FoodViewModel
    private final MutableLiveData<List<Food>> foodListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final FoodRepository foodRepository;

    public FoodViewModel() {
        foodRepository = new FoodRepository();
        loadFoodList();
    }

    public MutableLiveData<List<Food>> getFoodListLiveData() {
        return foodListLiveData;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadFoodList() {
        foodRepository.getFoodList(new FirebaseFoodData.FoodCallback() {
            @Override
            public void onSuccess(List<Food> foodList) {
                foodListLiveData.setValue(foodList);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.setValue(error);
            }
        });
    }
}
