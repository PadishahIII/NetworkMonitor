package bishe.networkmonitor.dao;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;

public class MsgRepository {
    private TextMsgDao dao;
    private LiveData<List<TextMsg>> mAllMsg;

    MsgRepository(Application application, String databaseName, String databaseAssetPath) {
        MsgDatabase db = MsgDatabase.getDatabase(application, databaseName, databaseAssetPath, sRoomDatabaseCallback);
        dao = db.textMsgDao();
        mAllMsg = dao.getAll();
    }

    private RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            MsgDatabase.databaseWriteExecutor.execute(() -> {
                TextMsg textMsg = TextMsg.getDefault();
                textMsg.primaryText = "test1";
                textMsg.to = 3;
//                insert(textMsg);
                TextMsg textMsg2 = TextMsg.getDefault();
                textMsg.primaryText = "test2";
                textMsg.to = 3;
//                insert(textMsg2);
                Log.d("listcallback", "insert complete");
            });
        }
    };

    LiveData<List<TextMsg>> getAllMsg() {
        return mAllMsg;
    }

    void insert(TextMsg textMsg) {
        MsgDatabase.databaseWriteExecutor.execute(() -> {
            int maxId = dao.getMaxId();
            textMsg.id = maxId + 1;
            dao.insert(textMsg);
        });
    }

    void insertAll(TextMsg... textMsgs) {
        MsgDatabase.databaseWriteExecutor.execute(() -> {
            int maxId = dao.getMaxId();
            int id = maxId + 1;
            for (TextMsg msg :
                    textMsgs) {
                msg.id = id++;
            }
            dao.insertAll(textMsgs);
        });
    }
}
