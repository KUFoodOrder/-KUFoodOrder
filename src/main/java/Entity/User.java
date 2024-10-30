package Entity;

import Repository.FoodRepository;
import Repository.StoreRepository;
import Repository.UserRepository;
import manager.CsvManager;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

//사용자 객체
public class User {
    private String userId; //사용자 아이디
    private String userPassword; // 사용자 비밀번호
    private String userName; // 사용자 이름
    private Position userLocation; //사용자 위치
    private List<Order> userOderList; //사용자 주문내역 리스트
    HashMap<Food, Integer> userOrderMap; //사용자 주문내역 리스트(메뉴,주문한양)

    UserRepository userRepository = UserRepository.getInstance();
    FoodRepository foodRepository = FoodRepository.getInstance();
    StoreRepository storeRepository = StoreRepository.getInstance();

    static String admin_id = "admin";

    CsvManager csvManager = new CsvManager();

    public User(String userId, String userPassword, String userName, Position userLocation) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userLocation = userLocation;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public Position getUserLocation() {
        return userLocation;
    }

    public User() {
        userRepository = csvManager.readUserCsv();
        csvManager.writeUserCsv(userRepository);
    }

//    public User(String time) {
//        userRepository = csvManager.readUserCsv();
//        csvManager.writeUserCsv(userRepository,time);
//    }

    public void register() {
        //TODO 회원가입 구현
        String name, id, Password;
        Scanner sc = new Scanner(System.in);
        System.out.println("----------------- 회원 가입 -----------------");
        System.out.println("메인 메뉴로 돌아가려면 'q'를 누르세요.\n");
        while (true) {
            System.out.println("회원님의 이름을 입력해주세요.");
            System.out.print("> ");
            name = sc.nextLine();
            name = name.trim();
            if (name.contentEquals("q")) {
                return;
            }
            if (name.isEmpty() || name.length() > 15) {
                System.out.println("이름의 길이는 1이상 15이하여야 합니다!");
                continue;
            }
            if (!name.matches("^[가-힣]+$")) {
                if (name.matches(".*[a-zA-Z]+.*")) {
                    System.out.println("영어를 포함한 이름은 입력할 수 없습니다.");
                } else if (name.contains(" ") || name.contains("\t")) {
                    System.out.println("공백은 입력할 수 없습니다.");
                } else if (name.matches(".*\\d+.*")) {
                    System.out.println("숫자는 입력할 수 없습니다.");

                } else {
                    System.out.println("올바른 형식을 입력하세요!");
                }
            } else {
                break;
            }

        }

        while (true) {
            System.out.println("아이디를 입력해주세요.");
            System.out.print("> ");
            id = sc.nextLine();
            id = id.trim();
            if (id.contentEquals("q")) {
                return;
            }
            if (id.length() < 4 || id.length() > 10) {
                System.out.println("4~10자 영문, 숫자를 사용하세요.");
                continue;
            }
            if (id.contains(" ")) {
                System.out.println("공백은 포함될 수 없습니다.");
                continue;
            }
            if (!id.matches("[a-zA-Z0-9]*")) {
                System.out.println("영어와 숫자만 사용해주세요.");
                continue;
            }
            if (id.equals(User.admin_id)) {
                System.out.println("관리자 아이디는 사용할 수 없습니다.");
                continue;
            }

            break;
        }

        while (true) {
            System.out.println("비밀번호를 입력해주세요.");
            System.out.print("> ");
            Password = sc.nextLine();
            Password = Password.trim();
            if (Password.contentEquals("q")) {
                return;
            }
            if (Password.length() < 8 || Password.length() > 16) {
                System.out.println("8~16자의 영어, 숫자, 특수문자를 사용하세요.");
                continue;
            }
            if (Password.contains(" ")) {
                System.out.println("공백은 포함될 수 없습니다.");
                continue;
            }
            if (!Password.matches("[a-zA-Z0-9!@#$%^&*()_+=~-₩]*")) {
                System.out.println("8~16자의 영어, 숫자, 특수문자를 사용하세요.");
                continue;
            }
            break;
        }


        if (!isUniqueID(id)) {
            System.out.println("이미 등록된 아이디입니다.");
            System.out.println("아무 키를 누르면 메인 메뉴로 돌아갑니다.");
            sc.nextLine();
            return;
        }

        Position p = new Position(0,0);
        User newuser = new User(id, Password, name,p);
        System.out.println("회원가입에 성공하였습니다.\n");

        userRepository.addUser(newuser);
        csvManager.writeUserCsv(userRepository);
        System.out.println("아무 키를 누르면 메인 메뉴로 이동합니다.");
        sc.nextLine();
    }
    private boolean isUniqueID(String id) {
        return userRepository.findUserById(id) == null;
    }


