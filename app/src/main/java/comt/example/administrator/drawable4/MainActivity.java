package comt.example.administrator.drawable4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private ImageView btn_back;
    private SlideMenu slideMenu;
    private LinearLayout main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        slideMenu = (SlideMenu) findViewById(R.id.slideMenu);
        main = (LinearLayout) findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideMenu.clickClose();
            }
        });

      //  slideMenu.isOpen

    }
    public void onClickView(View view){
        switch (view.getId()){
            case R.id.back:
                slideMenu.openMenu();
                break;
            case R.id.drawalayour:
            startActivity(new Intent(this,DrawerLayoutActivity.class));
                break;
        }
    }


}
