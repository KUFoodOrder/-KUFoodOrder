package Entity;

import Repository.UserRepository;
import manager.CsvManager;

import java.util.HashMap;
import java.util.List;

//사용자 객체
public class User {
    private String userId; //사용자 아이디
    private String userPassword; // 사용자 비밀번호
    private String userName; // 사용자 이름
    private Position userLocation; //사용자 위치
    private List<Order> userOderList; //사용자 주문내역 리스트
    HashMap<Food, Integer> userOrderMap; //사용자 주문내역 리스트(메뉴,주문한양)

    UserRepository userRepository = UserRepository.getInstance();

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
//        String name, id, PhoneNum, Password;
//        Scanner sc = new Scanner(System.in);
//        System.out.println("<회원 가입>");
//        System.out.println("메인 메뉴로 돌아가려면 'q'를 누르세요.\n");
//        while (true) {
//            System.out.print("이 름 : ");
//            name = sc.nextLine();
//            name = name.trim();
//            if (name.contentEquals("q")) {
//                return;
//            }
//            if (name.isEmpty() || name.length() > 15) {
//                System.out.println("이름의 길이는 1이상 15이하여야 합니다!");
//                continue;
//            }
//            if (!name.matches("^[가-힣]+$")) {
//                if (name.matches(".*[a-zA-Z]+.*")) {
//                    System.out.println("영어를 포함한 이름은 입력할 수 없습니다.");
//                } else if (name.contains(" ") || name.contains("\t")) {
//                    System.out.println("공백은 입력할 수 없습니다.");
//                } else if (name.matches(".*\\d+.*")) {
//                    System.out.println("숫자는 입력할 수 없습니다.");
//
//                } else {
//                    System.out.println("올바른 형식을 입력하세요!");
//                }
//            } else {
//                break;
//            }
//
//        }
//
//        while (true) {
//            System.out.print("아이디 : ");
//            id = sc.nextLine();
//            id = id.trim();
//            if (id.contentEquals("q")) {
//                return;
//            }
//            if (id.length() < 4 || id.length() > 10) {
//                System.out.println("4~10자 영문, 숫자를 사용하세요.");
//                continue;
//            }
//            if (id.contains(" ")) {
//                System.out.println("공백은 포함될 수 없습니다.");
//                continue;
//            }
//            if (!id.matches("[a-zA-Z0-9]*")) {
//                System.out.println("영어와 숫자만 사용해주세요.");
//                continue;
//            }
//            if (id.equals(User.admin_id)) {
//                System.out.println("관리자 아이디는 사용할 수 없습니다.");
//                continue;
//            }
//
//            break;
//        }
//
//        while (true) {
//            System.out.print("전화번호 : ");
//            PhoneNum = sc.nextLine();
//            PhoneNum = PhoneNum.trim();
//            if (PhoneNum.contentEquals("q")) {
//                return;
//            }
//            if (PhoneNum.isEmpty() || PhoneNum.length() > 15) {
//                System.out.println("1~15자의 숫자만 사용하세요.");
//                continue;
//            }
//            if (PhoneNum.contains(" ")) {
//                System.out.println("공백은 포함될 수 없습니다.");
//                continue;
//            }
//            if (!PhoneNum.matches("[0-9]*")) {
//                System.out.println("숫자만 사용해주세요.");
//                continue;
//            }
//
//            break;
//        }
//        while (true) {
//            System.out.print("비밀번호 : ");
//            Password = sc.nextLine();
//            Password = Password.trim();
//            if (Password.contentEquals("q")) {
//                return;
//            }
//            if (Password.length() < 8 || Password.length() > 16) {
//                System.out.println("8~16자의 영어, 숫자, 특수문자를 사용하세요.");
//                continue;
//            }
//            if (Password.contains(" ")) {
//                System.out.println("공백은 포함될 수 없습니다.");
//                continue;
//            }
//            if (!Password.matches("[a-zA-Z0-9!@#$%^&*()_+=~-₩]*")) {
//                System.out.println("8~16자의 영어, 숫자, 특수문자를 사용하세요.");
//                continue;
//            }
//            break;
//        }
//        if (!isUniquePhoneNum(PhoneNum)) {
//            System.out.println("이미 등록된 전화번호, 아이디입니다.");
//            System.out.println("아무 키를 누르면 메인 메뉴로 돌아갑니다.");
//            sc.nextLine();
//            return;
//        }
//        if (!isUniqueID(id)) {
//            System.out.println("이미 등록된 아이디입니다.");
//            System.out.println("아무 키를 누르면 메인 메뉴로 돌아갑니다.");
//            sc.nextLine();
//            return;
//        }
//        if (!isUniquePhoneNum(PhoneNum)) {
//            System.out.println("이미 등록된 전화번호입니다.");
//            System.out.println("아무 키를 누르면 메인 메뉴로 돌아갑니다.");
//            sc.nextLine();
//            return;
//        }
//        User newuser = new User(id, Password, name, PhoneNum, usingSeatNum);
//        System.out.println("회원가입에 성공하였습니다.\n");
//        users.add(newuser);
//        csvManager.writeUserCsv(users);
//        //users = csvManager.readUserCsv();
//        System.out.println("아무 키를 누르면 메인 메뉴로 이동합니다.");
//        sc.nextLine();
    }


