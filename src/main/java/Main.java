import manager.MenuManager;
import manager.OrderManeger;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        //String id ="kimsohee";
        //String time = "202411111111";
        //OrderManeger.Print_User_Main_Menu(time, id);
        //OrderManeger.getOrderFromUser(time, id);        //매개변수 test용임. 머지해서 getOrderTime(), getOrderId()로 받아오면됨.
        //OrderManeger.check_order_history_from_Admin();
        //OrderManeger.check_order_history_from_User(id);   //매개변수 TEST용임. getOrderId()로 받아오면됨.

        MenuManager.showMenu();
        System.out.println();
    }
}