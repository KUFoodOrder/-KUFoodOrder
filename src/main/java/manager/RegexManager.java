package manager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class RegexManager {

    //승효 추가 본
    public boolean checkMenu(String input, int x) {     //1부터 x까지 정규식 패턴 검증 (x>=1 && x<=10
        String pattern = "";
        if (x >= 1 && x < 10) {
            pattern = "^[1-" + x + "]$";
        } else if (x == 10) {
            pattern = "^(10|[1-9])$";
        }

        // 입력 값이 정규식 패턴과 일치하는지 확인
        if (Pattern.matches(pattern, input)) {
            return true;
        } else {
            System.out.println("잘못된 입력입니다. 1부터 " + x + " 사이의 정수를 입력해야 합니다.");
            return false;
        }
    }

    public boolean checkYN(String input) {
        String pattern = "^[YN]$";

        if (Pattern.matches(pattern, input)) {
            return true;
        } else {
            System.out.println("잘못된 입력입니다. Y 또는 N만 입력할 수 있습니다.");
            return false;
        }
    }
    //승효 추가 본

    public boolean checkThreeMenu(String input) {
        String pattern = "^[1-3]$";

        // 입력 값이 정규식 패턴과 일치하는지 확인
        if (Pattern.matches(pattern, input)) {
            return true;
        } else {
            System.out.println("잘못된 입력입니다. 1부터 3 사이의 숫자를 입력해야 합니다.");
            return false;

        }
    }
    public boolean checkFourMenu(String input) {
        String pattern = "^[1-4]$";

        // 입력 값이 정규식 패턴과 일치하는지 확인
        if (Pattern.matches(pattern, input)) {
            return true;
        } else {
            System.out.println("잘못된 입력입니다. 1부터 4 사이의 숫자를 입력해야 합니다.");
            return false;

        }
    }

    public boolean checkFiveMenu(String input) {
        String pattern = "^[1-5]$";

        // 입력 값이 정규식 패턴과 일치하는지 확인
        if (Pattern.matches(pattern, input)) {
            return true;
        } else {
            System.out.println("잘못된 입력입니다. 1부터 5 사이의 숫자를 입력해야 합니다.");
            return false;

        }
    }

    public boolean checkDate(String date) {
        // YYYYMMDD 형식의 정규식
        String regex = "^[0-9]{4}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$";

        if (!date.matches(regex)) {
            System.out.println("날짜는 YYYYMMDD 형식의 숫자로 입력해야합니다.");
            return false;
        } else if (!checkDateFormat(date)) {
            System.out.println("달력에 존재하는 날짜를 입력해주세요");
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkDateFormat(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMdd");
            LocalDate.parse(date, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean checkTime(String time) {
        // HHmm 형식의 정규식
        String regex = "^[0-2][0-9][0-5][0-9]$";

        if (!time.matches(regex)) {
            System.out.println("시간은 HHmm 형식의 숫자로 입력해야합니다.");
            return false;
        } else if (!checkTimeFormat(time)) {
            System.out.println("유효한 시간을 입력해주세요.");
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkTimeFormat(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
            LocalTime.parse(time, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }



    public static String formatDateTime(String dateTime) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");

        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, inputFormatter);
        return localDateTime.format(outputFormatter);
    }


}
