package bishe.networkmonitor.dao;

import android.app.Application;
import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MsgViewModel extends AndroidViewModel {
    private MsgRepository msgRepository;
    private Context context;
    private final LiveData<List<TextMsg>> mAllMsg;

    public MsgViewModel(Application application) {
        super(application);
        context = application.getApplicationContext();
        msgRepository = new MsgRepository(
                application,
                context.getString(context.getResources().getIdentifier("database_name", "string", context.getPackageName())),
                context.getString(context.getResources().getIdentifier("database_asset_path", "string", context.getPackageName()))
        );
        mAllMsg = msgRepository.getAllMsg();
    }

    public LiveData<List<TextMsg>> getAllMsg() {
        return mAllMsg;
    }

    public void insert(TextMsg textMsg) {
        msgRepository.insert(textMsg);
    }

    public void insertAll(TextMsg... textMsgs) {
        msgRepository.insertAll(textMsgs);
    }
}
