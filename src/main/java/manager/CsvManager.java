package manager;

import Entity.*;
import Repository.FoodRepository;
import Repository.OrderRepository;
import Repository.StoreRepository;
import Repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;


public class CsvManager {

    final String userCsvFileName = "/userData.csv";
    final String foodCsvFileName = "/foodData.csv";
    final String orderCsvFileName = "/orderData.csv";
    final String storeCsvFileName = "/storeData.csv";

    UserRepository userRepository = UserRepository.getInstance();
    FoodRepository foodRepository = FoodRepository.getInstance();
    OrderRepository orderRepository =OrderRepository.getInstance();
    StoreRepository storeRepository = StoreRepository.getInstance();



    public void writeUserCsv(UserRepository userRepository) {
        String homeDir = System.getProperty("user.home");
        Path path = Paths.get(homeDir, "userData.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (User user : userRepository.findAll()) {
                int x = user.getUserLocation().getX();
                int y = user.getUserLocation().getY();
                String line = user.getUserId() + ","
                        + user.getUserPassword() + ","
                        + user.getUserName() + ","
                        + x + ","
                        + y; // 위치 정보까지 CSV에 기록

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public UserRepository readUserCsv() {
        UserRepository userRepository = UserRepository.getInstance();
        String homeDir = System.getProperty("user.home");
        Path path = Paths.get(homeDir, "userData.csv");

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] array = line.split(",");

                String userId = array[0].trim();
                String userPassword = array[1].trim();
                String userName = array[2].trim();
                int x = Integer.parseInt(array[3].trim());
                int y = Integer.parseInt(array[4].trim());

                User user = new User(userId, userPassword, userName, new Position(x, y));
                userRepository.addUser(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userRepository;
    }

    public UserRepository home_readUserCsv() {
        String homeDir = System.getProperty("user.home");
        Path path = Paths.get(homeDir, "userData.csv");
        try (BufferedReader br = Files.newBufferedReader(path))
        {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;  // 빈 줄 무시
                }
                String[] array = line.split(",");

                User user = new User(array[0], array[1], array[2],
                        new Position(Integer.parseInt(array[3]), Integer.parseInt(array[4])));

                userRepository.addUser(user);  // 사용자 추가
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("회원 정보 파일이 없습니다.\n프로그램을 종료합니다.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("파일 읽기 중 오류가 발생했습니다.\n프로그램을 종료합니다.");
            System.exit(0);
        }
        return userRepository;
    }


    public StoreRepository readStoreCsv() {

        StoreRepository storeRepository =StoreRepository.getInstance();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(storeCsvFileName)))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;  // 빈 줄 무시
                }
                String[] array = line.split(",");

                // 카테고리 처리 (하나 이상의 카테고리일 수 있음)
                String[] categories = array[0].split(":");
                List<Integer> storeCategories = new ArrayList<>();
                for (String category : categories) {
                    storeCategories.add(Integer.parseInt(category));  // 카테고리 추가
                }

                // Store 객체 생성 (카테고리 리스트와 가게 이름, 위치)

                Store store = new Store(storeCategories, array[1],
                        new Position(Integer.parseInt(array[2]), Integer.parseInt(array[3])));

                // 메뉴 리스트 파싱 (콜론(:)으로 구분된 메뉴 이름)
                String[] menuItems = array[4].split(":");
                List<Food> menuList = new ArrayList<>();
                for (String itemName : menuItems) {
                    Food food = new Food(itemName);  // 음식 이름으로 Food 객체 검색
                    if (food != null) {
                        menuList.add(food);  // 메뉴 리스트에 Food 객체 추가
                    } else {
                        //System.out.println("음식 이름 '" + itemName + "'을 찾을 수 없습니다.");
                    }
                }
                store.setStoreMenuList(menuList);  // Store 객체에 메뉴 리스트 추가

