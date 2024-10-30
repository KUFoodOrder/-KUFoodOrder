package Entity;

import java.util.ArrayList;
import java.util.List;

public class Store {

    private int storeCategory; //가게 카테고리 1:한식 2:중식 3:일식
    private String storeName; //가게이름
    private List<Food> storeMenuList = new ArrayList<>(); //음식 메뉴
    private Position storeLocation; //가게 위치

    public Store(int storeCategory, String storeName, Position storeLocation) {
        this.storeCategory = storeCategory;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
    }

    public Store(int storeCategory, String storeName) {
        this.storeCategory = storeCategory;
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

    public int getStoreCategory() {
        return storeCategory;
    }
}
