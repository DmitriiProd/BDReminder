import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainClass {

    //private static final HashMap<String, Long> birthdays = new HashMap<>();
    private static HashMap<String, Long> birthdays = new HashMap<>();

    private static final int maxCheckDays = 7;

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Укажите путь к файлу!");
            return;
        }

        File file = new File(args[0]);

        if (!file.exists()) {
            System.out.println("Указанный файл не найден! (" + file.getAbsolutePath() + ")");
            return;
        }

        Calendar curr = Calendar.getInstance();
        curr.setTimeInMillis(System.currentTimeMillis());
        curr.set(Calendar.HOUR_OF_DAY, 0);
        curr.set(Calendar.MINUTE, 0);
        curr.set(Calendar.SECOND, 0);
        curr.set(Calendar.MILLISECOND, 0);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";

            while((line = reader.readLine()) != null) {
                String birthday = line.split("\\|")[0];
                String name = line.split("\\|")[1];

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = dateFormat.parse(birthday);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                checkBirthDay(curr, date, name);
            }

            birthdays = sortByValue(birthdays);

            birthdays.forEach((name, days) -> {
                if(days == 0) System.out.println("Сегодня день рождения у " + name);
                else System.out.println("У " + name + " день рождения через " + days + " " + formatName(days));
            });

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void checkBirthDay(Calendar current, Date date, String name) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if(current.equals(calendar)) {
            birthdays.put(name, 0L);
        }

        if(current.before(calendar)) {
            long remainingDays = TimeUnit.MILLISECONDS.toDays(
                    Math.abs(calendar.getTimeInMillis() - current.getTimeInMillis()));

            if(remainingDays <= maxCheckDays) {
                birthdays.put(name, remainingDays);
            }
        }
    }

    public static HashMap<String, Long> sortByValue(HashMap<String, Long> hm) {

        return hm.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static String formatName(long input){

        final String[] names = new String[] {"день", "дня", "дней"};


        int cef;

        if(input % 10 == 1 && input % 100 != 11) cef = 0;

        else if(2 <= input % 10 && input % 10 <= 4 && (input % 100 < 10 || input % 100 >= 20)) cef = 1;

        else cef = 2;

        return names[cef];
    }

}
