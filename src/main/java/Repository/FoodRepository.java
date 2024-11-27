package Repository;

import Entity.Food;
import Entity.Store;

import java.util.*;

public class FoodRepository {

    private final Map<String, Map<String, Food>> storeFoods = new HashMap<>();

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

    public void addFood(String storeName, Food food) {
        storeFoods.putIfAbsent(storeName, new HashMap<>());
        storeFoods.get(storeName).put(food.getFoodName(), food);
    }


    // 특정 가게의 음식을 이름으로 찾기
    public Food findFoodByFoodName(Store store, String foodName) {
        Map<String, Food> foodMap = storeFoods.get(store.getStoreName());
        if (foodMap != null) {
            return foodMap.get(foodName);
        }
        return null;
    }

    // 모든 음식 정보 출력
    public Collection<Food> findAll() {
        List<Food> allFoods = new ArrayList<>();
        for (Map<String, Food> foodMap : storeFoods.values()) {
            allFoods.addAll(foodMap.values());
        }
        return allFoods;
    }

    // 특정 가게에서 음식을 삭제
    public void removeFood(Store store, Food food) {
        Map<String, Food> foodMap = storeFoods.get(store.getStoreName());
        if (foodMap != null) {
            foodMap.remove(food.getFoodName());
        }
    }


    // 특정 가게의 모든 음식 리스트 찾기
    public List<Food> findFoodsStoreName(String storeName) {
        List<Food> foodsList = new ArrayList<>();
        Map<String, Food> foodMap = storeFoods.get(storeName);
        if (foodMap != null) {
            foodsList.addAll(foodMap.values());
        }
        return foodsList;
    }

}
