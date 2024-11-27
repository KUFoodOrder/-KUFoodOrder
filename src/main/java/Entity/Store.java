package Entity;

import java.util.ArrayList;
import java.util.List;

//TODO 2C. 서로 다른 가게에는 서로 같은 이름의 메뉴가 존재할 수 있게 해주세요.
//TODO 2D. 한 가게가 둘 이상의 카테고리에 속할 수 있게 해주세요.
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
