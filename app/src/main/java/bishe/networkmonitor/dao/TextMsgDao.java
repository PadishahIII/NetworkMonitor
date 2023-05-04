package bishe.networkmonitor.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Created by Dell on 5/1/2023.
 */

@Dao
public interface TextMsgDao {
    @Query("select * from textMsg")
    LiveData<List<TextMsg>> getAll();

    @Query("select count(*) from textMsg")
    LiveData<Integer> getCount();

    @Query("select * from textMsg where timestamp>:timestamp")
    LiveData<List<TextMsg>> getMsgAfter(Long timestamp);

    @Query("select max(id) from textMsg")
    Integer getMaxId();

    @Insert
    void insert(TextMsg textMsg);

    @Insert
    void insertAll(TextMsg... textMsgs);
}
