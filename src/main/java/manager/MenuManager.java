package manager;

import java.util.Scanner;
import Entity.*;
import Repository.FoodRepository;
import Repository.OrderRepository;
import Repository.StoreRepository;


public class MenuManager {

    static RegexManager regexManager = new RegexManager();
    static CsvManager csvManager = new CsvManager();

    public static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        String date = getDateFromUser(scanner);
        String time = getTimeFromUser(scanner);
        time=date+time;
        System.out.println("사용자가 입력한 날짜와 시간은 " + RegexManager.formatDateTime(time)+"입니다.");

        //파일 싱크맞추는거 2차구현때
        //csvManager.timeSynchronize(time);
        mainMenu(scanner, time);
    }

    //기존에있는 파일 내용그대로 저장되는거까지는 1차때 구현함
    static void mainMenu(Scanner scanner, String time) {

        //프로그램 실행하면
        //가게정보,주문정보,음식정보,유저정보
        //기존파일에있던거 불러와서 다시써야댐
        User user = new User();
        FoodRepository foodRepository = csvManager.readFoodCsv();
        StoreRepository storeRepository= csvManager.readStoreCsv();
        OrderRepository orderRepository = csvManager.readOrderCsv();
        csvManager.writeFoodCsv(foodRepository);
        csvManager.writeOrderCsv(orderRepository);
        csvManager.writeStoreCsv(storeRepository);


        while (true) {
            System.out.println("KUFoodOrder");
            System.out.println("---------------------------");
            System.out.println("1) 회원가입");
            System.out.println("2) 사용자 로그인");
            System.out.println("3) 관리자 로그인");
            System.out.println("4) 종료");
            System.out.print("메뉴 번호를 입력하세요 >>");

            String input = scanner.nextLine();


            if (regexManager.checkFourMenu(input)) {
                int menuNumber = Integer.parseInt(input);
                switch (menuNumber) {
                    case 1:
                        System.out.println("회원가입 메뉴로 이동합니다.");
                        user.register();
                        break;
                    case 2:
                        System.out.println("로그인 메뉴로 이동합니다.");
                        user.user_Login(time);
                        break;
                    case 3:
                        System.out.println("관리자 로그인 메뉴로 이동합니다.");
                        user.admin_Login(time);
                        break;
                    case 4:
                        System.out.println("프로그램을 종료합니다.\n");
                        return;
                }
            }


        }
    }


    private static String getDateFromUser(Scanner scanner) {
        String date;
        do {
            System.out.println("오늘의 날짜를 YYYYMMDD 형식으로 입력해주세요.(예: 20231031)");
            System.out.print(">> ");
            date = scanner.nextLine().trim();


        } while (!regexManager.checkDate(date));
        return date;
    }

    private static String getTimeFromUser(Scanner scanner) {
        String time;
        do {
            System.out.println("현재 시간을 HHMM 형식으로 입력해주세요.(예: 21시 14분 -> 2114)");
            System.out.print(">> ");
            time = scanner.nextLine().trim();

        } while (!regexManager.checkTime(time));
        return time;
    }



}
