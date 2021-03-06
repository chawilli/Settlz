package devtech.settlz;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;
import retrofit2.http.Url;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "5RDt1TaGoPIoZQ2bpNRXeiyEi";
    private static final String TWITTER_SECRET = "wTLAD6GO1WSyrRGMofLIYK3Nq0wbmLjJTO6rDGpu4CJA4EZXeV";

    private FrameLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        contentLayout = (FrameLayout) findViewById(R.id.content_frame);
        contentLayout.setOnClickListener(this);
        Fragment fragment = new PollFragment();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(contentLayout.getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_create) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            ;
            boolean login = pref.getBoolean("login", false);
            if (login) {
                Fragment fragment = new CreateFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            } else {
                Fragment fragment = new LoginFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
                Toast toast = Toast.makeText(this.getApplicationContext(), "Please Login/Register", Toast.LENGTH_LONG);
                toast.show();
            }
        } else if (id == R.id.nav_featured) {
            Fragment fragment = new PollFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_search) {
            Fragment fragment = new SearchFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_login) {
            Fragment fragment = new LoginFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_register) {
            Fragment fragment = new RegisterFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_profile) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            ;
            boolean login = pref.getBoolean("login", false);
            if (login) {
                Fragment fragment = new ProfileFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            } else {
                Fragment fragment = new LoginFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
                Toast toast = Toast.makeText(this.getApplicationContext(), "Please Login/Register", Toast.LENGTH_LONG);
                toast.show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == contentLayout.getId()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static class ProfileFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        Button logoutButton;
        Button changeButton;
        Button subscribeButton;
        Button changeQuestionButton;
        Button facebookLogoutButton;
        EditText passwordEditText;
        EditText verifyEditText;
        EditText changedEditText;
        TextView emailTextView;


        public ProfileFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            connectionClass = new Database();

            passwordEditText = (EditText) rootView.findViewById(R.id.passwordEditText);
            verifyEditText = (EditText) rootView.findViewById(R.id.verifyEditText);
            changedEditText = (EditText) rootView.findViewById(R.id.changedEditText);
            logoutButton = (Button) rootView.findViewById(R.id.logoutButton);
            logoutButton.setOnClickListener(this);
            changeButton = (Button) rootView.findViewById(R.id.changeButton);
            changeButton.setOnClickListener(this);
            subscribeButton = (Button) rootView.findViewById(R.id.subscribedPollButton);
            subscribeButton.setOnClickListener(this);
            facebookLogoutButton = (Button) rootView.findViewById(R.id.facebookLogOutButton);
            facebookLogoutButton.setOnClickListener(this);
            changeQuestionButton = (Button) rootView.findViewById(R.id.changeQuestionButton);
            changeQuestionButton.setOnClickListener(this);

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            emailTextView = (TextView) rootView.findViewById(R.id.emailTextView);
            emailTextView.setText(pref.getString("email", ""));


            return rootView;
        }

        public void logout() {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("id");
            editor.remove("email");
            editor.remove("password");
            editor.putBoolean("login", false);
            editor.commit();
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "You have been logged out", Toast.LENGTH_LONG);
            toast.show();
            Fragment fragment = new PollFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == facebookLogoutButton.getId()) {
                FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
                LoginManager.getInstance().logOut();
            }
            if (v.getId() == logoutButton.getId()) {
                this.logout();
            }
            if (v.getId() == changeButton.getId()) {
                this.changePassword();
            }
            if (v.getId() == changeQuestionButton.getId()) {
                Fragment fragment = new QuestionFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
            if (v.getId() == subscribeButton.getId()) {
                Fragment fragment = new SubscribeFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }

        }

        public void changePassword() {
            String password = passwordEditText.getText().toString();
            String changedPassword = changedEditText.getText().toString();
            String verifyPassword = verifyEditText.getText().toString();
            if (!password.isEmpty() && password.length() >= 6 && !changedPassword.isEmpty() && changedPassword.length() >= 6 && !verifyPassword.isEmpty() && verifyPassword.length() >= 6 && verifyPassword.equals(changedPassword)) {

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (password.equals(pref.getString("password", ""))) {
                    int id = pref.getInt("id", -1);
                    connectionClass.changePassword(id, changedPassword);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("password", changedPassword);
                    editor.commit();
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Password changed", Toast.LENGTH_LONG);
                    toast.show();
                    Fragment fragment = new ProfileFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_LONG);
                    toast.show();
                }


            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public static class LoginFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        EditText emailEditText;
        EditText passwordEditText;
        Button registerButton;
        Button loginButton;
        Button forgotButton;

        public LoginFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            connectionClass = new Database();
            emailEditText = (EditText) rootView.findViewById(R.id.emailEditText);
            passwordEditText = (EditText) rootView.findViewById(R.id.passwordEditText);
            registerButton = (Button) rootView.findViewById(R.id.registerButton);
            registerButton.setOnClickListener(this);
            loginButton = (Button) rootView.findViewById(R.id.loginButton);
            loginButton.setOnClickListener(this);
            forgotButton = (Button) rootView.findViewById(R.id.forgotButton);
            forgotButton.setOnClickListener(this);
            return rootView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == registerButton.getId()) {
                this.register();
            } else if (v.getId() == loginButton.getId()) {
                this.login();
            } else if (v.getId() == forgotButton.getId()) {
                Fragment fragment = new PasswordFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }

        public void login() {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            connectionClass.login(email, password);
            int id = connectionClass.login(email, password);
            if (connectionClass.banned(email, password)) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "User is banned", Toast.LENGTH_LONG);
                toast.show();
            } else if (!password.isEmpty() && id != -1 && !email.isEmpty()) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.putBoolean("login", true);
                editor.putInt("id", id);
                editor.commit();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Thank you for logging in", Toast.LENGTH_LONG);
                toast.show();

                if (pref.getInt("subscribeLogin", -1) != -1) {
                    editor.putInt("pollid", pref.getInt("subscribeLogin", 1));
                    editor.remove("subscribeLogin");
                    editor.commit();
                    Fragment fragment = new PollFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Fragment fragment = new ProfileFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }

            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_LONG);
                toast.show();
            }

        }

        public void register() {
            Fragment fragment = new RegisterFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public static class RegisterFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        EditText emailEditText;
        EditText passwordEditText;
        EditText verifyEditText;
        Button registerButton;
        Spinner spinnerQuestions;
        List<String> questions = new ArrayList<String>();
        EditText answer;

        public RegisterFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_register, container, false);
            connectionClass = new Database();
            emailEditText = (EditText) rootView.findViewById(R.id.emailEditText);
            emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            });
            answer = (EditText) rootView.findViewById(R.id.answerEditText);
            passwordEditText = (EditText) rootView.findViewById(R.id.passwordEditText);
            verifyEditText = (EditText) rootView.findViewById(R.id.verifyEditText);
            registerButton = (Button) rootView.findViewById(R.id.registerButton);
            registerButton.setOnClickListener(this);

            getQuestions();
            spinnerQuestions = (Spinner) rootView.findViewById(R.id.spinnerQuestions);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, questions);
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinnerQuestions.setAdapter(dataAdapter);
            return rootView;
        }

        public void getQuestions() {

            ResultSet rs = connectionClass.getQuestions();
            try {
                while (rs.next()) {
                    questions.add(rs.getString("securityQuestion"));
                }
                ;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onClick(View v) {
            if (v.getId() == registerButton.getId()) {
                this.register();
            }
        }

        public void register() {
            String question = spinnerQuestions.getSelectedItem().toString();
            int questionId = -1;
            questionId = connectionClass.getQuestionId(question);
            String answerString = answer.getText().toString().toLowerCase();
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();
            boolean emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if (password.equals(verifyEditText.getText().toString()) && !password.isEmpty() && password.length() >= 6 && emailValid && !answerString.isEmpty()) {

                if (connectionClass.verifyEmail(email)) {

                    int id = connectionClass.register(email, password, questionId, answerString);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.putBoolean("login", true);
                    editor.putInt("id", id);
                    editor.commit();

                    Fragment fragment = new ProfileFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Thank you for registering", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Email already in use", Toast.LENGTH_LONG);
                    toast.show();
                }

            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public static class CreateFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        Button expiryButton;
        Button createButton;
        static EditText expiryEditText;
        EditText argumentEditText;
        EditText option1EditText;
        EditText option2EditText;
        EditText option3EditText;
        EditText option4EditText;
        Spinner spinnerCategory;
        final List<String> categories = new ArrayList<String>();
        static Date nextDate;
        static Date expiryDate;
        TextView option3TextView;
        TextView option4TextView;

        public CreateFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create, container, false);
            connectionClass = new Database();

            argumentEditText = (EditText) rootView.findViewById(R.id.argumentEditText);
            option1EditText = (EditText) rootView.findViewById(R.id.option1EditText);
            option2EditText = (EditText) rootView.findViewById(R.id.option2EditText);
            option3EditText = (EditText) rootView.findViewById(R.id.option3EditText);
            option3TextView = (TextView) rootView.findViewById(R.id.option3TextView);
            option3TextView.setVisibility(View.GONE);
            option3EditText.setVisibility(View.GONE);
            option4EditText = (EditText) rootView.findViewById(R.id.option4EditText);
            option4TextView = (TextView) rootView.findViewById(R.id.option4TextView);
            option4TextView.setVisibility(View.GONE);
            option4EditText.setVisibility(View.GONE);
            option2EditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        option3TextView.setVisibility(View.VISIBLE);
                        option3EditText.setVisibility(View.VISIBLE);
                    } else if (s.length() == 0) {
                        option3TextView.setVisibility(View.GONE);
                        option4TextView.setVisibility(View.GONE);
                        option3EditText.setText("");
                        option4EditText.setText("");
                        option3EditText.setVisibility(View.GONE);
                        option4EditText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            option3EditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        option4TextView.setVisibility(View.VISIBLE);
                        option4EditText.setVisibility(View.VISIBLE);
                    } else if (s.length() == 0) {
                        option4TextView.setVisibility(View.GONE);
                        option4EditText.setText("");
                        option4EditText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            expiryButton = (Button) rootView.findViewById(R.id.expiryButton);
            expiryButton.setOnClickListener(this);

            expiryEditText = (EditText) rootView.findViewById(R.id.expiryEditText);
            expiryEditText.setInputType(InputType.TYPE_NULL);

            getCategories();
            spinnerCategory = (Spinner) rootView.findViewById(R.id.spinnerCategory);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, categories);
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinnerCategory.setAdapter(dataAdapter);

            createButton = (Button) rootView.findViewById(R.id.createButton);
            createButton.setOnClickListener(this);
            return rootView;
        }

        public static class DatePickerFragment extends DialogFragment
                implements DatePickerDialog.OnDateSetListener {


            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH) + 1;

                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                nextDate = c.getTime();

                return new DatePickerDialog(getActivity(), this, year, month, day);
            }

            public void onDateSet(DatePicker view, int year, int month, int day) {

                Calendar d = Calendar.getInstance();
                d.set(Calendar.YEAR, year);
                d.set(Calendar.MONTH, month);
                d.set(Calendar.DAY_OF_MONTH, day);
                d.set(Calendar.HOUR_OF_DAY, 0);
                d.set(Calendar.MINUTE, 0);
                d.set(Calendar.SECOND, 0);
                d.set(Calendar.MILLISECOND, 0);
                expiryDate = d.getTime();

                if (nextDate.after(expiryDate)) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    month += 1;
                    expiryEditText.setText(year + "-" + month + "-" + day);
                }
            }
        }

        public void getCategories() {
            ResultSet rs = connectionClass.getCategories();
            try {
                while (rs.next()) {
                    categories.add(rs.getString("CategoryName"));
                }
                ;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == expiryButton.getId()) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            } else if (v.getId() == expiryEditText.getId()) {

            } else if (v.getId() == createButton.getId()) {
                String argument = argumentEditText.getText().toString();
                String option1 = option1EditText.getText().toString();
                String option2 = option2EditText.getText().toString();
                String option3 = option3EditText.getText().toString();
                String option4 = option4EditText.getText().toString();
                String categoryName = spinnerCategory.getSelectedItem().toString();
                int categoryId = -1;
                categoryId = connectionClass.getCategoryId(categoryName);

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (argument.isEmpty() || option1.isEmpty() || option2.isEmpty() || expiryEditText.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Cannot make poll. Fill in the fields!", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    int pollId = connectionClass.create(argument, option1, option2, option3, option4, expiryEditText.getText().toString(), categoryId, pref.getInt("id", 0));

                    if (pollId == -1) {
                        Log.d("ERRORR", "PROBLEMS");
                    } else {
                        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("pollid", pollId);
                        Fragment fragment = new PollFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        editor.commit();
                    }
                }

            }
        }
    }

    public static class PollFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        TextView pollTextView;
        RadioButton option1RadioButton;
        RadioButton option2RadioButton;
        RadioButton option3RadioButton;
        RadioButton option4RadioButton;
        TextView category;
        TextView expire;
        Button nextButton;
        Button voteButton;
        RadioGroup optionsRadioGroup;
        CheckBox subscribeCheckBox;
        Button reportButton;
        ImageButton facebookButton;
        ImageButton twitterButton;
        LinearLayout layout;
        PieChart chart;
        Calendar c;
        SimpleDateFormat df;
        String currentDate;
        CallbackManager callbackManager;
        ShareDialog shareDialog;
        int id;
        int pollId;
        int userId;
        String email;
        String password;
        Boolean login;
        Boolean checked;
        int facebookId;
        int twitterId;

        public PollFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.content_main, container, false);

            FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
            callbackManager = CallbackManager.Factory.create();
            shareDialog = new ShareDialog(this);
            facebookButton = (ImageButton) rootView.findViewById(R.id.facebookButton);
            facebookButton.setOnClickListener(this);
            twitterButton = (ImageButton) rootView.findViewById(R.id.twitterButton);
            twitterButton.setOnClickListener(this);

            connectionClass = new Database();

            c = Calendar.getInstance();
            df = new SimpleDateFormat("yyyy-MM-dd");
            currentDate = df.format(c.getTime());

            pollTextView = (TextView) rootView.findViewById(R.id.pollTextView);
            option1RadioButton = (RadioButton) rootView.findViewById(R.id.option1RadioButton);
            option2RadioButton = (RadioButton) rootView.findViewById(R.id.option2RadioButton);
            option3RadioButton = (RadioButton) rootView.findViewById(R.id.option3RadioButton);
            option4RadioButton = (RadioButton) rootView.findViewById(R.id.option4RadioButton);
            category = (TextView) rootView.findViewById(R.id.categoryTextView);
            expire = (TextView) rootView.findViewById(R.id.expiredTextView);
            nextButton = (Button) rootView.findViewById(R.id.nextButton);
            nextButton.setOnClickListener(this);
            voteButton = (Button) rootView.findViewById(R.id.voteButton);
            voteButton.setOnClickListener(this);
            optionsRadioGroup = (RadioGroup) rootView.findViewById(R.id.optionsRadioGroup);
            subscribeCheckBox = (CheckBox) rootView.findViewById(R.id.subscribeCheckBox);
            subscribeCheckBox.setOnClickListener(this);
            layout = (LinearLayout) rootView.findViewById(R.id.linearLayout);
            chart = new PieChart(getActivity().getApplicationContext());
            reportButton = (Button) rootView.findViewById(R.id.reportButton);
            reportButton = (Button) rootView.findViewById(R.id.reportButton);
            reportButton.setOnClickListener(this);

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = pref.edit();

            login = pref.getBoolean("login", false);
            email = pref.getString("email", "");
            password = pref.getString("password", "");
            userId = connectionClass.login(email, password);

            Intent intent;
            intent = getActivity().getIntent();
            pollId = pref.getInt("pollid", 0);
            if (pollId != 0) {
                subscribedPoll(pollId);
                checkSubscribe();
                editor.remove("pollid");
                editor.commit();
            } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                Uri uri = intent.getData();
                id = Integer.valueOf(uri.getQueryParameter("id"));
                subscribedPoll(id);
            } else {
                random();
            }


            return rootView;
        }

        private void subscribedPoll(int pollId) {
            //Load a specific poll
            ResultSet rs = connectionClass.subscribedPoll(pollId);
            try {
                rs.next();
                id = rs.getInt("PollId");
                pollTextView.setText(rs.getString("Argument"));
                option1RadioButton.setText(rs.getString("Option1"));
                option2RadioButton.setText(rs.getString("Option2"));
                if (rs.getString("Option3").equals("")) {
                    option3RadioButton.setVisibility(View.GONE);
                }
                if (rs.getString("Option4").equals("")) {
                    option4RadioButton.setVisibility(View.GONE);
                }
                option3RadioButton.setText(rs.getString("Option3"));
                option4RadioButton.setText(rs.getString("Option4"));
                category.setText(rs.getString("CategoryName"));
                expire.setText(rs.getDate("ExpiryDate").toString());
                facebookId = rs.getInt("Facebook_FacebookId");
                twitterId = rs.getInt("Twitter_TwitterId");
                c = Calendar.getInstance();
                df = new SimpleDateFormat("yyyy-MM-dd");
                c.setTime(df.parse(currentDate));
                Date today = c.getTime();

                Calendar e = Calendar.getInstance();
                e.setTime(df.parse(rs.getDate("ExpiryDate").toString()));
                Date expiryDate = e.getTime();

                if (today.after(expiryDate) || today.equals(expiryDate)) {
                    result(-1);
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Poll was settled", Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void random() {
            ResultSet rs = connectionClass.randomPoll(currentDate);
            try {
                rs.next();
                id = rs.getInt("PollId");
                pollTextView.setText(rs.getString("Argument"));
                option1RadioButton.setText(rs.getString("Option1"));
                option2RadioButton.setText(rs.getString("Option2"));
                if (rs.getString("Option3").equals("")) {
                    option3RadioButton.setVisibility(View.GONE);
                }
                if (rs.getString("Option4").equals("")) {
                    option4RadioButton.setVisibility(View.GONE);
                }
                option3RadioButton.setText(rs.getString("Option3"));
                option4RadioButton.setText(rs.getString("Option4"));
                category.setText(rs.getString("CategoryName"));
                expire.setText(rs.getDate("ExpiryDate").toString());
                facebookId = rs.getInt("Facebook_FacebookId");
                twitterId = rs.getInt("Twitter_TwitterId");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            checkSubscribe();
        }

        public void next() {
            ResultSet rs = connectionClass.nextPoll(id, currentDate);
            if (option3RadioButton.getVisibility() == View.GONE) {
                option3RadioButton.setVisibility(View.VISIBLE);
            }
            if (option4RadioButton.getVisibility() == View.GONE) {
                option4RadioButton.setVisibility(View.VISIBLE);
            }
            try {
                rs.next();
                id = rs.getInt("PollId");
                pollTextView.setText(rs.getString("Argument"));
                option1RadioButton.setText(rs.getString("Option1"));
                option2RadioButton.setText(rs.getString("Option2"));
                if (rs.getString("Option3").equals("")) {
                    option3RadioButton.setVisibility(View.GONE);
                }
                if (rs.getString("Option4").equals("")) {
                    option4RadioButton.setVisibility(View.GONE);
                }
                option3RadioButton.setText(rs.getString("Option3"));
                option4RadioButton.setText(rs.getString("Option4"));
                category.setText(rs.getString("CategoryName"));
                expire.setText(rs.getDate("ExpiryDate").toString());
                facebookId = rs.getInt("Facebook_FacebookId");
                twitterId = rs.getInt("Twitter_TwitterId");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            optionsRadioGroup.clearCheck();
            checkSubscribe();
            if (optionsRadioGroup.getVisibility() == View.GONE) {
                optionsRadioGroup.setVisibility(View.VISIBLE);
                voteButton.setVisibility(View.VISIBLE);
                subscribeCheckBox.setVisibility(View.VISIBLE);
                reportButton.setVisibility(View.VISIBLE);
                layout.removeViewAt(6);
            }
        }

        //method for voteButton
        public void vote() {
            int selected = 0;
            if (option1RadioButton.isChecked()) {
                selected = 1;
            } else if (option2RadioButton.isChecked()) {
                selected = 2;
            } else if (option3RadioButton.isChecked()) {
                selected = 3;
            } else if (option4RadioButton.isChecked()) {
                selected = 4;
            }

            if (selected != 0) {
                result(selected);
                option1RadioButton.setChecked(false);
                option2RadioButton.setChecked(false);
                option3RadioButton.setChecked(false);
                option4RadioButton.setChecked(false);
            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "You have to select an option", Toast.LENGTH_LONG);
                toast.show();
            }
        }

        public void checkSubscribe() {
            if (connectionClass.checkPollUser(id, userId)) {
                subscribeCheckBox.setChecked(true);
            } else {
                subscribeCheckBox.setChecked(false);
            }
        }

        public void result(int selected) {
            PieChart chart = new PieChart(getActivity().getApplicationContext());

            try {
                ResultSet rs = connectionClass.vote(id, selected);
                rs.next();
                ArrayList<String> labels = new ArrayList<String>();
                ArrayList<Entry> entries = new ArrayList<Entry>();
                if (rs.getInt("Vote1") != 0) {
                    labels.add(rs.getString("Option1"));
                    entries.add(new Entry((float) rs.getInt("Vote1"), rs.getInt("Vote1")));
                }
                if (rs.getInt("Vote2") != 0) {
                    labels.add(rs.getString("Option2"));
                    entries.add(new Entry((float) rs.getInt("Vote2"), rs.getInt("Vote2")));
                }
                if (option3RadioButton.getVisibility() == View.VISIBLE && rs.getInt("Vote3") != 0) {
                    labels.add(rs.getString("Option3"));
                    entries.add(new Entry((float) rs.getInt("Vote3"), rs.getInt("Vote3")));
                }
                if (option4RadioButton.getVisibility() == View.VISIBLE && rs.getInt("Vote4") != 0) {
                    labels.add(rs.getString("Option4"));
                    entries.add(new Entry((float) rs.getInt("Vote4"), rs.getInt("Vote4")));
                }
                PieDataSet dataset = new PieDataSet(entries, "");

                dataset.setColors(new int[]{Color.parseColor("#463BFF"), Color.parseColor("#8F2DFF"), Color.parseColor("#2C97FF"), Color.parseColor("#6B62FF")});

                dataset.setValueTextSize(16);


                PieData data = new PieData(labels, dataset);
                chart.setData(data);
                chart.setDescription("Results");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            optionsRadioGroup.setVisibility(View.GONE);
            voteButton.setVisibility(View.GONE);
            reportButton.setVisibility(View.GONE);
            chart.getLegend().setEnabled(false);
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = (int) (metrics.heightPixels * 0.9);
            int width = (int) (metrics.widthPixels * 0.9);
            layout.addView(chart, height, width); // add the programmatically created chart
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == voteButton.getId()) {
                vote();
            } else if (v.getId() == facebookButton.getId()) {
                connectionClass.updateShareCount(facebookId);
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("http://settlz.com/view?id=" + id))
                        .build();
                shareDialog.show(content);
            } else if (v.getId() == twitterButton.getId()) {
                connectionClass.updateTwitterCount(twitterId);
                TwitterAuthConfig authConfig = new TwitterAuthConfig("consumerKey", "consumerSecret");
                Fabric.with(getActivity(), new TweetComposer(), new Twitter(authConfig));
                TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                        .text("http://settlz.com/view?id=" + id);
                builder.show();
            } else if (v.getId() == nextButton.getId()) {
                if (option1RadioButton.isChecked() || option2RadioButton.isChecked() || option3RadioButton.isChecked() || option4RadioButton.isChecked()) {
                    vote();
                } else {
                    next();
                }

            } else if (v.getId() == reportButton.getId()) {
                connectionClass.reportPoll(id);
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Thank you for reporting the malicious poll!", Toast.LENGTH_LONG);
                toast.show();
                next();
            } else if (v.getId() == subscribeCheckBox.getId()) {
                if (subscribeCheckBox.isChecked()) {
                    if (login) {
                        connectionClass.setSubscribePoll(id, userId);
                        checked = connectionClass.checkPollUser(id, userId);
                    } else {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("subscribeLogin", id);
                        editor.commit();
                        subscribeCheckBox.setChecked(false);
                        Fragment fragment = new LoginFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Please Login/Register", Toast.LENGTH_LONG);
                        toast.show();
                    }
                } else {
                    connectionClass.deleteSubscribedPoll(id, userId);
                }
            }
        }
    }

    public static class SearchFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        EditText argumentEditTextView;
        Button searchButton;
        Spinner spinnerCategory;
        LinearLayout searchResultsLayout;
        ArrayList<Button> buttonList;
        final List<String> categories = new ArrayList<String>();

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search, container, false);
            connectionClass = new Database();
            searchResultsLayout = (LinearLayout) rootView.findViewById(R.id.searchResultsLayout);
            buttonList = new ArrayList<Button>();
            argumentEditTextView = (EditText) rootView.findViewById(R.id.argumentEditText);
            searchButton = (Button) rootView.findViewById(R.id.searchButton);
            searchButton.setOnClickListener(this);
            getCategories();
            spinnerCategory = (Spinner) rootView.findViewById(R.id.spinnerCategory);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, categories);

            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinnerCategory.setAdapter(dataAdapter);

            return rootView;
        }

        public void getCategories() {
            ResultSet rs = connectionClass.getCategories();
            categories.add("All");
            try {
                while (rs.next()) {
                    categories.add(rs.getString("CategoryName"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public SearchFragment() {

        }

        @Override
        public void onClick(View view) {
            for (int i = 0; i < buttonList.size(); i++) {
                if (view.getId() == buttonList.get(i).getId()) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("pollid", buttonList.get(i).getId());
                    Fragment fragment = new PollFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    editor.commit();
                }
            }

            if (view.getId() == searchButton.getId()) {
                String categoryName = spinnerCategory.getSelectedItem().toString();
                int categoryId = -1;
                if (!categoryName.equals("All")) {
                    categoryId = connectionClass.getCategoryId(categoryName);
                }


                ResultSet rs = connectionClass.getSearchResults(argumentEditTextView.getText().toString(), categoryId);
                try {
                    if (searchResultsLayout.getChildCount() > 0) {
                        searchResultsLayout.removeAllViews();
                    }

                    while (rs.next()) {
                        Button button = new Button(getActivity());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 10);
                        button.setLayoutParams(params);
                        button.setText(rs.getString("Argument"));
                        button.setId(rs.getInt("PollId"));
                        button.setOnClickListener(this);
                        buttonList.add(button);
                        searchResultsLayout.addView(button);
                    }
                    if (searchResultsLayout.getChildCount() == 0) {
                        TextView tx = new TextView(getActivity());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 10);
                        tx.setLayoutParams(params);
                        tx.setText("No Results");
                        searchResultsLayout.addView(tx);

                    }
                } catch (SQLException e) {
                    Log.d("SQLPROBLEM", e.toString());

                }
            }

        }
    }

    public static class SubscribeFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        ArrayList<Button> buttonList;
        LinearLayout subscribeLayout;
        Spinner spinnerSubscribe;
        Button updateButton;
        private String[] showPolls = new String[]{"Show 10 Polls", "Show 25 Polls", "Show 50 Polls", "Show All Polls"};
        private int top = 10;

        public SubscribeFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_subscribe, container, false);
            buttonList = new ArrayList<Button>();
            subscribeLayout = (LinearLayout) rootView.findViewById(R.id.subscribedResultsLayout);
            connectionClass = new Database();
            updateButton = (Button) rootView.findViewById(R.id.updateButton);
            updateButton.setOnClickListener(this);

            spinnerSubscribe = (Spinner) rootView.findViewById(R.id.spinnerSubscribe);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, showPolls);
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinnerSubscribe.setAdapter(dataAdapter);

            populateSubscriptions(top);

            return rootView;

        }

        public void populateSubscriptions(int top) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (pref.getInt("id", -1) != -1) {
                ResultSet rs = connectionClass.getSubscribedPolls(pref.getInt("id", -1), top);
                try {
                    while (rs.next()) {
                        Button button = new Button(getActivity());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 10);
                        button.setLayoutParams(params);
                        button.setText(rs.getString("Argument"));
                        button.setId(rs.getInt("PollId"));
                        button.setOnClickListener(this);
                        buttonList.add(button);
                        subscribeLayout.addView(button);
                    }
                } catch (SQLException e) {
                    Log.d("SQLPROBLEM", e.toString());
                }
            }
        }

        @Override
        public void onClick(View view) {
            for (int i = 0; i < buttonList.size(); i++) {
                if (view.getId() == buttonList.get(i).getId()) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("pollid", buttonList.get(i).getId());
                    Fragment fragment = new PollFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    editor.commit();
                }
            }
            if (view.getId() == updateButton.getId()) {
                if (spinnerSubscribe.getSelectedItemPosition() == 0) {
                    top = 10;
                } else if (spinnerSubscribe.getSelectedItemPosition() == 1) {
                    top = 25;
                } else if (spinnerSubscribe.getSelectedItemPosition() == 2) {
                    top = 50;
                } else {
                    top = 1000;
                }
                subscribeLayout.removeAllViews();
                buttonList.clear();
                populateSubscriptions(top);
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Updated", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    public static class PasswordFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        TextView emailTextView;
        EditText usernameEditText;
        Button verifyEmailButton;
        TextView securityQuestionTextView;
        EditText answerEditText;
        Button answerButton;
        TextView passwordTextView;
        EditText passwordEditText;
        TextView verifyTextView;
        EditText verifyEditText;
        Button changeButton;

        public PasswordFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_password, container, false);
            connectionClass = new Database();
            emailTextView = (TextView) rootView.findViewById(R.id.emailTextView);
            usernameEditText = (EditText) rootView.findViewById(R.id.usernameEditText);
            verifyEmailButton = (Button) rootView.findViewById(R.id.verifyEmailButton);
            verifyEmailButton.setOnClickListener(this);
            securityQuestionTextView = (TextView) rootView.findViewById(R.id.securityQuestionTextView);
            securityQuestionTextView.setVisibility(View.GONE);
            answerEditText = (EditText) rootView.findViewById(R.id.answerEditText);
            answerEditText.setVisibility(View.GONE);
            answerButton = (Button) rootView.findViewById(R.id.answerButton);
            answerButton.setOnClickListener(this);
            answerButton.setVisibility(View.GONE);
            passwordTextView = (TextView) rootView.findViewById(R.id.passwordTextView);
            passwordTextView.setVisibility(View.GONE);
            passwordEditText = (EditText) rootView.findViewById(R.id.passwordEditText);
            passwordEditText.setVisibility(View.GONE);
            verifyTextView = (TextView) rootView.findViewById(R.id.verifyTextView);
            verifyTextView.setVisibility(View.GONE);
            verifyEditText = (EditText) rootView.findViewById(R.id.verifyEditText);
            verifyEditText.setVisibility(View.GONE);
            changeButton = (Button) rootView.findViewById(R.id.changeButton);
            changeButton.setVisibility(View.GONE);
            changeButton.setOnClickListener(this);
            return rootView;

        }

        @Override
        public void onClick(View v) {
            String email = usernameEditText.getText().toString();
            if (v.getId() == verifyEmailButton.getId()) {
                if (!connectionClass.verifyEmail(email)) {
                    emailTextView.setVisibility(View.GONE);
                    usernameEditText.setVisibility(View.GONE);
                    verifyEmailButton.setVisibility(View.GONE);
                    String question = connectionClass.getQuestion(email);
                    securityQuestionTextView.setText(question);
                    securityQuestionTextView.setVisibility(View.VISIBLE);
                    answerEditText.setVisibility(View.VISIBLE);
                    answerButton.setVisibility(View.VISIBLE);
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Email is incorrect or does not exist.", Toast.LENGTH_LONG);
                    toast.show();
                }


            }
            if (v.getId() == answerButton.getId()) {
                if (connectionClass.verifyAnswer(email, answerEditText.getText().toString())) {
                    securityQuestionTextView.setText("");
                    securityQuestionTextView.setVisibility(View.GONE);
                    answerEditText.setVisibility(View.GONE);
                    answerButton.setVisibility(View.GONE);
                    passwordTextView.setVisibility(View.VISIBLE);
                    passwordEditText.setVisibility(View.VISIBLE);
                    verifyTextView.setVisibility(View.VISIBLE);
                    verifyEditText.setVisibility(View.VISIBLE);
                    changeButton.setVisibility(View.VISIBLE);
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Incorrect Answer.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            if (v.getId() == changeButton.getId()) {
                String changedPassword = passwordEditText.getText().toString();
                String verifyPassword = verifyEditText.getText().toString();
                if (!changedPassword.isEmpty() && changedPassword.length() >= 6 && !verifyPassword.isEmpty() && verifyPassword.length() >= 6 && verifyPassword.equals(changedPassword)) {

                    connectionClass.forgotPassword(email, changedPassword);
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Password changed", Toast.LENGTH_LONG);
                    toast.show();
                    passwordTextView.setVisibility(View.GONE);
                    passwordEditText.setVisibility(View.GONE);
                    verifyTextView.setVisibility(View.GONE);
                    verifyEditText.setVisibility(View.GONE);
                    changeButton.setVisibility(View.GONE);
                    emailTextView.setVisibility(View.VISIBLE);
                    usernameEditText.setVisibility(View.VISIBLE);
                    verifyEmailButton.setVisibility(View.VISIBLE);
                    Fragment fragment = new LoginFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid Information. Make sure password match and are at least 6 characters.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    public static class QuestionFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        Spinner spinnerQuestions;
        Button changeButton;
        EditText answer;
        List<String> questions = new ArrayList<String>();

        public QuestionFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_question, container, false);
            connectionClass = new Database();
            changeButton = (Button) rootView.findViewById(R.id.changeQuestionButton);
            changeButton.setOnClickListener(this);
            answer = (EditText) rootView.findViewById(R.id.answerEditText);
            getQuestions();
            spinnerQuestions = (Spinner) rootView.findViewById(R.id.spinnerQuestions);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, questions);
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinnerQuestions.setAdapter(dataAdapter);
            return rootView;

        }

        public void getQuestions() {

            ResultSet rs = connectionClass.getQuestions();
            try {
                while (rs.next()) {
                    questions.add(rs.getString("securityQuestion"));
                }
                ;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == changeButton.getId()) {
                if (!answer.getText().toString().isEmpty()) {
                    String question = spinnerQuestions.getSelectedItem().toString();
                    int questionId = -1;
                    questionId = connectionClass.getQuestionId(question);
                    if (questionId != -1) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        connectionClass.changeQuestion(pref.getInt("id", -1), questionId, answer.getText().toString());

                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Secrets changed", Toast.LENGTH_LONG);
                        toast.show();
                        Fragment fragment = new ProfileFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_LONG);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }
}