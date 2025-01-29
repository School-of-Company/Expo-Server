package team.startup.expo.global.date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@Slf4j
public class DateUtil {

    public boolean dateComparison(String startDate, String endDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date start = simpleDateFormat.parse(String.valueOf(startDate));
            Date end = simpleDateFormat.parse(String.valueOf(endDate));
            Date now = simpleDateFormat.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            log.info(startDate);
            log.info(start.toString());
            log.info(end.toString());
            log.info(now.toString());

            return !now.before(start) && !now.after(end);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
