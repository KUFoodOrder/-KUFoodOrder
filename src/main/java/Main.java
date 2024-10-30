import manager.MenuManager;
import manager.OrderManeger;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String id ="kang123";
        String time = "202411111111";
        //OrderManeger.getOrderFromUser(time, id);        //test용임. 머지해서 getOrderTime(), getOrderId()사용
        //OrderManeger.check_order_history_from_Admin();
        OrderManeger.check_order_history_from_User(id);

        //MenuManager.showMenu();
        System.out.println();
    }
}