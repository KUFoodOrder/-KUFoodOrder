package Entity;

import Repository.FoodRepository;
import Repository.StoreRepository;
import Repository.UserRepository;
import manager.CsvManager;
import manager.RegexManager;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static manager.MenuManager.Synchronize_csv_resource_to_home;


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
    RegexManager regexManager = new RegexManager();

    static String admin_id = "admin";

    CsvManager csvManager = new CsvManager();

    int xPos = 0, yPos = 0;

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
    }


    public void register() {
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


    public void setUserLocation(Position userLocation) {
        this.userLocation = userLocation;
    }

    public String user_Login(String time) {
        Scanner sc = new Scanner(System.in);
        csvManager.home_readUserCsv();
        String uid;
        String upwd;
        System.out.println("사용자 로그인 메뉴로 돌아가려면 'q'를 누르세요.\n");

        while (true) {
            System.out.println("아이디를 입력해주세요.");
            System.out.print("> ");
            uid = sc.nextLine().trim();
            if (uid.equals("q")) {
                return uid;
            }

            System.out.println("비밀번호를 입력해주세요.");
            System.out.print("> ");
            upwd = sc.nextLine().trim();
            if (upwd.equals("q")) {
                uid="q";
                return uid;
            }

            User user = userRepository.findUserById(uid);

            if (user != null && user.getUserPassword().equals(upwd)) {
                System.out.println("로그인 성공!");

                while (true) {
                    try {
                        System.out.println("사용자 위치를 (x y) 형식으로 입력해주세요.");
                        System.out.print("> ");
                        String input = sc.nextLine();
                        input=input.trim();
                        if (!regexManager.check_XY(input)) continue;

                        String[] coordinates = input.split("\\s+");

                        xPos = Integer.parseInt(coordinates[0].trim());
                        yPos = Integer.parseInt(coordinates[1].trim());

                        user.setUserLocation(new Position(xPos,yPos));
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
                return uid;
            } else {
                System.out.println("아이디 또는 비밀번호가 일치하지 않습니다."); // 로그인 실패!
                System.out.println("사용자 로그인 메뉴로 돌아가려면 'q'를 누르세요.\n");
            }
        }
    }

    public String admin_Login(String time) {
        Scanner sc = new Scanner(System.in);
        String uid;
        String upwd;
        System.out.println("관리자 로그인 메뉴로 돌아가려면 'q'를 누르세요.\n");
        while (true) {
            System.out.println("아이디를 입력해주세요.");
            System.out.print("> ");
            uid = sc.nextLine();
            uid = uid.trim();
            if (uid.equals("q")) {
                return "q";
            }
            System.out.println("비밀번호를 입력해주세요.");
            System.out.print("> ");
            upwd = sc.nextLine();
            upwd = upwd.trim();
            if (upwd.equals("q")) {
                return "q";
            }
            if (uid.equals("admin")) {
                if (upwd.equals("1234")) {
                    System.out.println("로그인 성공!");
                    return "";
                }
            }
            System.out.println("아이디 또는 비밀번호가 일치하지 않습니다."); //로그인 실패!
            return "q";
        }
    }

    public void admin_SetInformation() {
        Synchronize_csv_resource_to_home();
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
                    int choice = 0;
                    while (true) {
                        if (scanner.hasNextInt()) {
                            choice = scanner.nextInt();
                            break;
                        } else {
                            System.out.println("1 또는 2를 입력하세요.");
                            scanner.next();
                        }
                    }
                    if(choice == 1){ //메뉴 추가
                        int foodPrice = 0;
                        boolean validInput = false;
                        String storeName = "";
                        String foodName = "";
                        // 가게 이름 입력 예외 처리
                        // 가게 이름 입력 예외 처리
                        while (true) {
                            System.out.print("가게 이름 > ");
                            storeName = scanner.next().trim(); // 공백 제거

                            // 한글만 입력 받도록 정규 표현식 사용
                            if (regexManager.checkKorean(storeName)) {

                                if (storeRepository.findStoreName(storeName) != null) { // 가게가 존재하는지 확인
                                    break; // 유효한 가게 이름이면 루프 종료
                                } else {
                                    System.out.println("해당 가게가 존재하지 않습니다.");
                                }
                            } else {
                                System.out.println("가게 이름은 한글로만 입력해주세요."); // 한글 이외의 입력 안내
                            }
                        }
                        Store s = storeRepository.findStoreName(storeName);
                        // 메뉴 이름 입력 예외 처리
                        while (true) {
                            System.out.print("메뉴 이름 > ");
                            foodName = scanner.next().trim(); // 공백 제거

                            // 한글만 입력 받도록 정규 표현식 사용
                            if (regexManager.checkKorean(foodName)) {
                                if (s.isAddFoodToMenu(foodName)) { // 메뉴가 존재하지 않으면
                                    break; // 유효한 메뉴 이름이면 루프 종료
                                } else {
                                    System.out.println("이미 존재하는 메뉴입니다."); // 이미 존재하는 메뉴일 경우 안내
                                }
                            } else {
                                System.out.println("메뉴 이름은 한글로만 입력해주세요."); // 한글 이외의 입력 안내
                            }
                        }
                        while (!validInput) {
                            System.out.print("메뉴 가격 > ");
                            if (scanner.hasNextInt()) {
                                foodPrice = scanner.nextInt();
                                validInput = true;
                            } else {
                                System.out.println("유효한 가격을 입력하세요.");
                                scanner.next();
                            }
                        }

                            Food newFood = new Food(s,s.getMenuListSize()+1,foodName,foodPrice,0);
                            foodRepository.addFood(storeName,newFood); // 추가된거 목록에 추가
                            s.addFoodToMenu(newFood);
                            csvManager.writeFoodCsv(foodRepository); // 파일 수정
                            csvManager.writeStoreCsv(storeRepository);
                            System.out.println("메뉴가 추가되었습니다.");
                            return;
                    }
                    else if (choice == 2) { // 메뉴 삭제
                        // 가게 이름 입력 예외 처리
                        String storeName = "";
                        String foodName = "";
                        while (true) {
                            System.out.print("가게 이름 > ");
                            storeName = scanner.next().trim(); // 공백 제거

                            // 한글만 입력 받도록 정규 표현식 사용
                            if (regexManager.checkKorean(storeName)) {
                                if (storeRepository.findStoreName(storeName) != null) { // 가게가 존재하는지 확인
                                    break; // 유효한 가게 이름이면 루프 종료
                                } else {
                                    System.out.println("해당 가게가 존재하지 않습니다.");
                                }
                            } else {
                                System.out.println("가게 이름은 한글로만 입력해주세요."); // 한글 이외의 입력 안내
                            }
                        }
                        Store s = storeRepository.findStoreName(storeName);

                        while (true) {
                            System.out.println("삭제할 메뉴 이름을 입력해주세요");
                            System.out.print("> ");
                            foodName = scanner.next(); // 한글 입력 받기

                            // 한글로만 이루어져 있는지 확인
                            if (regexManager.checkKorean(foodName)) {
                                scanner.nextLine(); // 버퍼 비우기
                                break; // 유효한 한글 입력이 들어온 경우 루프 종료
                            } else {
                                scanner.nextLine(); // 잘못된 입력이 들어온 경우 버퍼 비우기
                            }
                        }

                        if (s.isAddFoodToMenu(foodName)) { // 삭제할 메뉴가 없다면
                            System.out.println("해당 메뉴는 존재하지 않습니다.");
                        } else {
                            Food f = foodRepository.findFoodByFoodName(s,foodName);
                            foodRepository.removeFood(s,f); // 리스트에서 해당 메뉴 지워줌
                            s.removeFoodFromMenu(foodName);
                            csvManager.writeFoodCsv(foodRepository); // 파일 수정
                            csvManager.writeStoreCsv(storeRepository);
                            System.out.println("메뉴가 삭제되었습니다.");
                            return;
                        }
                    }
                }

                case "가격" -> {
                    String storeName = "";
                    String foodName = "";
                    while (true) {
                        System.out.println("가격 변경할 메뉴의 가게이름을 입력해주세요.");
                        storeName = scanner.next().trim(); // 공백 제거

                        // 한글만 입력 받도록 정규 표현식 사용
                        if (regexManager.checkKorean(storeName)) {
                            if (storeRepository.findStoreName(storeName) != null) { // 가게가 존재하는지 확인
                                break; // 유효한 가게 이름이면 루프 종료
                            } else {
                                System.out.println("해당 가게가 존재하지 않습니다.");
                            }
                        } else {
                            System.out.println("가게 이름은 한글로만 입력해주세요."); // 한글 이외의 입력 안내
                        }
                    }
                    Store s = storeRepository.findStoreName(storeName);

                    while (true) {
                        System.out.println("가격 변경할 메뉴 이름을 입력해주세요.");
                        System.out.print("> ");
                        foodName = scanner.next(); // 한글 입력 받기

                        // 한글로만 이루어져 있는지 확인
                        if (regexManager.checkKorean(foodName)) {
                            scanner.nextLine(); // 버퍼 비우기
                            break; // 유효한 한글 입력이 들어온 경우 루프 종료
                        } else {
                            scanner.nextLine(); // 잘못된 입력이 들어온 경우 버퍼 비우기
                        }
                    }
                    if (s.isAddFoodToMenu(foodName)) {
                        System.out.println("해당 메뉴는 존재하지 않습니다.");
                        return;
                    } else {
                        int foodPrice = 0;
                        while (true) {
                            System.out.println("변경할 금액을 입력해주세요.");
                            System.out.print("> ");

                            // 가격 입력 예외 처리
                            if (scanner.hasNextInt()) {
                                foodPrice = scanner.nextInt();
                                break;
                            } else {
                                System.out.println("유효한 가격을 입력하세요.");
                                scanner.next();
                            }
                        }
                        Food f = foodRepository.findFoodByFoodName(s,foodName);
                        f.setFoodPrice(foodPrice); // 메뉴 금액 변경
                        csvManager.writeFoodCsv(foodRepository); // 파일 수정
                        System.out.println("변경되었습니다.");
                        return;

                    }

                }

                case "위치" -> {
                    System.out.println("위치 변경할 가게 이름을 입력해주세요.");
                    System.out.print("> ");
                    String storeName = scanner.next();
                    if (!storeName.matches(storeName)) {
                        System.out.println("가게 이름은 한글로만 입력할 수 있습니다.");
                        return;
                    }
                    Store s = storeRepository.findStoreName(storeName);
                    if (s == null) {
                        System.out.println("해당 가게가 존재하지 않습니다.");
                        return;
                    }

                    int x = 0, y = 0;
                    while (true) {
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
                            break;     // 정상적으로 위치 바뀌면?
                        } catch (NumberFormatException e) {
                            System.out.println("유효한 숫자를 입력해주세요.");
                        } catch (Exception e) {
                            System.out.println("입력 형식이 올바르지 않습니다.");
                        }
                        return;
                    }
                }

                default -> {
                    System.out.println("잘못된 입력입니다. <메뉴, 가격, 위치> 중에서 선택하세요.");
                }

            }
        }
    }

}
