package de.hs_mannheim.planb.mobilegrowthmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.AppLock;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.AppLockActivity;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.LockManager;

public class MainView extends BaseActivity {
    public static final String TAG = "MainView";

    private MenuItem onOffPinLock;
    private MenuItem changePin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_view, menu);
        onOffPinLock = menu.getItem(0);
        changePin = menu.getItem(1);
        updateMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.on_off_pin) {
            int type = LockManager.getInstance().getAppLock().isPasscodeSet() ? AppLock.DISABLE_PASSLOCK
                    : AppLock.ENABLE_PASSLOCK;
            Intent intent = new Intent(this, AppLockActivity.class);
            intent.putExtra(AppLock.TYPE, type);
            startActivityForResult(intent, type);
        }
        if (id == R.id.change_pin) {
            Intent intent = new Intent(this, AppLockActivity.class);
            intent.putExtra(AppLock.TYPE, AppLock.CHANGE_PASSWORD);
            intent.putExtra(AppLock.MESSAGE,
                    getString(R.string.enter_old_passcode));
            startActivityForResult(intent, AppLock.CHANGE_PASSWORD);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AppLock.DISABLE_PASSLOCK:
                break;
            case AppLock.ENABLE_PASSLOCK:
            case AppLock.CHANGE_PASSWORD:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, getString(R.string.setup_passcode),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        updateMenu();
    }

    private void updateMenu() {
        if (LockManager.getInstance().getAppLock().isPasscodeSet()) {

            onOffPinLock.setTitle(R.string.disable_passcode);
            changePin.setEnabled(true);
        } else {
            onOffPinLock.setTitle(R.string.enable_passcode);
            changePin.setEnabled(false);
        }
    }


}
