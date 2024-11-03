package Repository;

import Entity.Food;
import Entity.Store;
import Entity.User;

import java.util.*;

public class FoodRepository {

    private final Map<String, Food> foods = new HashMap<>();
    private static  FoodRepository memoryfoodRepository;

    private FoodRepository() {
    }

    public static FoodRepository getInstance() {
        if ( memoryfoodRepository == null) {
            memoryfoodRepository = new  FoodRepository();
            return  memoryfoodRepository;
        }
        return  memoryfoodRepository;
    }

    public void addFood(Food food) {
        foods.put(food.getFoodName(),food);
    }

    public Food findFoodByName(String foodName) {
        return foods.get(foodName);
    }

    public Collection< Food> findAll() {
        return foods.values();
    }
    public void removeFood(Food food) {
        // Food 리스트에서 해당 음식을 제거
        foods.remove(food.getFoodName());
    }
    public List<Food> findFoodsStoreName(String storeName) {
        List<Food> matchedFoods = new ArrayList<>();
        for (Food food : foods.values()) {
            if (food.getStore().getStoreName().equals(storeName)) {
                matchedFoods.add(food);
            }
        }
        return matchedFoods;          // 해당 가게의 음식 리스트 반환
    }

}
