package Entity;

//가게마다 같은이름의 메뉴가 추가되는 로직이 생기면서 음식 같은지 확인할때 메뉴이름과 상품이 등록되어있는 가게이름으로 확인해야함
public class Food {

    private Store store; //이 상품이 등록되어있는 가게
    private int foodId; //음식정보id
    private String foodName; //메뉴이름
    private int foodPrice; //메뉴가격
    private int foodQuantity; //총주문한메뉴양

    public Food(Store store,int foodId, String foodName,int foodPrice, int foodQuantity) {
        this.store= store;
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodQuantity = foodQuantity;
    }

    public Food(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodName() {
        return foodName;
    }
    public void setFoodPrice(int foodPrice) {
        this.foodPrice = foodPrice;
    }


    public Store getStore() {
        return store;
    }

    public int getFoodId() {
        return foodId;
    }

    public int getFoodPrice() {
        return foodPrice;
    }

    public int getFoodQuantity() {
        return foodQuantity;
    }
}