//    private boolean isUniqueID(String id) {
////        boolean flag=true;
////        for (User user : users) {
////            if (user.getUserId().equals(id)) {
////                flag= false;
////                break;
////            }
////        }
////        return flag;
//    }

    public void user_Login(String time) {
        //TODO 사용자 로그인 구현
//        Scanner sc = new Scanner(System.in);
//        csvManager.readUserCsv();
//        String uid;
//        String upwd;
//        System.out.println("사용자 로그인 메뉴로 돌아가려면 'q'를 누르세요.\n");
//        while (true) {
//            System.out.print("아이디 : ");
//            uid = sc.nextLine();
//            uid = uid.trim();
//            if (uid.equals("q")) {
//                return;
//            }
//            System.out.print("비밀번호 : ");
//            upwd = sc.nextLine();
//            upwd = upwd.trim();
//            if (upwd.equals("q")) {
//                return;
//            }
//            for (User u : users) {       //회원 정보 일치 체크!
//                if (u.getUserId().equals(uid))
//                    if (u.getUserPassword().equals(upwd)) {
//                        System.out.println("로그인 성공!");
//                        System.out.println("아무 키를 누르면 예약 메뉴로 돌아갑니다.");
//                        sc.nextLine();
//                        User.Seat seat = User.Seat.getInstance();
//                        seat.setUser(u);
//                        seat.reservation_Menu(time); //seat menu
//                        return;
//                    }
//            }
//            System.out.println("아이디 또는 비밀번호가 일치하지 않습니다."); //로그인 실패!
//            System.out.println("사용자 로그인 메뉴로 돌아가려면 'q'를 누르세요.\n");
//        }
    }

    public void admin_Login(String time) {
        //TODO 관리자 로그인 구현
//        Scanner sc = new Scanner(System.in);
//        String uid;
//        String upwd;
//        System.out.println("사용자 로그인 메뉴로 돌아가려면 'q'를 누르세요.\n");
//        while (true) {
//            System.out.print("아이디 : ");
//            uid = sc.nextLine();
//            uid = uid.trim();
//            if (uid.equals("q")) {
//                return;
//            }
//            System.out.print("비밀번호 : ");
//            upwd = sc.nextLine();
//            upwd = upwd.trim();
//            if (upwd.equals("q")) {
//                return;
//            }
//            if (uid.equals("admin")) {
//                if (upwd.equals("1234")) {
//                    User.Seat seat = User.Seat.getInstance();
//                    seat.setUsers(users);
//                    seat.admin_Menu(time);
//                    return;
//                }
//            }
//            System.out.println("아이디 또는 비밀번호가 일치하지 않습니다."); //로그인 실패!
//        }
    }

    //TODO 가게 관리자가 가게에 대한 정보(메뉴, 가격, 위치 등)를 직접 추가/삭제

}
