package bishe.networkmonitor.dao;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Dell on 5/1/2023.
 */

@Database(entities = {TextMsg.class}, version = 1, exportSchema = false)
public abstract class MsgDatabase extends RoomDatabase {
    public abstract TextMsgDao textMsgDao();
    private static volatile MsgDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static MsgDatabase getDatabase(final Context context, String databaseName, String databaseAssetPath,RoomDatabase.Callback callback){
        if (INSTANCE == null) {
            synchronized (MsgDatabase.class) {
                INSTANCE = Room.databaseBuilder(context, MsgDatabase.class, databaseName)
                        .addCallback(callback)
                        .createFromAsset(databaseAssetPath)
                        .build();
            }
        }
        return INSTANCE;
    }

}
