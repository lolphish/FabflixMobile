package fabflix.fabflixmobile;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phee on 5/24/17.
 */

public class HomeActivity extends Activity{

    private ListView listview;
    private TextView title;
    private Dialog progressDialog;
    private int totalItems;
    private int itemsPerPage;
    private Button prevButton;
    private Button nextButton;
    private List<String> data;
    ArrayAdapter<String> sd;
    private int increment;
    private int numOfPages;
    private TextView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listview = (ListView)findViewById(R.id.list);
        prevButton = (Button)findViewById(R.id.prev_button);
        nextButton = (Button)findViewById(R.id.next_button);
        title = (TextView)findViewById(R.id.title);
        searchBar = (TextView) findViewById(R.id.search_bar);
        prevButton.setEnabled(false);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String search = extras.getString("searchValue");
            searchBar.setText(search);
            searchHandler(search);
        } else {
            searchHandler("");
        }

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    Intent nextPage = new Intent(getApplicationContext(), HomeActivity.class);
                    if(searchBar.getText().toString().trim().length() != 0)
                        nextPage.putExtra("searchValue", searchBar.getText().toString());
                    startActivity(nextPage);
                }
                return false;
            }
        });




    }

    // Connect to the REST address and get information
    protected void searchHandler(final String search) {
        final Map<String, String> params = new HashMap<>();

        // no user is logged in, so we must connect to the server
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://" + Constants.ADDRESS + ":8080/fabflix/MobileMain";
        if (!search.isEmpty())
            params.put("searchString", search);
        progressDialog = ProgressDialog.show(
                HomeActivity.this, "", "Loading Movies...", true);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d("response", response);
                        increment = 0;
                        data = Arrays.asList(response.split(";"));
                        totalItems = data.size();
                        itemsPerPage = 10;
                        int divNum = totalItems % itemsPerPage;
                        numOfPages = totalItems / itemsPerPage + (divNum == 0 ? 0 : 1);
                        checkButtons();
                        loadList(0);
                        prevButton.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                loadList(--increment);
                                checkButtons();
                            }
                        });

                        nextButton.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                loadList(++increment);
                                checkButtons();
                            }
                        });

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(HomeActivity.this, error.toString() , Toast.LENGTH_LONG).show();
                        Log.d("security.error", error.toString());
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                if (!search.isEmpty())
                    params.put("searchString", search);
                return params;
            }
        };

        queue.add(postRequest);
    }

    // Load the given data into it's own list item
    private void loadList(int number) {
        ArrayList<String> sort = new ArrayList<>();
        title.setText("Page " + (number + 1) + " of " + numOfPages);
        int start = number * itemsPerPage;
        for (int i = start; i < (start) + itemsPerPage; ++i) {
            if (i < data.size()) {
                sort.add(data.get(i));
            } else
                break;
        }
        sd = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sort);
        listview.setAdapter(sd);
    }

    // Check the pages to disable the buttons accordingly
    private void checkButtons()
    {
        if(increment+1 == numOfPages)
            nextButton.setEnabled(false);
        else if(increment == 0)
            prevButton.setEnabled(false);
        else {
            prevButton.setEnabled(true);
            nextButton.setEnabled(true);
        }
    }
}