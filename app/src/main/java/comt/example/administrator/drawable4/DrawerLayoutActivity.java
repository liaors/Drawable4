package comt.example.administrator.drawable4;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;


/**
 * Created by Rs on 2017/9/8.
 */

public class DrawerLayoutActivity extends AppCompatActivity {
    private DrawerLayout drawalayour;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout_activity);
        initView();
        drawer = (LinearLayout) findViewById(R.id.drawer);
        initData();
    }

    private void initData() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int witdh =metric.widthPixels*4/5;
        DrawerLayout.LayoutParams linearParams = (DrawerLayout.LayoutParams) drawer.getLayoutParams();
        linearParams.width = witdh;
        drawer.setLayoutParams(linearParams);

    }



    private void initView() {
        drawalayour = (DrawerLayout) findViewById(R.id.drawalayour);
    }



    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.drawerlayout:
             drawalayour.openDrawer(Gravity.RIGHT);
                break;
        }
    }


}
