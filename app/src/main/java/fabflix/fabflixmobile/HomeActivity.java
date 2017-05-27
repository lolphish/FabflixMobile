package fabflix.fabflixmobile;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by phee on 5/24/17.
 */

public class HomeActivity extends Activity{

    private ListView listview;
    private TextView title;

    private ArrayList<String> data;
    ArrayAdapter<String> sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }
}