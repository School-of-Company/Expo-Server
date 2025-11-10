package team.startup.expo.global.date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class DateUtil {

    public boolean dateComparison(String startDate, String endDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date start = simpleDateFormat.parse(String.valueOf(startDate));
            Date end = simpleDateFormat.parse(String.valueOf(endDate));
            Date now = simpleDateFormat.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            return !now.before(start) && !now.after(end);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean dateTimeComparison(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime now = LocalDateTime.now();

            return (now.isAfter(startDateTime) || now.isEqual(startDateTime)) &&
                    (now.isBefore(endDateTime) || now.isEqual(endDateTime));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
