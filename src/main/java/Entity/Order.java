package Entity;

import java.util.List;

public class Order {

    private String orderTime; //주문시간
    private String orderId; //주문정보id
    private User user; //주문자정보
    private List<Food> foods; //주문음식리스트
    private List<Integer> quantitys; //주문한음식당 주문한양 리스트

    public Order(String orderTime, String orderId, User user, List<Food> foods, List<Integer> quantitys) {
        this.orderTime = orderTime;
        this.orderId = orderId;
        this.user = user;
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

    public List<Integer> getQuantitys() {
        return quantitys;
    }
}