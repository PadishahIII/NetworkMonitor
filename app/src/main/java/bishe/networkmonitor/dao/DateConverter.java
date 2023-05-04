package bishe.networkmonitor.dao;

import java.util.Date;

import androidx.room.TypeConverter;

/**
 * Created by Dell on 5/1/2023.
 */

public class DateConverter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
