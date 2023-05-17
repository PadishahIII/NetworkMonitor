package bishe.networkmonitor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bishe.networkmonitor.dao.MsgViewModel;
import bishe.networkmonitor.dao.TextMsg;

public class MsgActivity extends AppCompatActivity {
    private MsgViewModel msgViewModel;
    private List<TextMsg> msgList;
    public static final int NEW_MSG_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_msg));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MsgActivity.this, EditNewMsgActivity.class);
            startActivityForResult(intent, NEW_MSG_ACTIVITY_REQUEST_CODE);
        });

        final MsgListAdapter adapter = new MsgListAdapter(new MsgListAdapter.MsgDiff(), this);
        RecyclerView recycleView = (RecyclerView) findViewById(R.id.msg_list);
        recycleView.setAdapter(adapter);
        recycleView.setLayoutManager(new LinearLayoutManager(this));

        this.msgViewModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(MsgViewModel.class);
        this.msgViewModel.getAllMsg().observe(this, msgs -> {
            Collections.sort(msgs, new Comparator<TextMsg>() {
                @Override
                public int compare(TextMsg o1, TextMsg o2) {
                    return Long.compare(o2.timestamp, o1.timestamp);
                }
            });
            adapter.submitList(msgs);
        });
        List<TextMsg> l = adapter.getCurrentList();
//        Log.i("listnum", Integer.toString(l.size()));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_MSG_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            TextMsg textMsg = TextMsg.getDefault();
            textMsg.primaryText = data.getStringExtra("reply");
            msgViewModel.insert(textMsg);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }


}
