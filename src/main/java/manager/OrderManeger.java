package manager;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import Entity.*;
import Repository.FoodRepository;
import Repository.OrderRepository;
import Repository.StoreRepository;
import Repository.UserRepository;

import javax.lang.model.element.QualifiedNameable;


public class OrderManeger {
    static RegexManager regexManager = new RegexManager();
    static CsvManager csvManager = new CsvManager();

    static List<List<String>> List_Store = new ArrayList<>();   //카테고리 고른 후 해당 카테고리 가게 저장. ex) [[1, 건국쌈밥], [1, 건국밥상]]
    static List<String> List_Menu = new ArrayList<>();    //가게 고른 후 해당 가게의 메뉴 리스트 ex) [짬뽕, 짜장면, 볶음밥]
    static List<List<String>> Confirmed_order = new ArrayList<>();     //최종 주문 확정 리스트. orderData.csv에 삽입할 정보들

    public static void getOrderFromUser(String time, String id) {
        Confirmed_order.clear();
        int keep_order = 1;         //첫번째 주문!!
        while (true) {
            //카테고리, 가게, 메뉴 선택
            int Category_user_selected = getCategoryFromUser();
            int Store_user_selected = getStoreFromUser(Category_user_selected);
            int Menu_user_selected = getMenuFromUser(Store_user_selected);
            //리스트 내부 확인 테스트
            //System.out.println("List_Store is " + List_Store);
            //System.out.println("List_Menu is " + List_Menu);


            //수량 파악
            int Quantity = Quantity_check();

            //주문 확정된 메뉴 Confirmed_order에 넣기, 그리고 주문한 내용 출력
            //리스트 양식: 카테고리 가게이름 메뉴 개수 개당가격  ex) [1, 건국밥상, 김치찌개정식, 5, 9500]
            pushToConfirmed(Category_user_selected, Store_user_selected, Menu_user_selected, Quantity, keep_order);


            //총 주문 목록 테스트용 출력
            //System.out.println("keep_order = " + keep_order);
            //System.out.println("Confirmed_order = " + Confirmed_order);


            //추가 주문 여부 체크
            keep_order = Keep_Order_Check(keep_order);
            if (keep_order==0) break;                   //더 이상 추가 주문 안할거면 break
            keep_order++;
        }
        //주문서 출력
        Print_Bill(Confirmed_order);

        //순번이랑 음식, 수량 리스트 제작 / user id 제작
        String cur_max = check_max();
        List<Food> foodList = new ArrayList<>(); // Food 객체를 저장할 리스트
        List<Integer> quantityList = new ArrayList<>(); // 개수를 저장할 리스트
        FoodRepository foodRepository = FoodRepository.getInstance();
        for (List<String> row : Confirmed_order) {
            String menuName = row.get(2); // 각 행의 메뉴 이름
            int quantity = Integer.parseInt(row.get(3)); // 각 행의 개수
            // 음식 이름으로 Food 객체를 검색하여 foodList에 추가
            Food food = foodRepository.findFoodByName(menuName);
            if (food != null) {
                foodList.add(food); // 음식 객체 리스트에 추가
                quantityList.add(quantity); // 개수 리스트에 추가
            } else {
                System.out.println("음식 '" + menuName + "'을 찾을 수 없습니다.");
            }
        }
        UserRepository userRepository = UserRepository.getInstance();
        System.out.println("id = " + id);
        User user_id = userRepository.findUserById(id);
        System.out.println("User = " + user_id);
        //주문 확정할건지 체크 확정 시 1, 취소 시 0 리턴
        if (1 == getConfirmFromUser()) {
            OrderRepository orderRepository = csvManager.readOrderCsv();

            // 새 주문 객체 생성
            Order newOrder = new Order(time, cur_max, user_id, foodList, quantityList);
            orderRepository.addOrder(newOrder); // OrderRepository에 추가

            System.out.println(newOrder.getOrderId());
            System.out.println(newOrder.getOrderTime());
            System.out.println(newOrder.getUser());
            System.out.println(newOrder.getFoods());
            System.out.println(newOrder.getQuantitys());



            // 새로운 주문을 파일에 추가
            //csvManager.writeOrderCsv(orderRepository);

            System.out.println("주문이 완료되었습니다. 엔터 키를 누르면 고객 메뉴로 돌아갑니다.");
        } else {
            System.out.println("주문이 완료되지 않았습니다. 엔터 키를 누르면 고객 메뉴로 돌아갑니다.");
        }

//        //주문 확정할건지 체크 확정 시 1, 취소 시 0 리턴
//        if (1 == getConfirmFromUser()) {
//            //지금 orderData.csv에 max 순번 찾고 다음 순번으로 리턴
//            String cur_max = check_max();
//            //시간 순번 id 메뉴 수량 순으로 orderData.csv에 append
//            try (FileWriter writer = new FileWriter("src/main/java/dataInfo/orderData.csv", true)) {
//                for (List<String> row : Confirmed_order) {
//                    String line = String.join(",", time, cur_max, id, row.get(2), row.get(3));
//                    writer.write(line + "\n");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println("주문이 완료되었습니다. 엔터 키를 누르면 고객 메뉴로 돌아갑니다.");
//        }
//        else {
//            System.out.println("주문이 완료되지 않았습니다. 엔터 키를 누르면 고객 메뉴로 돌아갑니다.");
//        }
        Scanner sc = new Scanner(System.in);
        sc.nextLine();  // 사용자가 Enter 키를 누를 때까지 대기
        sc.close();





    }

