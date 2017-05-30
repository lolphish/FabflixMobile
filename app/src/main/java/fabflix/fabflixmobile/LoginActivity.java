package fabflix.fabflixmobile;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by phee on 5/24/17.
 */

public class LoginActivity extends Activity{

    private EditText emailText;
    private EditText passwordText;
    private Button mloginButton;
    private Dialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = (EditText) findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password);
        mloginButton = (Button) findViewById(R.id.loginButton);

        // Go to password after user presses enter
        emailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    passwordText.requestFocus();
                }
                return false;
            }
        });

        // Clicks login button after user presses enter
        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    mloginButton.performClick();
                }
                return false;
            }
        });

        mloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tEmailText = emailText.getText().toString();
                String tPassword = passwordText.getText().toString();
                loginHandler(tEmailText, tPassword);

            }
        });


    }

    public void loginHandler(final String email, final String password) {


        final Map<String, String> params = new HashMap<>();

        // no user is logged in, so we must connect to the server
        RequestQueue queue = Volley.newRequestQueue(this);

        final Context context = this;
        String url = "http://" + Constants.ADDRESS + ":8080/fabflix/MobileLogin";
        progressDialog = ProgressDialog.show(
                LoginActivity.this, "", "Logging in...", true);

        if (email.isEmpty() || password.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Email or password not entered." , Toast.LENGTH_LONG).show();
            return;
        }

        params.put("email", email);
        params.put("pass", password);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);
                        if (response.contains("true"))
                        {
                            progressDialog.dismiss();
                            Intent home = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(home);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Invalid Loging" , Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(LoginActivity.this, error.toString() , Toast.LENGTH_LONG).show();
                        Log.d("security.error", error.toString());
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                params.put("email", email);
                params.put("pass", password);
                return params;
            }
        };

        queue.add(postRequest);
    }


}
