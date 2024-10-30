package manager;

import Repository.FoodRepository;
import Repository.OrderRepository;
import Repository.StoreRepository;
import Repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import Entity.*;

import static java.nio.file.Files.newBufferedReader;

public class CsvManager {

    final String userCsvFileName = "src/main/java/dataInfo/userData.csv";
    final String foodCsvFileName = "src/main/java/dataInfo/foodData.csv";
    final String orderCsvFileName = "src/main/java/dataInfo/orderData.csv";
    final String storeCsvFileName = "src/main/java/dataInfo/storeData.csv";

    UserRepository userRepository = UserRepository.getInstance();
    FoodRepository foodRepository = FoodRepository.getInstance();
    OrderRepository orderRepository =OrderRepository.getInstance();
    StoreRepository storeRepository = StoreRepository.getInstance();


    public void writeUserCsv(UserRepository userRepository) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userCsvFileName))) {
            for (User u : userRepository.findAll()) {
                // 사용자 정보를 CSV 형식으로 작성
                writer.write(u.getUserId() + ","
                        + u.getUserPassword() + ","
                        + u.getUserName() + ","
                        + u.getUserLocation().getX() + "," // 위치 X
                        + u.getUserLocation().getY() + "\n"); // 위치 Y
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserRepository readUserCsv() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(userCsvFileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;  // 빈 줄 무시
                }
                String[] array = line.split(",");

                User user = new User(array[0], array[1], array[2],
                        new Position(Integer.parseInt(array[3]), Integer.parseInt(array[4])));

                userRepository.addUser(user);  // 사용자 추가

                System.out.println("Added User: " + user.getUserId()); // 디버깅용
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
        try (BufferedReader br = Files.newBufferedReader(Paths.get(storeCsvFileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;  // 빈 줄 무시
                }
                String[] array = line.split(",");

                Store store = new Store(Integer.parseInt(array[0]), array[1],
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
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(storeCsvFileName))) {
            for (Store store : storeRepository.findAll()) {
                // 각 Store의 정보를 CSV 형식으로 변환
                StringBuilder line = new StringBuilder();
                line.append(store.getStoreCategory()).append(",")
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

    public void updateStoreCsv(Store store) {
        Path path = Paths.get(storeCsvFileName);
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            boolean isUpdated = false;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;  // 빈 줄 무시
                }

                String[] data = line.split(",");
                String storeName = data[1];

                if (storeName.equals(store.getStoreName())) {
                    // 가게 이름 일치하면 바뀐정보 업데이트
                    StringBuilder newLine = new StringBuilder();
                    newLine.append(store.getStoreCategory()).append(",")
                            .append(store.getStoreName()).append(",")
                            .append(store.getStoreLocation().getX()).append(",")
                            .append(store.getStoreLocation().getY()).append(",");

                    // 메뉴 리스트를 ":"로 구분하여 추가
                    List<Food> menuList = store.getStoreMenuList();
                    for (int i = 0; i < menuList.size(); i++) {
                        newLine.append(menuList.get(i).getFoodName());
                        if (i < menuList.size() - 1) {
                            newLine.append(":");
                        }
                    }
                    updatedLines.add(newLine.toString());  // 수정된 데이터 추가
                    isUpdated = true;
                } else {
                    updatedLines.add(line);  // 일치하지 않으면 기존 데이터 유지
                }
            }

            if (!isUpdated) {
                System.out.println("해당 가게를 찾을 수 없습니다.");
                return;
            }

        } catch (IOException e) {
            System.out.println("파일을 읽는 중 오류가 발생했습니다.");
            return;
        }

    }


    public FoodRepository readFoodCsv() {

        try (BufferedReader br = Files.newBufferedReader(Paths.get(foodCsvFileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;  // 빈 줄 무시
                }
                String[] array = line.split(",");

                Store store =new Store(Integer.parseInt(array[0]), array[1]);
                Food food= new Food(store,Integer.parseInt(array[2]),array[3],
                        Integer.parseInt(array[4]),Integer.parseInt(array[5]));

                foodRepository.addFood(food);

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
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(foodCsvFileName))) {
            for (Food food : foodRepository.findAll()) {
                // 각 Food 객체의 정보를 CSV 형식으로 변환
                StringBuilder line = new StringBuilder();
                line.append(food.getStore().getStoreCategory()).append(",")  // 가게 카테고리
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

    public void updateFoodCsv(Food food) {
        Path path = Paths.get(foodCsvFileName);
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            boolean isUpdated = false;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;  // 빈 줄 무시
                }

                String[] data = line.split(",");
                String foodName = data[3];

                if (foodName.equals(food.getFoodName())) {
                    // 음식이름이 일치하면 수정한 정보 업데이트
                    StringBuilder newLine = new StringBuilder();
                    newLine.append(food.getStore().getStoreCategory()).append(",")
                            .append(food.getStore().getStoreName()).append(",")
                            .append(food.getFoodId()).append(",")
                            .append(food.getFoodName()).append(",")
                            .append(food.getFoodPrice()).append(",")
                            .append(food.getFoodQuantity());

                    updatedLines.add(newLine.toString());  // 수정된 데이터 추가
                    isUpdated = true;
                } else {
                    updatedLines.add(line);  // 일치하지 않으면 기존 데이터 유지
                }
            }

            if (!isUpdated) {
                System.out.println("해당 음식을 찾을 수 없습니다.");
                return;
            }

        } catch (IOException e) {
            System.out.println("파일을 읽는 중 오류가 발생했습니다.");
            return;
        }

        // 업데이트된 내용을 다시 CSV 파일에 씀
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("파일을 쓰는 중 오류가 발생했습니다.");
        }
    }



    public OrderRepository readOrderCsv() {

        try (BufferedReader br = Files.newBufferedReader(Paths.get(orderCsvFileName))) {
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
                List<Food> foods = new ArrayList<>();
                List<Integer> quantities = new ArrayList<>();

                // 음식과 수량 정보를 ":"로 분리하여 처리
                for (int i = 3; i < array.length; i++) {  // 3번 인덱스부터 끝까지 반복
                    String[] foodQuantityPair = array[i].split(":");
                    if (foodQuantityPair.length == 2) {
                        Food food = foodRepository.findFoodByName(foodQuantityPair[0]);
                        int quantity = Integer.parseInt(foodQuantityPair[1]);

                        if (food != null) {
                            foods.add(food);
                            quantities.add(quantity);
                        } else {
                            System.out.println("음식 '" + foodQuantityPair[0] + "'을 찾을 수 없습니다.");
                        }
                    }
                }

                // Order 객체 생성 및 저장
                Order order = new Order(orderTime, orderId, user, foods, quantities);
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
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(orderCsvFileName))) {
            for (Order order : orderRepository.findAll()) {

                // 각 Order 객체의 정보를 CSV 형식으로 변환
                StringBuilder line = new StringBuilder();
                line.append(order.getOrderTime()).append(",")          // 주문시간
                        .append(order.getOrderId()).append(",")        //주문내역id
                        .append(order.getUser().getUserId()).append(",");      // 사용자 id

                // 음식과 수량 리스트를 ":"로 구분하여 추가
                List<Food> foods = order.getFoods();
                List<Integer> quantities = order.getQuantitys();

                for (int i = 0; i < foods.size(); i++) {
                    line.append(foods.get(i).getFoodName()).append(":").append(quantities.get(i));
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
//    public void timeSynchronize(String time) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
//        LocalDateTime givenTime = LocalDateTime.parse(time, formatter);
//
//        List<Order> orders = readSeatCsv();
//        Set<Integer> resetSeats = new HashSet<>();
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(seatCsvFileName))) {
//            for (Order order : orders) {
//                LocalDateTime startTime = null;
//                LocalDateTime endTime = null;
//
//                try {
//                    startTime = LocalDateTime.parse(seat.getorderTime(), formatter);
//                } catch (DateTimeParseException e) {
//                    // Handle invalid date format
//                }
//
//                try {
//                    endTime = LocalDateTime.parse(seat.getEndTime(), formatter);
//                } catch (DateTimeParseException e) {
//                    // Handle invalid date format
//                }
//
//                if (startTime == null || endTime == null || givenTime.isBefore(startTime) || givenTime.isAfter(endTime)) {
//                    writer.write(seat.getSeatNum() + ",0,000000000000,000000000000");
//                    resetSeats.add(seat.getSeatNum());
//                } else {
//                    writer.write(seat.getSeatNum() + "," + (seat.getUsing() ? "1" : "0") + "," + seat.getStartTime() + "," + seat.getEndTime());
//                }
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        userCsvTimeSynchronize(resetSeats);
    //  }

}