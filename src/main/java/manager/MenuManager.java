package manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import Entity.*;
import Repository.FoodRepository;
import Repository.OrderRepository;
import Repository.StoreRepository;
import Repository.UserRepository;


public class MenuManager {

    static RegexManager regexManager = new RegexManager();
    static CsvManager csvManager = new CsvManager();

    public static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        String time=null;
        while (true) {
            String date = getDateFromUser(scanner);
            time = getTimeFromUser(scanner);
            time=date+time;
            System.out.println("사용자가 입력한 날짜와 시간은 " + RegexManager.formatDateTime(time)+"입니다.");

            //파일 싱크맞추는거 2차구현때
            if (csvManager.timeSynchronize(time)) {
                break;
            }
        }
        mainMenu(scanner, time);
    }

    //기존에있는 파일 내용그대로 저장되는거까지는 1차때 구현함
    static void mainMenu(Scanner scanner, String time) {

        //프로그램 실행하면
        //가게정보,주문정보,음식정보,유저정보
        //기존파일에있던거 불러와서 다시써야댐

        // 사용자 홈 디렉토리에 저장할 파일 경로 설정
        String homeDir = System.getProperty("user.home");
        Path foodFilePath = Paths.get(homeDir, "foodData.csv");
        Path storeFilePath = Paths.get(homeDir, "storeData.csv");
        Path orderFilePath = Paths.get(homeDir, "orderData.csv");
        Path userFilePath = Paths.get(homeDir, "userData.csv");

        //TODO 파일존재할경우에도 데이터불러와서 레포지토리에 저장하도록 수정해야함
        // CSV 파일이 없을 경우에만 데이터를 복사
        User user = new User();
        UserRepository userRepository;
        FoodRepository foodRepository;
        StoreRepository storeRepository;
        OrderRepository orderRepository;
        if (Files.notExists(foodFilePath) || Files.notExists(storeFilePath) || Files.notExists(orderFilePath)|| Files.notExists(userFilePath)) {
            userRepository = csvManager.readUserCsv();
            foodRepository = csvManager.readFoodCsv();
            storeRepository = csvManager.readStoreCsv();
            orderRepository = csvManager.readOrderCsv();

            // 초기 데이터만 CSV로 저장
            csvManager.writeUserCsv(userRepository);
            csvManager.writeFoodCsv(foodRepository);
            csvManager.writeOrderCsv(orderRepository);
            csvManager.writeStoreCsv(storeRepository);
        }
        else {
            // 원본 파일 경로
            String[] fileNames = {"orderData.csv", "foodData.csv", "storeData.csv", "userData.csv"};
            // 대상 경로
            String resourceDir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources").toString();

            // resource 폴더가 존재하지 않으면 생성하거나 덮어씌우기
            File resourceFolder = new File(resourceDir);
            if (!resourceFolder.exists()) {
                resourceFolder.mkdirs();
            }

            // 각 파일 읽어서 resource 폴더에 복사
            for (String fileName : fileNames) {
                Path sourcePath = Paths.get(homeDir, fileName);
                Path destinationPath = Paths.get(resourceDir, fileName);

                // 파일 복사
                try {
                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    //System.out.println(fileName + " 파일이 " + resourceDir + " 폴더로 복사되었습니다.");
                } catch (IOException e) {
                    System.err.println("파일 복사 실패: " + fileName);
                    e.printStackTrace();
                }
            }
        }





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
                        String uid = user.user_Login(time);

                        if (uid.equals("q")) {
                            //nothing
                        }
                        else {
                            while (true) {
                                if (4 == OrderManeger.Print_User_Main_Menu(time, uid)) break;        //고객 메인 메뉴 출력
                            }
                        }
                        break;
                    case 3:
                        System.out.println("관리자 로그인 메뉴로 이동합니다.");
                        String s = user.admin_Login(time);
                        if (s.equals("q")) {
                            //nothing
                        }else{
                            while(true){
                                if (3== OrderManeger.Print_Admin_Main_Menu(time)) break;     //관리자 메인 메뉴 출력
                            }
                        }
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
