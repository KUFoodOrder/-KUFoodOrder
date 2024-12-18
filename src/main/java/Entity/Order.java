package Entity;

import java.util.List;

public class Order {

    private String orderTime; //주문시간
    private String orderId; //주문정보id
    private User user; //주문자정보
    private List<Store> stores; //주문가게리스트
    private List<Food> foods; //주문음식리스트
    private List<Integer> quantitys; //주문한음식당 주문한양 리스트

    public Order(String orderTime, String orderId, User user,List<Store> stores,List<Food> foods, List<Integer> quantitys) {
        this.orderTime = orderTime;
        this.orderId = orderId;
        this.user = user;
        this.stores = stores;
        this.foods = foods;
        this.quantitys = quantitys;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public User getUser() {
        return user;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public List<Store> getStores() {
        return stores;
    }

    public List<Integer> getQuantitys() {
        return quantitys;
    }

    @Override
    public String toString() {
        return "orderTime='" + orderTime + '\'' +
                ", orderId='" + orderId + '\'' +
                ", user=" + user +
                ", foods=" + foods +
                ", quantitys=" + quantitys +
                '}';
    }
}