    //commit test 1


    //TODO 카테고리 선택
    private static int getCategoryFromUser() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            Print_Category();
            String input = sc.nextLine();

            if (regexManager.checkMenu(input, 3)) {
                int Category_user_selected = Integer.parseInt(input);
                System.out.println(Category_user_selected + "번을 선택하셨습니다.");
                return Category_user_selected;
            }
        }
    }

    //카테고리 출력
    private static void Print_Category() {
        System.out.println("----------고객 카테고리 입장----------");
        System.out.println("1. 한식");
        System.out.println("2. 중식");
        System.out.println("3. 일식");
        System.out.println("-----------------------------------");
        System.out.println("카테고리 번호를 입력해주세요.");
        System.out.print(">");
    }

    //TODO 가게 선택
    private static int getStoreFromUser(int x) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            Print_Store(x);
            String input = sc.nextLine();
            if (regexManager.checkMenu(input, List_Store.size())) {
                int Store_user_selected = Integer.parseInt(input);
                System.out.println(Store_user_selected + "번을 선택하셨습니다.");
                return Store_user_selected;
            }
        }
    }

    //가게 출력
    private static void Print_Store(int category) {        //같은 가게 중복이 없어서 storeData.csv 사용.
        StoreRepository storeRepository = csvManager.readStoreCsv();
        List_Store.clear();

        int numbering = 1;
        System.out.println("----------가게 선택----------");
        // 해당 카테고리의 가게만 출력
        for (Store store : storeRepository.findAll()) {
            if (store.getStoreCategory() == category) { // 카테고리 일치 여부 확인
                System.out.print(numbering++ + ". ");
                System.out.println(store.getStoreName().trim());

                // 카테고리랑 가게 이름 List_Store에 추가
                List<String> storeInfo = List.of(
                        String.valueOf(store.getStoreCategory()),
                        store.getStoreName()
                );
                List_Store.add(storeInfo);
            }
        }
        System.out.println("---------------------------");
        System.out.println("가게 번호를 입력해주세요.");
        System.out.print(">");
    }

    //TODO 메뉴선택
    private static int getMenuFromUser(int x) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            Print_Menu(x);

            String input = sc.nextLine();

            if (regexManager.checkMenu(input, List_Menu.size())) {
                int Menu_user_selected = Integer.parseInt(input);
                System.out.println(Menu_user_selected + "번을 선택하셨습니다.");
                return Menu_user_selected;
            }
        }
    }

    // 메뉴 출력
    private static void Print_Menu(int x) {
        System.out.println("----------메뉴 선택----------");

        // Store 데이터 StoreRepository에 로드
        StoreRepository storeRepository = csvManager.readStoreCsv();
        List_Menu.clear(); // 기존 메뉴 리스트 초기화

        // 선택한 가게 이름 가져오기
        String selectedStoreName = List_Store.get(x - 1).get(1).trim();

        // 가게 이름과 일치하는 Store 객체 찾기
        Store selectedStore = storeRepository.findAll().stream()
                .filter(store -> store.getStoreName().equals(selectedStoreName))
                .findFirst()
                .orElse(null);
        if (selectedStore != null && !selectedStore.getStoreMenuList().isEmpty()) {
            int numbering = 1;
            // 해당 가게의 메뉴 출력
            for (Food menuItem : selectedStore.getStoreMenuList()) {
                List_Menu.add(menuItem.getFoodName()); // List_Menu에 메뉴 정보 추가

                System.out.print(numbering++ + ". ");
                System.out.println(menuItem.getFoodName());
            }
        } else {
            System.out.println("메뉴 없음!! storeData.csv 오류1!");
        }
        System.out.println("---------------------------");
        System.out.println("원하는 메뉴의 번호를 입력해주세요.");
        System.out.print(">");
    }

    //TODO 수량 확인
    private static int Quantity_check() {
        System.out.println("주문하신 메뉴의 수량을 선택해주세요.(최대 10개)");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            if (regexManager.checkMenu(input, 10)) {
                int Menu_Quantity = Integer.parseInt(input);
                System.out.println(Menu_Quantity + "개를 선택하셨습니다.");
                return Menu_Quantity;
            }
        }
    }

    private static void pushToConfirmed(int Category_user_selected, int Store_user_selected, int Menu_user_selected, int Quantity, int keep_order) {
        List<String> add_ordered_list = new ArrayList<>();
        add_ordered_list.add(Integer.toString(Category_user_selected));         //카테고리 번호
        add_ordered_list.add(List_Store.get(Store_user_selected-1).get(1));     //가게 이름
        add_ordered_list.add(List_Menu.get(Menu_user_selected-1));              //메뉴 이름
        add_ordered_list.add(Integer.toString(Quantity));                       //수량
        add_ordered_list.add(check_cost(List_Store.get(Store_user_selected-1).get(1), List_Menu.get(Menu_user_selected-1)));   //가게명, 메뉴명 인자. 리턴은 가격
        Confirmed_order.add(add_ordered_list);

        if (Category_user_selected==1) System.out.print("한식 카테고리의 ");
        else if(Category_user_selected==2) System.out.print("중식 카테고리의 ");
        else System.out.print("일식 카테고리의 ");
        System.out.println(Confirmed_order.get(keep_order-1).get(1) + " 가게의 " + Confirmed_order.get(keep_order-1).get(2) + "을 " + Confirmed_order.get(keep_order-1).get(3) + "개 선택하셨습니다.");
    }

    //메뉴 비용 확인
    private static String check_cost(String temp_store, String temp_menu) {
        FoodRepository foodRepository = csvManager.readFoodCsv();
        Food food = foodRepository.findAll().stream()
                .filter(f -> f.getStore().getStoreName().equals(temp_store) && f.getFoodName().equals(temp_menu))
                .findFirst()
                .orElse(null);
        if (food != null) {
            return String.valueOf(food.getFoodPrice()); // "개당" 가격 반환. 합계 아님. 메뉴별 개당 가격임.
        } else {
            return "가격을 찾을 수 없습니다.";
        }
    }


    //TODO 추가 주문 여부 확인
    private static int Keep_Order_Check(int keep_order) {
        System.out.println("메뉴를 추가 주문하시겠습니까?");
        System.out.print("[Y/N]");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            if (regexManager.checkYN(input)) {
                if (input.charAt(0) == 'Y') {
                    System.out.println("계속 주문합니다.");
                    return keep_order;
                }
                else {
                    System.out.println("추가 주문을 종료합니다.");
                    return 0;
                }
            }
        }
    }


    //TODO 주문내역 확인 출력
    private static void Print_Bill(List<List<String>> Bill){
            int cost_sum=0;
            System.out.println("<주문서>");
            System.out.printf("%-10s %5s%n", "메뉴", "수량"); // 메뉴와 수량의 제목
            for (List<String> order : Bill) {
                System.out.printf("%-10s %5s%n", order.get(2), order.get(3)); // 각 메뉴와 수량 출력
                cost_sum += Integer.parseInt(order.get(4)) * Integer.parseInt(order.get(3));
            }
            System.out.println("---------------");
            System.out.println("합계 : " + cost_sum + "원\n");
            System.out.println("이대로 주문을 확정하시겠습니까?");
            System.out.print("[Y/N]");
    }
    private static int getConfirmFromUser() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();

            if (regexManager.checkYN(input)) {
                if (input.charAt(0) == 'Y') {
                    System.out.println("주문을 확정합니다.");
                    return 1;
                }
                else {
                    System.out.println("주문을 확정하지 않습니다.");
                    return 0;
                }
            }
        }
    }

    private static String check_max() {
        String filePath = "src/main/java/dataInfo/orderData.csv";
        int maxIndex = Integer.MIN_VALUE;
        String max = "0000";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                int index = Integer.parseInt(columns[1]);
                if (index > maxIndex) {
                    maxIndex = index;
                    max = columns[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int number = Integer.parseInt(max);
        number++;
        max = String.format("%04d", number);
        return max;
    }


}