                // StoreRepository에 Store 객체 추가
                storeRepository.addStore(store);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("회원 정보 파일이 없습니다.\n프로그램을 종료합니다.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("파일 읽기 중 오류가 발생했습니다.\n프로그램을 종료합니다.");
            System.exit(0);
        }
        return storeRepository;
    }

    public void writeStoreCsv(StoreRepository storeRepository) {

        // 사용자 홈 디렉토리에 저장
        String homeDir = System.getProperty("user.home");
        Path path = Paths.get(homeDir, "storeData.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Store store : storeRepository.findAll()) {
                // 각 Store의 정보를 CSV 형식으로 변환
                StringBuilder line = new StringBuilder();

                // 카테고리 리스트를 콜론(:)으로 구분하여 추가
                List<Integer> categories = store.getStoreCategories();  // 여러 카테고리를 가져옴
                for (int i = 0; i < categories.size(); i++) {
                    line.append(categories.get(i));
                    if (i < categories.size() - 1) {
                        line.append(":");
                    }
                }

                line.append(",")
                        .append(store.getStoreName()).append(",")
                        .append(store.getStoreLocation().getX()).append(",")
                        .append(store.getStoreLocation().getY()).append(",");

                // 메뉴 리스트를 ":"로 구분하여 추가
                List<Food> menuList = store.getStoreMenuList();
                for (int i = 0; i < menuList.size(); i++) {
                    line.append(menuList.get(i).getFoodName());
                    if (i < menuList.size() - 1) {
                        line.append(":");
                    }
                }
                // 파일에 한 줄씩 추가
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("파일 쓰기 중 오류가 발생했습니다.");
        }
    }


    public FoodRepository readFoodCsv() {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(foodCsvFileName)))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;  // 빈 줄 무시
                }
                String[] array = line.split(",");

                // 가게의 여러 카테고리를 ":"로 구분하여 파싱
                String[] categories = array[0].split(":");
                List<Integer> storeCategories = new ArrayList<>();
                for (String category : categories) {
                    storeCategories.add(Integer.parseInt(category)); // 카테고리 값 저장
                }

                // Store 객체 생성 (여러 카테고리 지원)
                Store store = new Store(storeCategories, array[1]); // 카테고리 목록 전달

                Food food= new Food(store,Integer.parseInt(array[2]),array[3],
                        Integer.parseInt(array[4]),Integer.parseInt(array[5]));

                // 각 가게에 해당하는 음식 목록에 음식을 추가
                foodRepository.addFood(store.getStoreName(),food);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("회원 정보 파일이 없습니다.\n프로그램을 종료합니다.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("파일 읽기 중 오류가 발생했습니다.\n프로그램을 종료합니다.");
            System.exit(0);
        }
        return foodRepository;
    }

    public void writeFoodCsv(FoodRepository foodRepository) {
        // 사용자 홈 디렉토리에 저장
        String homeDir = System.getProperty("user.home");
        Path path = Paths.get(homeDir, "foodData.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(path)){
            for (Food food : foodRepository.findAll()) {
                // 각 Food 객체의 정보를 CSV 형식으로 변환
                StringBuilder line = new StringBuilder();

                // 가게 카테고리가 여러 개일 수 있기 때문에, 이를 ":"로 구분하여 저장
                List<Integer> storeCategories = food.getStore().getStoreCategories();
                String categories = String.join(":", storeCategories.stream()
                        .map(String::valueOf)
                        .toArray(String[]::new));

                line.append(categories).append(",")  // 가게 카테고리 (여러 개일 수 있음)
                        .append(food.getStore().getStoreName()).append(",")     // 가게 이름
                        .append(food.getFoodId()).append(",")                  // 음식 ID
                        .append(food.getFoodName()).append(",")                // 음식 이름
                        .append(food.getFoodPrice()).append(",")               // 음식 가격
                        .append(food.getFoodQuantity());                       // 음식 총 주문수

                // 파일에 한 줄씩 추가
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("파일 쓰기 중 오류가 발생했습니다.");
        }
    }




    public OrderRepository readOrderCsv() {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(orderCsvFileName)))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;  // 빈 줄 무시
                }
                String[] array = line.split(",");


                String orderTime = array[0];
                String orderId = array[1];
                User user = userRepository.findUserById(array[2]);

                // 음식과 수량 리스트 초기화
                List<Store> stores = new ArrayList<>();
                List<Food> foods = new ArrayList<>();
                List<Integer> quantities = new ArrayList<>();

                // 음식과 수량 정보를 ":"로 분리하여 처리
                for (int i = 3; i < array.length; i++) {  // 3번 인덱스부터 끝까지 반복
                    String[] foodQuantityPair = array[i].split(":");
                    if (foodQuantityPair.length == 3) {
                        Store store =storeRepository.findStoreName(foodQuantityPair[0]);
                        Food food = foodRepository.findFoodByFoodName(store,foodQuantityPair[1]);
                        int quantity = Integer.parseInt(foodQuantityPair[2]);
                        if (food != null) {
                            stores.add(store);
                            foods.add(food);
                            quantities.add(quantity);
                        } else {
                            System.out.println("음식 '" + foodQuantityPair[1] + "'을 찾을 수 없습니다.");
                        }
                    }
                }

                // Order 객체 생성 및 저장
                Order order = new Order(orderTime, orderId, user, stores,foods, quantities);
                orderRepository.addOrder(order);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("회원 정보 파일이 없습니다.\n프로그램을 종료합니다.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("파일 읽기 중 오류가 발생했습니다.\n프로그램을 종료합니다.");
            System.exit(0);
        }
        return orderRepository;
    }
    public void writeOrderCsv(OrderRepository orderRepository) {
        // 사용자 홈 디렉토리에 저장
        String homeDir = System.getProperty("user.home");
        Path path = Paths.get(homeDir, "orderData.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Order order : orderRepository.findAll()) {

                // 각 Order 객체의 정보를 CSV 형식으로 변환
                StringBuilder line = new StringBuilder();
                line.append(order.getOrderTime()).append(",")          // 주문시간
                        .append(order.getOrderId()).append(",")        //주문내역id
                        .append(order.getUser().getUserId()).append(",");      // 사용자 id

                // 음식과 수량 리스트를 ":"로 구분하여 추가
                List<Food> foods = order.getFoods();
                List<Integer> quantities = order.getQuantitys();
                List<Store> stores = order.getStores();

                for (int i = 0; i < foods.size(); i++) {
                    line.append(stores.get(i).getStoreName()).append(":")
                            .append(foods.get(i).getFoodName())
                            .append(":").append(quantities.get(i));
                    if (i < foods.size() - 1) {
                        line.append(",");  // 각 음식-수량 쌍 사이에 콤마 추가
                    }
                }

                // 파일에 한 줄씩 추가
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("파일 쓰기 중 오류가 발생했습니다.");
        }
    }


    //싱크맞추는부분
    //가장 최근 주문 기록보다 이른 시간을 입력하면 일시/시간을 다시 입력 받게 함.
    //가장 최근 주문 기록과 같거나 그 이후의 시간만 허용
    public boolean timeSynchronize(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime currentMostRecent = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(orderCsvFileName)))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue; // 빈 줄 무시
                }

                String[] array = line.split(",");
                String orderTimeStr = array[0];

                try {
                    LocalDateTime orderTime = LocalDateTime.parse(orderTimeStr, formatter);

                    // 가장 최근 일시 업데이트
                    if (currentMostRecent == null || orderTime.isAfter(currentMostRecent)) {
                        currentMostRecent = orderTime;
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("유효하지 않은 날짜 형식: " + orderTimeStr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String most_cur_time = null;

        // orderData.csv 중 가장 최근 시간을 most_cur_time에 저장
        if (currentMostRecent != null) {
            most_cur_time = currentMostRecent.format(formatter);
        } else {
            System.out.println("유효한 데이터가 없습니다.");
        }

        //System.out.println("Most_cur_time is " + most_cur_time);

        // 사용자 입력 시간을 LocalDateTime으로 변환
        LocalDateTime userTime;
        try {
            userTime = LocalDateTime.parse(time, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("유효하지 않은 사용자 입력 시간 형식: " + time);
            return false;
        }

        // 비교 로직
        if (currentMostRecent != null && userTime.isBefore(currentMostRecent)) {
            System.out.println("[경고!!!]\n사용자의 입력 시간이 과거입니다.\n가장 최근 주문 기록은 " + RegexManager.formatDateTime(currentMostRecent.format(formatter)) + "입니다.");
            System.out.println("다시 입력해주세요.");
            return false;
        } else {
            return true;
        }
    }

    public void DeleteOrderCsv(List<String> updatedOrders) {
        String homeDir = System.getProperty("user.home");
        Path path = Paths.get(homeDir, "orderData.csv");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toString()))) {
            for (String order : updatedOrders) {
                bw.write(order);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MenuManager.Synchronize_csv_home_to_resource();
    }

    public void DeleteFoodCsv(String orderDetails) {
        // 사용자 홈 디렉토리에 있는 파일 경로
        String homeDir = System.getProperty("user.home");
        String foodFilePath = Paths.get(homeDir, "foodData.csv").toString();

        // 주문 내역 파싱 (주문 내역 예시: 202410311820,0008,kang,가람성:짬뽕:1,가람성:짬뽕:1,가람성:볶음밥:1)
        String[] orderArray = orderDetails.split(",");
        int count = orderArray.length - 3;  //반복해야하는 횟수
        //System.out.println("###"+count);

        for (int i=0; i<count; i++) {
            // foodData.csv 파일을 읽어들일 맵
            List<String> updatedFoodData = new ArrayList<>();
            boolean foundMatch = false;

            try (BufferedReader br = new BufferedReader(new FileReader(foodFilePath))) {
                String line;

                String[] orderedItems = orderArray[3 + i].split(":");         // orderdItems에는 [가람성, 짬뽕, 수량]

                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) {
                        continue; // 빈 줄 무시
                    }

                    String[] foodData = line.split(",");
                    String storeName = foodData[1];  // foodData.csv의 가게 이름
                    String menuName = foodData[3];   // foodData.csv의 메뉴 이름
                    int currentQuantity = Integer.parseInt(foodData[5]);  // 현재 수량
                    // 주문 내역을 하나씩 확인하며 일치하는 항목을 찾아 수량을 차감

                    int orderedQuantity = Integer.parseInt(orderedItems[2]);

                    // 가게와 메뉴 이름이 일치하는지 확인
                    if (storeName.equals(orderedItems[0]) && menuName.equals(orderedItems[1])) {
                        // 일치하는 항목이 있으면 수량을 차감
                        currentQuantity -= orderedQuantity;

                        // 수량이 0보다 작아지지 않도록 처리
                        if (currentQuantity < 0) {
                            currentQuantity = 0;
                        }

                        // 일치한 항목에 대해 수정된 수량을 기록
                        foodData[5] = String.valueOf(currentQuantity);
                        foundMatch = true;
                    }
                    // 수정된 행을 updatedFoodData에 추가
                    updatedFoodData.add(String.join(",", foodData));
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            // 수정된 내용으로 foodData.csv를 갱신
            if (foundMatch) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(foodFilePath))) {
                    for (String updatedLine : updatedFoodData) {
                        bw.write(updatedLine);
                        bw.newLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("주문 내역에 해당하는 가게나 메뉴가 없습니다.");
            }
        }
        MenuManager.Synchronize_csv_home_to_resource();
    }

}