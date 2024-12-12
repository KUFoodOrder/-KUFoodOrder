package Entity;

import java.util.ArrayList;
import java.util.List;


public class Store {

    private List<Integer> storeCategories; //가게 카테고리 1:한식 2:중식 3:일식
    private String storeName; //가게이름
    private List<Food> storeMenuList = new ArrayList<>(); //음식 메뉴
    private Position storeLocation; //가게 위치

    public Store(List<Integer> storeCategories, String storeName, Position storeLocation) {
        this.storeCategories = storeCategories;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
    }

    public Store(List<Integer> storeCategories, String storeName) {
        this.storeCategories = storeCategories;
        this.storeName = storeName;
    }


    public void setStoreLocation(Position storeLocation) {
        this.storeLocation = storeLocation;
    }
    public void setStoreMenuList(List<Food> storeMenuList) {
        this.storeMenuList = storeMenuList;
    }

    public List<Food> getStoreMenuList() {
        return storeMenuList;
    }

    public Position getStoreLocation() {
        return storeLocation;
    }


    public String getStoreName() {
        return storeName;
    }

    // 카테고리 리스트 반환
    public List<Integer> getStoreCategories() {
        return storeCategories;
    }

    public int getMenuListSize() {
        return storeMenuList.size();
    }

    public void addFoodToMenu(Food food) {
        if (food == null) {
            throw new IllegalArgumentException("Food 객체는 null일 수 없습니다.");
        }
        storeMenuList.add(food);
    }

    // 음식 이름으로 메뉴에 추가 (이름으로 중복 체크)
    public boolean isAddFoodToMenu(String foodName) {
        // 동일한 이름의 음식이 이미 있는지 확인
        for (Food existingFood : storeMenuList) {
            if (existingFood.getFoodName().equals(foodName)) {
                return false; // 이미 같은 이름의 음식이 존재하면 추가하지 않음
            }
        }
        return true; // 추가 성공
    }

    public boolean removeFoodFromMenu(String foodName) {
        // 음식 이름으로 해당 음식을 찾음
        for (Food food : storeMenuList) {
            if (food.getFoodName().equals(foodName)) {
                storeMenuList.remove(food);  // 찾은 음식을 메뉴에서 제거
                return true;  // 성공적으로 삭제됨
            }
        }
        return false;  // 해당 이름의 음식이 메뉴에 없으면 false 반환
    }


}
