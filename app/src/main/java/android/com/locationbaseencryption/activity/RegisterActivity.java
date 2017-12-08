package android.com.locationbaseencryption.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.com.locationbaseencryption.Constant;
import android.com.locationbaseencryption.R;
import android.com.locationbaseencryption.retro.GenralResponse;
import android.com.locationbaseencryption.retro.Retro;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText et_name, et_email, et_password, et_mobile, et_address, et_dob, et_account_number, et_aadhar_number, et_account_balance;
    Button register_btn;
    String str_name, str_email, str_password, str_mobile, str_address, str_user_type = "";
    String str_dob, str_account_number, str_adhar_number, str_balance, str_gender;
    Spinner ut_usertype, sp_gender;
    ProgressDialog progressDialog;
    private Context context = RegisterActivity.this;
    private String TAG = RegisterActivity.class.getSimpleName();
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private String date_values = "";

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_btn = (Button) findViewById(R.id.register_btn);
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_address = (EditText) findViewById(R.id.et_address);
        et_dob = (EditText) findViewById(R.id.et_dob);
        et_account_number = (EditText) findViewById(R.id.et_account_number);
        et_aadhar_number = (EditText) findViewById(R.id.et_aadhar_number);
        et_account_balance = (EditText) findViewById(R.id.et_account_balance);
        ut_usertype = (Spinner) findViewById(R.id.ut_usertype);
        sp_gender = (Spinner) findViewById(R.id.sp_gender);

        ut_usertype.setVisibility(View.GONE);

        et_dob.setFocusable(false);
        et_dob.setFocusableInTouchMode(false);

        et_name.addTextChangedListener(new MyTextWatcher(et_name));
        et_password.addTextChangedListener(new MyTextWatcher(et_password));
        et_email.addTextChangedListener(new MyTextWatcher(et_email));
        et_mobile.addTextChangedListener(new MyTextWatcher(et_password));

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");


        calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        final Calendar calendars = Calendar.getInstance();
        calendars.set(2002, 1, 1);

        final Calendar calendarmin = Calendar.getInstance();
        calendarmin.set(1970, 1, 1);

        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                String str_month = "" + (datePicker.getMonth() + 1);
                String str_date = "" + datePicker.getDayOfMonth();

                if (datePicker.getDayOfMonth() < 10) {
                    str_date = "0" + datePicker.getDayOfMonth();
                }

                if ((datePicker.getMonth() + 1) < 10) {
                    str_month = "0" + (datePicker.getMonth() + 1);
                }

                date_values = datePicker.getYear() + "-" + str_month + "-" + str_date;

                String date_time = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(datePicker.getDrawingTime());

                et_dob.setText(date_values);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(calendars.getTime().getTime());
        datePickerDialog.getDatePicker().setMinDate(calendarmin.getTime().getTime());
        datePickerDialog.setTitle(null);

        et_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        ArrayList<String> arrlist = new ArrayList<String>();
        arrlist.add("User");
        arrlist.add("Garage");
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrlist);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ut_usertype.setAdapter(aa);

        ArrayList<String> gender_arrlist = new ArrayList<String>();
        gender_arrlist.add("Male");
        gender_arrlist.add("Female");
        ArrayAdapter gaa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gender_arrlist);
        gaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_gender.setAdapter(gaa);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmail() && validatePhone() && validateName() && validatePassword()) {

                    str_name = et_name.getText().toString();
                    str_email = et_email.getText().toString();
                    str_password = et_password.getText().toString();
                    str_mobile = et_mobile.getText().toString();
                    str_address = et_address.getText().toString();
                    str_account_number = et_account_number.getText().toString();
                    str_adhar_number = et_aadhar_number.getText().toString();
                    str_balance = et_account_balance.getText().toString();
                    str_dob = et_dob.getText().toString();
                    str_gender = sp_gender.getSelectedItem().toString();

                    Log.e(TAG, " Selected user type values is as " + str_user_type);
                    registerOnServer();

                }
            }
        });

    }

    private void registerOnServer() {
        progressDialog.show();
        Retro.getInterface(context).UserRegistration(str_name, str_email, str_mobile, str_password, str_address, str_gender, str_account_number
                , str_adhar_number, str_balance, str_dob, new Callback<GenralResponse>() {
                    @Override
                    public void success(GenralResponse genralResponse, Response response) {
                        progressDialog.dismiss();
                        if (genralResponse.equals("success")) {
                            startActivity(new Intent(context, UpdateGEOFenceActivity.class).putExtra(Constant.EMAIL, str_email).putExtra(Constant.ISUPDATE, false));
                            finish();
                        }
                        Toast.makeText(context, "" + genralResponse.getSuccess(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                    }
                });
    }

    private boolean validateEmail() {
        String email = et_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            et_email.setError(getString(R.string.err_msg_email));
            requestFocus(et_email);
            return false;
        } else {
            et_email.setError(null);
        }

        return true;

    }

    private boolean validateName() {

        if (et_name.getText().toString().trim().length() == 0) {
            et_name.setError(getString(R.string.err_msg_name));
            requestFocus(et_name);
            return false;
        } else {
            et_name.setError(null);
        }

        return true;
    }

    private boolean validatePhone() {

        String str_mobile = et_mobile.getText().toString().trim();

        if (str_mobile.isEmpty()) {
            et_mobile.setError(getString(R.string.err_msg_mobile));
            requestFocus(et_mobile);
            return false;
        } else {
            et_mobile.setError(null);
        }

        return true;
    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            et_password.setError(getString(R.string.err_msg_password));
            requestFocus(et_password);
            return false;
        } else {
            et_password.setError(null);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.et_name:
                    validateName();
                    break;
                case R.id.et_mobile:
                    validatePhone();
                    break;
            }
        }
    }

}
