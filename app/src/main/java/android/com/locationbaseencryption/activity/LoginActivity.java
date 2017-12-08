package android.com.locationbaseencryption.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.com.locationbaseencryption.Constant;
import android.com.locationbaseencryption.R;
import android.com.locationbaseencryption.preferance.Shareprefrance;
import android.com.locationbaseencryption.retro.RegisterResponse;
import android.com.locationbaseencryption.retro.Retro;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button login_btn;
    String str_email, str_password, str_user_type;
    TextView register_tv;
    Spinner ut_usertype;
    Shareprefrance shareprefrance;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    String user_type = "";
    Dialog dialog;
    AlertDialog alertDialog;
    private TextView tv_forgot_password;
    private Context context = LoginActivity.this;
    private String TAG = LoginActivity.class.getSimpleName();
    private int server_otp = 0;

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.login);
        setSupportActionBar(toolbar);

        login_btn = (Button) findViewById(R.id.login_btn);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        register_tv = (TextView) findViewById(R.id.register_tv);
        ut_usertype = (Spinner) findViewById(R.id.ut_usertype);
        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);

        et_email.addTextChangedListener(new MyTextWatcher(et_email));
        et_password.addTextChangedListener(new MyTextWatcher(et_password));

        shareprefrance = new Shareprefrance();
        progressDialog = new ProgressDialog(context);

        user_type = getIntent().getStringExtra(Constant.USERTYPE);
        Log.e(TAG, " Login user type is as follow as " + user_type);

        if (shareprefrance.isLOgin(context)) {
            startActivity(new Intent(context, HomeActivity.class));
            finish();
        }

        //TODO Register Page
        String resister_str = "Register";
        String register_msg = context.getResources().getString(R.string.register_tv_message) + " " + resister_str;
        SpannableString text = new SpannableString(register_msg);
        ClickableSpan clickfor = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                startActivity(new Intent(context, RegisterActivity.class));
            }
        };

        text.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), (register_msg.length() - resister_str.length()), register_msg.length(), 0);
        text.setSpan(clickfor, (register_msg.length() - resister_str.length()), register_msg.length(), 0);
        register_tv.setText(text);
        register_tv.setMovementMethod(LinkMovementMethod.getInstance());
        register_tv.setText(text, TextView.BufferType.SPANNABLE);

        //TODO ForgotPassword Page
       /* String forgot_pass_str = context.getResources().getString(R.string.forgot_password);
        String forgot_pass_msg = context.getResources().getString(R.string.forgot_password);
        SpannableString spp_txt = new SpannableString(forgot_pass_msg);
        ClickableSpan clicksppn = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                str_email = et_email.getText().toString();
                if (validateEmail()) {
                    forgotPasswordRequest();
                }
            }
        };

        spp_txt.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), (forgot_pass_msg.length() - forgot_pass_str.length()), forgot_pass_msg.length(), 0);
        spp_txt.setSpan(clicksppn, (forgot_pass_msg.length() - forgot_pass_str.length()), forgot_pass_msg.length(), 0);
        tv_forgot_password.setText(spp_txt);
        tv_forgot_password.setMovementMethod(LinkMovementMethod.getInstance());
        tv_forgot_password.setText(spp_txt, TextView.BufferType.SPANNABLE);*/

        ArrayList<String> arrlist = new ArrayList<String>();
        arrlist.add("User");
        arrlist.add("Garage");

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrlist);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ut_usertype.setAdapter(aa);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_email = et_email.getText().toString();
                str_password = et_password.getText().toString();
                str_user_type = ut_usertype.getSelectedItem().toString();

                if (validateEmail() && validatePassword()) {
                    postOnServer();
                }
            }
        });

    }

    private void postOnServer() {

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String fcm_id = shareprefrance.getFCMkey(context);
        Log.e(TAG, " FCM Id is as follow as " + fcm_id);

        Retro.getInterface(context).loginUser(str_email, str_password, fcm_id, new Callback<RegisterResponse>() {
            @Override
            public void success(RegisterResponse registerResponse, Response response) {
                progressDialog.dismiss();

                //TODO Set SharedPreferance to login.
                Log.e(TAG, " Results " + registerResponse.getSuccess() + " user name " + registerResponse.getName());
                if (registerResponse.getSuccess().equals("success")) {
                    shareprefrance.loginUser(context, registerResponse.getName(), registerResponse.getEmail(), registerResponse.getMobile(), registerResponse.getUserid(), str_user_type, registerResponse.getAadhar_number(), registerResponse.getAccount_number(), true);
                    startActivity(new Intent(context, HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Log.e(TAG, " Error as " + error.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_ip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_update_IP:
                final EditText ip_address;
                Button update_bttn;
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_change_ip, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Update IP Address")
                        .setView(view);

                update_bttn = (Button) view.findViewById(R.id.update_bttn);
                ip_address = (EditText) view.findViewById(R.id.ip_address);

                shareprefrance = new Shareprefrance();

                String IP_address = shareprefrance.getServerURL(context);
                Log.e(TAG, " Server IP address as follow as " + IP_address);
                ip_address.setText(IP_address);

                dialog = builder.create();
                dialog.show();

                update_bttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (ip_address.getText().toString().trim().length() != 0) {
                            Log.e(TAG, " UPDATED IP values as " + ip_address.getText().toString());
                            shareprefrance.setServerURL(context, ip_address.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validateEmail() {
        String email = et_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            et_email.setError(getString(R.string.err_msg_email));
            requestFocus(et_email);
            return false;
        }

        return true;
    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().length() < 5) {
            et_password.setError(getString(R.string.err_msg_password));
            requestFocus(et_password);
            return false;
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_email:
                    validateEmail();
                    break;
                case R.id.et_password:
                    validatePassword();
                    break;
            }
        }
    }

}
