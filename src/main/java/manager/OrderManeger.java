package manager;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.time.temporal.ChronoUnit;

import java.time.format.DateTimeParseException;

import java.util.*;

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
    static List<List<String>> Confirmed_order = new ArrayList<>();     //최종 주문 확정 리스트. [[카테고리, 가게, 메뉴, 수량, 개당가격], [~~]]

    static Scanner sc = new Scanner(System.in);

    static String userId;
    static String storeName;
    private static String currentOrderTime;
    static boolean delivery = false;

    private static boolean canOrder = true;
    private static boolean checkMoreOrder = false;



    public static int Print_User_Main_Menu(String time, String id) {
        userId = id;

        int userMainMenu_user_selected = 0;

        while (true) {
            System.out.println("----------고객 메인 메뉴----------");
            System.out.println("1. 카테고리 선택");
            System.out.println("2. 주문내역 확인");
            System.out.println("3. 주문 취소");
            System.out.println("4. 로그아웃");
            System.out.println("--------------------------------");
            System.out.println("고객 메인 메뉴 번호를 입력해주세요.");
            System.out.print(">");

            String input = sc.nextLine().trim();
            if (regexManager.checkMenu(input, 4)) {

                userMainMenu_user_selected = Integer.parseInt(input);
                System.out.println(userMainMenu_user_selected + "번을 선택하셨습니다.");

                // 선택한 메뉴에 따라 동작 수행
                switch (userMainMenu_user_selected) {
                    case 1:
                        getOrderFromUser(time, id);
                        break;
                    case 2:
                        check_order_history_from_User(id);
                        break;
                    case 3:
                        cancle_order(time, id);
                        break;
                    case 4:
                        return 4;
                }
            } else {
                System.out.println("올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }

    private static void cancle_order(String time, String id) {
        System.out.println("------------[유의 사항]------------");
        System.out.println("10분 이내 주문 내역만 취소가 가능합니다.");
        System.out.println("------------[유의 사항]------------");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime targetTime;
        try {
            targetTime = LocalDateTime.parse(time, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("유효하지 않은 시간 형식: " + time);
            return;
        }

        List<String> allOrders = new ArrayList<>();
        List<String> ordersToCancel = new ArrayList<>();



        // 사용자 홈 디렉토리에 있는 파일 경로
        String homeDir = System.getProperty("user.home");
        String orderFilePath = Paths.get(homeDir, "orderData.csv").toString();
        String foodFilePath = Paths.get(homeDir, "foodData.csv").toString();

        try (BufferedReader br = new BufferedReader(new FileReader(orderFilePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue; // 빈 줄 무시
                }

                String[] array = line.split(",");
                String orderTimeStr = array[0];
                //System.out.println("time is "+ array[0] + " 번호는 "+ array[1]);
                if (!array[2].equals(id)) {//id 다르면 넘어가기
                    continue;
                }
                try {
                    LocalDateTime orderTime = LocalDateTime.parse(orderTimeStr, formatter);
                    String MyorderTimeStr = targetTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                    // 10분 이내의 주문인지 확인
                    if (MyorderTimeStr.equals(orderTimeStr) || (orderTime.isAfter(targetTime.minusMinutes(10)) && orderTime.isBefore(targetTime.plusMinutes(10))) ) {
                        ordersToCancel.add(line);
                    }
                    else {
                        allOrders.add(line);
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("유효하지 않은 날짜 형식: " + orderTimeStr);
                    allOrders.add(line); // 날짜 형식이 유효하지 않은 데이터는 그대로 유지
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 출력: 10분 이내의 주문
        System.out.println("10분 이내의 주문:");
        for (int i = 0; i < ordersToCancel.size(); i++) {
            System.out.println((i + 1) + ". " + ordersToCancel.get(i));
        }

        if (ordersToCancel.isEmpty()) {
            System.out.println("삭제 가능한 주문이 없습니다.\n");
            return;
        }

        // 사용자로부터 삭제할 주문 선택
        System.out.print("삭제할 주문의 번호를 선택하세요: ");
        Scanner scanner = new Scanner(System.in);
        int selectedIndex = -1;

        try {
            selectedIndex = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("잘못된 입력입니다.");
            return;
        }

        if (selectedIndex < 1 || selectedIndex > ordersToCancel.size()) {
            System.out.println("잘못된 번호입니다.");
            return;
        }

        // 선택된 주문 제외하고 나머지 유지
        String list_to_delete = ordersToCancel.get(selectedIndex - 1);  //삭제할 주문 내역 받아놓고
        ordersToCancel.remove(selectedIndex - 1);
        allOrders.addAll(ordersToCancel);

        // CsvManager를 이용해 파일 갱신
        csvManager.DeleteOrderCsv(allOrders);
        csvManager.DeleteFoodCsv(list_to_delete);
        System.out.println("선택한 주문이 삭제되었습니다.");

    }

    public static void getOrderFromUser(String time, String id) {
        currentOrderTime = time;

        Confirmed_order.clear();
        int keep_order = 1;
        canOrder = true;
        checkMoreOrder = false;
        while (true) {
            int Category_user_selected = getCategoryFromUser();
            int Store_user_selected = getStoreFromUser(Category_user_selected);
            int Menu_user_selected = getMenuFromUser(Store_user_selected);

            int Quantity = Quantity_check();

            pushToConfirmed(Category_user_selected, Store_user_selected, Menu_user_selected, Quantity, keep_order);

            if(!delivery){  //배달 아닐때만
                keep_order = Keep_Order_Check(keep_order);
                if (keep_order == 0) break;
                keep_order++;
                checkMoreOrder = true;  // 매장일때만 true로 설정
            }
            else{
                break;
            }

        }
        if(delivery){
            calculateDeliveryPay();

        }
        if(!canOrder){
            return;
        }

        Print_Bill(Confirmed_order);

        //orderData.csv랑 foodData.csv 수정
        if (1 == getConfirmFromUser()) {
            //orderData.csv에 집어넣기 양식은 time, 주문번호, id, 가게:메뉴:수량, 가게:메뉴:수량, 가게:메뉴:수량 ..... 이런식으로 쭉
            // 주문번호 생성
            String cur_max = check_max();

            // 메뉴 항목을 "가게:메뉴:수량" 형식으로 구성
            StringBuilder menuItems = new StringBuilder();
            for (int i = 0; i < Confirmed_order.size(); i++) {
                List<String> row = Confirmed_order.get(i);
                String store = row.get(1); // 가게 이름
                String menu = row.get(2); // 메뉴 이름
                String quantity = row.get(3); // 수량
                String menuItem = store + ":" + menu + ":" + quantity;
                menuItems.append(menuItem);
                if (i < Confirmed_order.size() - 1) {
                    menuItems.append(","); // 여러 항목 구분
                }
            }

            String homeDir = System.getProperty("user.home");
            String orderFilePath = Paths.get(homeDir, "orderData.csv").toString();

            String line = String.join(",", time, cur_max, id, menuItems.toString());

            try (FileWriter writer = new FileWriter(orderFilePath, true)) {
                writer.write(line + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //foodData.csv에서 가게 이름, 메뉴 이름 비교 후 해당 메뉴 수량 증가
            String foodFilePath = Paths.get(homeDir, "foodData.csv").toString();
            for (int i = 0; i < Confirmed_order.size(); i++) {
                String targetMenu = Confirmed_order.get(i).get(2);
                String targetStore = Confirmed_order.get(i).get(1);
                int addQuantity = Integer.parseInt(Confirmed_order.get(i).get(3));

                List<String[]> foodData = new ArrayList<>();

                try (BufferedReader br = new BufferedReader(new FileReader(foodFilePath))) {
                    String new_line;
                    while ((new_line = br.readLine()) != null) {
                        String[] data = new_line.split(",");
                        if (data[3].equals(targetMenu) && data[1].equals(targetStore)) {
                            int currentQuantity = Integer.parseInt(data[5]);
                            data[5] = String.valueOf(currentQuantity + addQuantity);
                        }
                        foodData.add(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(foodFilePath))) {
                    for (String[] data : foodData) {
                        bw.write(String.join(",", data));
                        bw.newLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("주문이 완료되었습니다. 엔터 키를 누르면 고객 메뉴로 돌아갑니다.");

            MenuManager.Synchronize_csv_home_to_resource();

        } else {
            System.out.println("주문이 완료되지 않았습니다. 엔터 키를 누르면 고객 메뉴로 돌아갑니다.");
        }

        //종료 대기
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
    }

    //TODO 배달 기능 구현
    // 최근 일정 기간 동안의 주문액이 일정 이상이면, 붙을 배달료가 면제되거나 거부될 배달이 (배달료는 붙여서) 수락되게
    private static boolean isEnoughPay() { // 일정 주문액 (5만원)
        OrderRepository orderRepository = csvManager.readOrderCsv();  // 파일에 있는 order 내용 받아오고
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm"); //날짜형태로 변환
        LocalDateTime now = LocalDateTime.parse(currentOrderTime, formatter); //현재시간 LocalDateTime으로

        int totalRecentOrder = 0;  // 주문 금액량 저장할 변수
        for (Order order : orderRepository.findAll()) { // 모든 주문 정보 순회
            if (order.getUser().getUserId().equals(userId)) { // 현재 사용자가 주문한것만 필터링..
                LocalDateTime orderTime = LocalDateTime.parse(order.getOrderTime(), formatter);

                // 7일 이내인지 확인
                long daysDifference = ChronoUnit.DAYS.between(orderTime, now);  // orderTime과 now를 일 단위로 계산해줌
                if (daysDifference <= 7) {
                    // 주문 금액 합산
                    for (int i = 0; i < order.getFoods().size(); i++) {
                        int foodPrice = order.getFoods().get(i).getFoodPrice();
                        int quantity = order.getQuantitys().get(i);
                        totalRecentOrder += foodPrice * quantity;
                    }
                }
            }
        }

        // 합산 금액이 5만 원 이상인지 확인
        return totalRecentOrder >= 50000;
    }

    //TODO 배달료 계산
    // 가게 위치와 주문자 위치 간의 직선거리에 따라, 일정 이상 멀면 배달료가 붙고, 더 멀면 아예 배달이 거부되게
    private static void calculateDeliveryPay() {
        UserRepository userRepository =  csvManager.readUserCsv();
        User user = userRepository.findUserById(userId);
        Position userPosition = user.getUserLocation();


        StoreRepository storeRepository = csvManager.readStoreCsv();
        Store store = storeRepository.findStoreName(storeName);
        Position storePosition = store.getStoreLocation();

        int deliveryPay = 1000; //배달료 1000원으로
        // 좌표 사이의 거리 계산
        double distance = Math.sqrt(Math.pow(userPosition.getX() - storePosition.getX(), 2) +
                Math.pow(userPosition.getY() - storePosition.getY(), 2));

        System.out.println(distance);

        // 거리 기준에 따라 배달 가능 여부 및 배달료 설정
        if (distance <= 1000.0) {  // 1000 이하이면
            System.out.println("배달이 가능합니다.");
            System.out.println("배달료는 무료입니다.");
//            System.out.println("배달 가능 (배달료 없음). 거리: " + distance);
        } else if (distance > 1000.0 && distance <= 4000) { // 1001~4000
            System.out.println("배달이 가능합니다.");
            if(isEnoughPay()){//최근 주문 금액이 일정 이상일 때
                System.out.println("최근 일정금액 이상 주문하셨습니다.\n배달료는 무료입니다.");
            }
            else{
                //            System.out.println("배달 가능 (배달료 있음). 거리: " + distance);
                System.out.println("배달료: " + deliveryPay);
            }

        } else { // 4000 초과
            if(isEnoughPay()){  //최근 주문 금액이 일정 이상일 때
                System.out.println("최근 일정금액 이상 주문하셨습니다.\n배달이 가능합니다.");
                System.out.println("배달료: " + deliveryPay);
            }
            else{
                System.out.println("거리가 멀어서 배달이 불가능합니다.");
                //            System.out.println("배달 불가능. 거리: " + distance);
                canOrder = false;

            }
        }
    }



    private static boolean isDelivery() {
        int userSelected = 0;
        while (true) {
            System.out.println("주문 방식을 선택해주세요.");
            System.out.println("1. 매장에서 먹기");
            System.out.println("2. 배달하기");
            System.out.println("--------------------------------");
            System.out.println("번호를 입력해주세요.");
            System.out.print("> ");

            String input = sc.nextLine().trim();
            if (regexManager.checkMenu(input, 2)) {
                userSelected = Integer.parseInt(input);
                break;
            }
        }
        if (userSelected == 1) {
            System.out.println("매장에서 먹기를 선택하셨습니다.");
            return false;
        } else {
            System.out.println("배달하기를 선택하셨습니다.");
            return true;
        }
    }

    private static int getCategoryFromUser() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            Print_Category();
            String input = sc.nextLine();
            input = input.trim();

            if (regexManager.checkMenu(input, 3)) {
                int Category_user_selected = Integer.parseInt(input);
                System.out.println(Category_user_selected + "번을 선택하셨습니다.");
                return Category_user_selected;
            }
        }
    }

    //카테고리 출력
    private static void Print_Category() {
        if(!checkMoreOrder){  // 매장 추가주문 아닐때만 떠야함
            delivery = isDelivery();
        }

        System.out.println("----------고객 카테고리 입장----------");
        System.out.println("1. 한식");
        System.out.println("2. 중식");
        System.out.println("3. 일식");
        System.out.println("-----------------------------------");
        System.out.println("카테고리 번호를 입력해주세요.");
        System.out.print(">");
    }

    private static int getStoreFromUser(int x) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            Print_Store(x);
            String input = sc.nextLine();
            input = input.trim();
            if (regexManager.checkMenu(input, List_Store.size())) {
                int Store_user_selected = Integer.parseInt(input);

                //선택된 가게의 이름을 storeName에 저장
                storeName = List_Store.get(Store_user_selected - 1).get(1);
                System.out.println(Store_user_selected + "번을 선택하셨습니다.");
                return Store_user_selected;
            }
        }
    }

    // 가게 출력
    private static void Print_Store(int category) {
        StoreRepository storeRepository = csvManager.readStoreCsv();
        List_Store.clear();

        int numbering = 1;
        System.out.println("---------- 가게 선택 ----------");

        // 해당 카테고리에 속한 가게만 출력
        for (Store store : storeRepository.findAll()) {
            // 가게가 해당 카테고리에 속하는지 확인
            if (store.getStoreCategories().contains(category)) {
                System.out.print(numbering++ + ". ");
                System.out.println(store.getStoreName().trim());

                // 카테고리랑 가게 이름 List_Store에 추가
                List<String> storeInfo = List.of(
                        String.valueOf(category), // 첫 번째 카테고리
                        store.getStoreName()
                );
                List_Store.add(storeInfo);
            }
        }

        System.out.println("----------------------------");
        System.out.println("가게 번호를 입력해주세요.");
        System.out.print(">");
    }


    private static int getMenuFromUser(int x) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            Print_Menu(x);

            String input = sc.nextLine();
            input = input.trim();
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

    private static int Quantity_check() {
        System.out.println("주문하신 메뉴의 수량을 선택해주세요.(최대 10개)");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            input = input.trim();
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
        add_ordered_list.add(List_Store.get(Store_user_selected - 1).get(1));     //가게 이름
        add_ordered_list.add(List_Menu.get(Menu_user_selected - 1));              //메뉴 이름
        add_ordered_list.add(Integer.toString(Quantity));                       //수량
        add_ordered_list.add(check_cost(List_Store.get(Store_user_selected - 1).get(1), List_Menu.get(Menu_user_selected - 1)));   //가게명, 메뉴명 인자. 리턴은 가격
        Confirmed_order.add(add_ordered_list);

        if (Category_user_selected == 1) System.out.print("한식 카테고리의 ");
        else if (Category_user_selected == 2) System.out.print("중식 카테고리의 ");
        else System.out.print("일식 카테고리의 ");
        System.out.println(Confirmed_order.get(keep_order - 1).get(1) + " 가게의 " + Confirmed_order.get(keep_order - 1).get(2) + "을 " + Confirmed_order.get(keep_order - 1).get(3) + "개 선택하셨습니다.");
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

    private static int Keep_Order_Check(int keep_order) {
        System.out.println("메뉴를 추가 주문하시겠습니까?");
        System.out.print("[Y/N]");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            input = input.trim();
            if (regexManager.checkYN(input)) {
                if (input.charAt(0) == 'Y') {
                    System.out.println("계속 주문합니다.");
                    return keep_order;
                } else {
                    System.out.println("추가 주문을 종료합니다.");
                    return 0;
                }
            }
        }
    }

    private static void Print_Bill(List<List<String>> Bill) {
        int cost_sum = 0;
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
            input = input.trim();
            if (regexManager.checkYN(input)) {
                if (input.charAt(0) == 'Y') {
                    System.out.println("주문을 확정합니다.");
                    return 1;
                } else {
                    System.out.println("주문을 확정하지 않습니다.");
                    return 0;
                }
            }
        }
    }

    private static String check_max() {
        // 사용자 홈 디렉토리에 있는 orderData.csv 파일 경로
        String homeDir = System.getProperty("user.home");
        String filePath = Paths.get(homeDir, "orderData.csv").toString();
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

    public static void check_order_history_from_User(String inputId) {
        // 사용자 홈 디렉토리에 있는 파일 경로
        String homeDir = System.getProperty("user.home");
        String orderFilePath = Paths.get(homeDir, "orderData.csv").toString();
        String foodFilePath = Paths.get(homeDir, "foodData.csv").toString();

        // 음식 데이터 읽기 (가게, 메뉴명과 가격을 매핑하는 해시맵 생성)
        Map<String, Integer> foodPrices = new HashMap<>();
        try (BufferedReader foodReader = new BufferedReader(new FileReader(foodFilePath))) {
            String line;
            while ((line = foodReader.readLine()) != null) {
                String[] columns = line.split(",");
                String storeName = columns[1]; // 가게 이름
                String foodName = columns[3]; // 메뉴 이름
                int price = Integer.parseInt(columns[4]); // 가격

                // 가게와 메뉴 이름을 결합하여 "가게:메뉴" 형식으로 키를 만들어 가격을 저장
                String key = storeName + ":" + foodName;
                foodPrices.put(key, price);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 주문 데이터 읽기 및 해당 아이디의 주문 내역 찾기
        boolean orderFound = false;
        try (BufferedReader orderReader = new BufferedReader(new FileReader(orderFilePath))) {
            String line;
            while ((line = orderReader.readLine()) != null) {
                String[] columns = line.split(",");
                String userId = columns[2];

                // 아이디가 일치하는 경우 주문 내역 출력
                if (userId.equals(inputId)) {
                    orderFound = true;
                    int totalPrice = 0;

                    System.out.println("아이디: " + userId);
                    System.out.println("주문 내역:");

                    // 주문 항목 처리
                    for (int i = 3; i < columns.length; i++) {
                        String[] item = columns[i].split(":");
                        String storeName = item[0]; // 가게 이름
                        String foodName = item[1]; // 메뉴 이름
                        int quantity = Integer.parseInt(item[2]); // 수량

                        // 가게와 메뉴를 결합하여 "가게:메뉴" 키로 가격을 조회
                        String key = storeName + ":" + foodName;
                        int price = foodPrices.getOrDefault(key, 0); // 가격을 찾지 못할 경우 0으로 설정
                        totalPrice += price * quantity;

                        System.out.println("  " + storeName + " - " + foodName + " " + quantity + "개 - " + (price * quantity) + "원");
                    }
                    System.out.println("합계: " + totalPrice + "원");
                    System.out.println();

//                    UserRepository userRepository = csvManager.userRepository;
//                    User user = userRepository.findUserById(userId);

                }
            }

            if (!orderFound) {
                System.out.println("해당 아이디의 주문 내역이 없습니다.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static void check_order_history_from_Admin() {
        // 사용자 홈 디렉토리에 있는 파일 경로
        String homeDir = System.getProperty("user.home");
        String orderFilePath = Paths.get(homeDir, "orderData.csv").toString();
        String foodFilePath = Paths.get(homeDir, "foodData.csv").toString();

        Map<String, Integer> foodPrices = new HashMap<>();

        // 음식 데이터 읽기 (메뉴명과 가격을 매핑하는 해시맵 생성)
        try (BufferedReader foodReader = new BufferedReader(new FileReader(foodFilePath))) {
            String line;
            while ((line = foodReader.readLine()) != null) {
                String[] columns = line.split(",");
                String foodName = columns[3];   // 음식 이름
                int price = Integer.parseInt(columns[4]);  // 음식 가격
                foodPrices.put(foodName, price);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 주문 데이터 읽기
        List<String> ids = new ArrayList<>();
        List<List<String[]>> items = new ArrayList<>();
        try (BufferedReader orderReader = new BufferedReader(new FileReader(orderFilePath))) {
            String line;
            while ((line = orderReader.readLine()) != null) {
                String[] columns = line.split(",");
                ids.add(columns[2]); // ID 저장

                // 메뉴와 수량을 이중 배열로 저장
                List<String[]> itemList = new ArrayList<>();
                for (int i = 3; i < columns.length; i++) {
                    itemList.add(columns[i].split(":"));
                }
                items.add(itemList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 결과 출력
        for (int i = 0; i < ids.size(); i++) {
            String userId = ids.get(i);
            List<String[]> userItems = items.get(i);
            int totalPrice = 0;

            System.out.println((i + 1) + ". " + userId);
            System.out.println("  ➤ 주문내역:");

            for (String[] item : userItems) {
                String itemName = item[1];
                int quantity = Integer.parseInt(item[2]);
                int price = foodPrices.getOrDefault(itemName, 0); // 가격을 찾지 못할 경우 0으로 설정
                totalPrice += price * quantity;

                System.out.println("    " + itemName + " " + quantity + "개 - " + (price * quantity) + "원");
            }
            System.out.println("  합계: " + totalPrice + "원");
            System.out.println();
        }
    }



    public static int Print_Admin_Main_Menu(String time) {
        int adminMainMenu_admin_selected = 0;
        while (true) {
            System.out.println("----------관리자 메인 메뉴----------");
            System.out.println("1. 주문내역 확인");
            System.out.println("2. 가게/메뉴 정보 변경");
            System.out.println("3. 로그아웃");
            System.out.println("--------------------------------");
            System.out.println("관리자 메인 메뉴 번호를 입력해주세요.");
            System.out.print(">");
            String input = sc.nextLine();
            input = input.trim();
            if (regexManager.checkMenu(input, 3)) {
                adminMainMenu_admin_selected = Integer.parseInt(input);
                System.out.println(adminMainMenu_admin_selected + "번을 선택하셨습니다.");
                break;
            }
        }
        if (adminMainMenu_admin_selected == 1) {
            Print_Admin_Order_Check_Menu();
            //check_order_history_from_Admin();
            return 1;
        } else if (adminMainMenu_admin_selected == 2) {
            User user = new User();
            user.admin_SetInformation();
            return 2;
        } else {
            System.out.println("로그아웃합니다");
            return 3;
        }


    }

    private static void Print_Admin_Order_Check_Menu() {
        int adminOrderCheckMenu_admin_selected = 0;
        while (true) {
            System.out.println("----------관리자 메인 메뉴----------");
            System.out.println("1. 전체 주문내역 확인");
            System.out.println("2. 카테고리별 주문내역 확인");
            System.out.println("--------------------------------");
            System.out.println("메뉴 번호를 입력해주세요.");
            System.out.print(">");
            String input = sc.nextLine();
            input = input.trim();
            if (regexManager.checkMenu(input, 2)) {
                adminOrderCheckMenu_admin_selected = Integer.parseInt(input);
                System.out.println(adminOrderCheckMenu_admin_selected + "번을 선택하셨습니다.");
                break;
            }
        }
        if (adminOrderCheckMenu_admin_selected == 1) {
            check_order_history_from_Admin();
        } else {
            check_category_history_from_Admin();
        }
    }


    private static void check_category_history_from_Admin() {

        StoreRepository storeRepository = StoreRepository.getInstance();
        int[] categories = {1, 2, 3}; // 한식, 중식, 일식

        for (int category : categories) {
            System.out.println("------------ " + getCategoryName(category) + " ------------");

            List<Store> storesByCategory = storeRepository.findStoreCategory(category);

            if (storesByCategory.isEmpty()) {
                System.out.println("해당 카테고리 주문 내역이 없습니다.");
            } else {
                int cnt = 1;
                for (Store store : storesByCategory) {
                    // 각 가게가 여러 카테고리에 속할 수 있으므로, 해당 가게가 속한 모든 카테고리를 출력
                    List<Integer> storeCategories = store.getStoreCategories();
                    if (storeCategories.contains(category)) {
                        System.out.println((cnt++) + ". " + store.getStoreName());
                        FoodRepository foodRepository = FoodRepository.getInstance();
                        List<Food> foodsInStore = foodRepository.findFoodsStoreName(store.getStoreName());

                        int price = 0;
                        for (Food food : foodsInStore) {
                            System.out.println("   " + food.getFoodName() + " " + food.getFoodQuantity());
                            price += food.getFoodPrice() * food.getFoodQuantity();
                        }
                        System.out.println("   " + price + "원\n-------------");
                    }
                }
            }
        }
    }

    private static String getCategoryName(int category) {
        switch (category) {
            case 1:
                return "한식";
            case 2:
                return "중식";
            case 3:
                return "일식";
            default:
                return "알 수 없는 카테고리";
        }
    }


}