    public void user_Login(String time) {
        int x = 0, y = 0;
        Scanner sc = new Scanner(System.in);
        csvManager.readUserCsv();
        String uid;
        String upwd;
        System.out.println("사용자 로그인 메뉴로 돌아가려면 'q'를 누르세요.\n");

        while (true) {
            System.out.println("아이디를 입력해주세요.");
            System.out.print("> ");
            uid = sc.nextLine().trim();
            if (uid.equals("q")) {
                return;
            }

            System.out.println("비밀번호를 입력해주세요.");
            System.out.print("> ");
            upwd = sc.nextLine().trim();
            if (upwd.equals("q")) {
                return;
            }

            User user = userRepository.findUserById(uid);

            if (user != null && user.getUserPassword().equals(upwd)) {
                System.out.println("로그인 성공!");

                while (true) {
                    try {
                        System.out.println("사용자 위치를 (x,y) 형식으로 입력해주세요.");
                        System.out.print("> ");
                        String input = sc.nextLine();
                        String[] coordinates = input.split(",");

                        x = Integer.parseInt(coordinates[0].trim());
                        y = Integer.parseInt(coordinates[1].trim());
                        this.userLocation = new Position(x,y);
                        userRepository.updateUser(user);
                        csvManager.writeUserCsv(userRepository);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("유효한 정수를 입력해주세요.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
                System.out.println("아무 키를 누르면 고객 메인메뉴로 돌아갑니다.");
                sc.nextLine();
                return;
            } else {
                System.out.println("아이디 또는 비밀번호가 일치하지 않습니다."); // 로그인 실패!
                System.out.println("사용자 로그인 메뉴로 돌아가려면 'q'를 누르세요.\n");
            }
        }
    }

    public void admin_Login(String time) {
        //TODO 관리자 로그인 구현
        Scanner sc = new Scanner(System.in);
        String uid;
        String upwd;
        System.out.println("사용자 로그인 메뉴로 돌아가려면 'q'를 누르세요.\n");
        while (true) {
            System.out.println("아이디를 입력해주세요.");
            System.out.print("> ");
            uid = sc.nextLine();
            uid = uid.trim();
            if (uid.equals("q")) {
                return;
            }
            System.out.println("비밀번호를 입력해주세요.");
            System.out.print("> ");
            upwd = sc.nextLine();
            upwd = upwd.trim();
            if (upwd.equals("q")) {
                return;
            }
            if (uid.equals("admin")) {
                if (upwd.equals("1234")) {
                    System.out.println("로그인 성공!");
                    return;
                }
            }
            System.out.println("아이디 또는 비밀번호가 일치하지 않습니다."); //로그인 실패!
        }
    }

    //TODO 가게 관리자가 가게에 대한 정보(메뉴, 가격, 위치 등)를 직접 추가/삭제
    public void admin_SetInformation() {
        Scanner scanner = new Scanner(System.in);
        String option = "";

        while (true) {
            System.out.println("변경할 정보를 입력해주세요. <메뉴, 가격, 위치>");
            System.out.println("관리자 메뉴로 돌아가려면 'q'를 누르세요.");
            System.out.print("> ");

            option = scanner.nextLine().trim();
            if (option.equals("q")) {
                System.out.println("관리자 메뉴로 돌아갑니다.");
                break;
            }
            switch (option) {
                case "메뉴"-> {
                    System.out.println("--- 메뉴 정보 변경하기 ---");
                    System.out.println("1.추가  2.삭제");
                    System.out.print("> ");
                    int choice = scanner.nextInt();
                    if(choice == 1){ //메뉴 추가
                        System.out.print("가게 이름 > ");
                        String storeName = scanner.next();
                        System.out.print("메뉴 이름 > ");
                        String foodName = scanner.next();
                        System.out.print("메뉴 가격 > ");
                        int foodPrice = scanner.nextInt();
                        if(storeRepository.findStoreName(storeName)==null){ // 가게 없으면
                            System.out.println("해당 가게가 존재하지 않습니다.");
                            return;
                        }
                        Store s = storeRepository.findStoreName(storeName);
                        if(foodRepository.findFoodByName(foodName)==null){
                            Food newFood = new Food(s,0,foodName,foodPrice,0);
                            foodRepository.addFood(newFood); // 추가된거 목록에 추가
                            csvManager.writeFoodCsv(foodRepository); // 파일 수정
                            System.out.println("메뉴가 추가되었습니다.");
                            return;
                        }else{
                            System.out.println("이미 존재하는 메뉴입니다.");
                            return;
                        }
                    }
                    else if (choice == 2){ //메뉴 삭제
                        System.out.println("삭제할 메뉴 이름을 입력해주세요");
                        System.out.print("> ");
                        String foodName = scanner.next();
                        if(foodRepository.findFoodByName(foodName)==null){ // 삭제할 메뉴가 없다면
                            System.out.println("해당 메뉴는 존재하지 않습니다.");
                            return;
                        }else{
                            Food f = foodRepository.findFoodByName(foodName);
                            foodRepository.removeFood(f); // 리스트에서 해당 메뉴 지워줌
                            csvManager.writeFoodCsv(foodRepository); // 파일 수정
                            System.out.println("메뉴가 삭제되었습니다.");
                            return;
                        }
                    }
                }

                case "가격" -> {
                    System.out.println("가격 변경할 메뉴 이름을 입력해주세요.");
                    System.out.print("> ");
                    String foodName = scanner.next();
                    Food f = foodRepository.findFoodByName(foodName);
                    if(f==null){
                        System.out.println("해당 메뉴가 존재하지 않습니다.");
                        return;
                    }
                    System.out.println("변경할 금액을 입력해주세요.");
                    System.out.print("> ");
                    int foodPrice = scanner.nextInt();
                    f.setFoodPrice(foodPrice); // 메뉴 금액 변경
                    csvManager.writeFoodCsv(foodRepository); // 파일 수정
                    System.out.println("변경되었습니다.");
                    return;
                }

                case "위치" -> {
                    System.out.println("위치 변경할 가게 이름을 입력해주세요.");
                    System.out.print("> ");
                    String storeName = scanner.next();
                    Store s = storeRepository.findStoreName(storeName);
                    if (s == null) {
                        System.out.println("해당 가게가 존재하지 않습니다.");
                        return;
                    }

                    int x = 0, y = 0;
                    System.out.println("변경할 위치를 (x,y) 형식으로 입력해주세요.");
                    System.out.print("> ");
                    scanner.nextLine();
                    String input = scanner.nextLine();

                    try {
                        String[] coordinates = input.split(",");
                        if (coordinates.length != 2) {
                            System.out.println("위치를 (x,y) 형식으로 입력해주세요.");
                            return;
                        }
                        x = Integer.parseInt(coordinates[0].trim());
                        y = Integer.parseInt(coordinates[1].trim());

                        s.setStoreLocation(new Position(x, y)); // 위치 새로 설정해주기
                        csvManager.writeStoreCsv(storeRepository); // 파일 수정
                        System.out.println("변경되었습니다.");
                    } catch (NumberFormatException e) {
                        System.out.println("유효한 숫자를 입력해주세요."); // 예외 처리
                    } catch (Exception e) {
                        System.out.println("입력 형식이 올바르지 않습니다.");
                    }
                    return;
                }

                default -> {
                    System.out.println("잘못된 입력입니다. <메뉴, 가격, 위치> 중에서 선택하세요.");
                }

            }
        }
    }

}
