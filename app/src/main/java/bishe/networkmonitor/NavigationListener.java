package bishe.networkmonitor;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

/**
 * Created by Dell on 4/27/2023.
 */
public class NavigationListener extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static NavigationListener ourInstance = new NavigationListener();
    private AppCompatActivity curActivity;
    public static NavigationListener getInstance(AppCompatActivity curActivity) {
        ourInstance.curActivity = curActivity;
        return ourInstance;
    }

    private NavigationListener() {
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_app_list) {
//            AppListAdapter adapter = new AppListAdapter(this, this.appInfos);
//            ListView listView = (ListView) findViewById(R.id.applistmain);
//            listView.setAdapter(adapter);
            Intent intent = new Intent(this.curActivity, AppListMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
