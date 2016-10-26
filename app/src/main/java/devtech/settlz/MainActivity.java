package devtech.settlz;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_share){

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_create) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            ;
            boolean login = pref.getBoolean("login", false);
            //user logged in
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
                Toast toast = Toast.makeText(this.getApplicationContext(), "Please Login/Register", Toast.LENGTH_SHORT);
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
        } else if (id == R.id.nav_settings) {

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
            //user logged in
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
                Toast toast = Toast.makeText(this.getApplicationContext(), "Please Login/Register", Toast.LENGTH_SHORT);
                toast.show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class ProfileFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        Button logoutButton;
        Button changeButton;
        Button subscribeButton;
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
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "You have been logged out", Toast.LENGTH_SHORT);
            toast.show();
            Fragment fragment = new PollFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == logoutButton.getId()) {
                this.logout();
            }
            if (v.getId() == changeButton.getId()) {
                this.changePassword();
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
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Password changed", Toast.LENGTH_SHORT);
                    toast.show();
                    Fragment fragment = new ProfileFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_SHORT);
                    toast.show();
                }


            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_SHORT);
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
        Button backButton;
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
            backButton = (Button) rootView.findViewById(R.id.backButton);
            backButton.setOnClickListener(this);
            forgotButton = (Button) rootView.findViewById(R.id.forgotButton);
            forgotButton.setOnClickListener(this);
            return rootView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == backButton.getId()) {
                this.back();
            } else if (v.getId() == registerButton.getId()) {
                this.register();
            } else if (v.getId() == loginButton.getId()) {
                this.login();
            } else if (v.getId() == forgotButton.getId()) {

            }
        }

        public void login() {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            connectionClass.login(email, password);
            int id = connectionClass.login(email, password);

            if (!password.isEmpty() && id != -1 && !email.isEmpty()) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.putBoolean("login", true);
                editor.putInt("id", id);
                editor.commit();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Thank you for logging in", Toast.LENGTH_SHORT);
                toast.show();
                Fragment fragment = new ProfileFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_SHORT);
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

        public void back() {
            Fragment fragment = new PollFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public static class RegisterFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        //        Statement stmt = null;
        EditText emailEditText;
        EditText passwordEditText;
        EditText verifyEditText;
        Button backButton;
        Button registerButton;

        public RegisterFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_register, container, false);
            connectionClass = new Database();
            emailEditText = (EditText) rootView.findViewById(R.id.emailEditText);
            passwordEditText = (EditText) rootView.findViewById(R.id.passwordEditText);
            verifyEditText = (EditText) rootView.findViewById(R.id.verifyEditText);
            backButton = (Button) rootView.findViewById(R.id.backButton);
            backButton.setOnClickListener(this);
            registerButton = (Button) rootView.findViewById(R.id.registerButton);
            registerButton.setOnClickListener(this);
            return rootView;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == backButton.getId()) {
                this.back();
            } else if (v.getId() == registerButton.getId()) {
                this.register();
            }
        }

        public void register() {
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();
            boolean emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if (password.equals(verifyEditText.getText().toString()) && !password.isEmpty() && password.length() >= 6 && emailValid) {

                if (connectionClass.verifyEmail(email)) {

                    int id = connectionClass.register(email, password);
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
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Thank you for registering", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Email already in use", Toast.LENGTH_SHORT);
                    toast.show();
                }

            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        public void back() {
            Fragment fragment = new PollFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public static class CreateFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        Button expiryButton;
        Button backButton;
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

        public CreateFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create, container, false);
            connectionClass = new Database();

            argumentEditText = (EditText) rootView.findViewById(R.id.argumentEditText);
            option1EditText = (EditText) rootView.findViewById(R.id.option1EditText);
            option2EditText = (EditText) rootView.findViewById(R.id.option2EditText);
            option3EditText = (EditText) rootView.findViewById(R.id.option3EditText);
            option4EditText = (EditText) rootView.findViewById(R.id.option4EditText);

            backButton = (Button) rootView.findViewById(R.id.backButton);
            backButton.setOnClickListener(this);

            expiryButton = (Button) rootView.findViewById(R.id.expiryButton);
            expiryButton.setOnClickListener(this);

            expiryEditText = (EditText) rootView.findViewById(R.id.expiryEditText);
            expiryEditText.setInputType(InputType.TYPE_NULL);

            getCategories();
            spinnerCategory = (Spinner) rootView.findViewById(R.id.spinnerCategory);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(dataAdapter);

            createButton = (Button) rootView.findViewById(R.id.createButton);
            createButton.setOnClickListener(this);
            return rootView;
        }

        public static class DatePickerFragment extends DialogFragment
                implements DatePickerDialog.OnDateSetListener {


            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the current date as the default date in the picker
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

                // Create a new instance of DatePickerDialog and return it
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

                if(nextDate.after(expiryDate)){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    month +=1;
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

            if (v.getId() == backButton.getId()) {
                Fragment fragment = new PollFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            } else if (v.getId() == expiryButton.getId()) {
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

    public static class PollFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        //        Statement stmt = null;
        TextView pollTextView;
        RadioButton option1RadioButton;
        RadioButton option2RadioButton;
        RadioButton option3RadioButton;
        RadioButton option4RadioButton;
        TextView category;
        TextView expire;
        Button nextButton;
        Button voteButton;
        int id;
        RadioGroup optionsRadioGroup;
        CheckBox subscribeCheckBox;
        Button reportButton;
        Button newButton;
        Button shareButton;
        // get a layout defined in xml
        LinearLayout layout;
        // programmatically create a PieChart
        PieChart chart;
        Calendar c;
        SimpleDateFormat df;
        String currentDate;
        CallbackManager callbackManager;
        ShareDialog shareDialog;

        public PollFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.content_main, container, false);

            FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
            callbackManager = CallbackManager.Factory.create();
            shareDialog = new ShareDialog(this);
            shareButton = (Button)rootView.findViewById(R.id.shareButton);
            shareButton.setOnClickListener(this);

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
            layout = (LinearLayout) rootView.findViewById(R.id.linearLayout);
            chart = new PieChart(getActivity().getApplicationContext());
            reportButton = (Button) rootView.findViewById(R.id.reportButton);
            newButton = (Button) rootView.findViewById(R.id.newButton);
            newButton.setOnClickListener(this);

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = pref.edit();

            Intent intent;
            intent = getActivity().getIntent();
            int pollId = pref.getInt("pollid", 0);
            if (pollId != 0) {
                subscribedPoll(pollId);
                editor.remove("pollid");
                editor.commit();
            }else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                Uri uri = intent.getData();
                id = Integer.valueOf(uri.getQueryParameter("id"));
                subscribedPoll(id);
            }
            else {
                random();
            }


            return rootView;
        }

        //CALLED WHEN POLLFRAGMENT IS LOADED AFTER SUBSCRIBEFRAGMENT
        private void subscribedPoll(int pollId) {
            //Load a specific poll
            ResultSet rs = connectionClass.subscribedPoll(pollId);
            try {
                rs.next();
                id = rs.getInt("PollId");
                pollTextView.setText(rs.getString("Argument"));
                option1RadioButton.setText(rs.getString("Option1"));
                option2RadioButton.setText(rs.getString("Option2"));
                option3RadioButton.setText(rs.getString("Option3"));
                option4RadioButton.setText(rs.getString("Option4"));
                category.setText(rs.getString("CategoryName"));
                expire.setText(rs.getDate("ExpiryDate").toString());

                c = Calendar.getInstance();
                df = new SimpleDateFormat("yyyy-MM-dd");
                c.setTime(df.parse(currentDate));
                Date today = c.getTime();

                Calendar e = Calendar.getInstance();
                e.setTime(df.parse(rs.getDate("ExpiryDate").toString()));
                Date expiryDate = e.getTime();

                if(today.after(expiryDate) || today.equals(expiryDate)){
                    result(-1);
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Poll is expired", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void random() {
            //Load a random poll
            ResultSet rs = connectionClass.randomPoll(currentDate);
            try {
                rs.next();
                id = rs.getInt("PollId");
                pollTextView.setText(rs.getString("Argument"));
                option1RadioButton.setText(rs.getString("Option1"));
                option2RadioButton.setText(rs.getString("Option2"));
                option3RadioButton.setText(rs.getString("Option3"));
                option4RadioButton.setText(rs.getString("Option4"));
                category.setText(rs.getString("CategoryName"));
                expire.setText(rs.getDate("ExpiryDate").toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //method for nextButton
        public void next() {
            ResultSet rs = connectionClass.nextPoll(id, currentDate);
            try {
                rs.next();
                id = rs.getInt("PollId");
                pollTextView.setText(rs.getString("Argument"));
                option1RadioButton.setText(rs.getString("Option1"));
                option2RadioButton.setText(rs.getString("Option2"));
                option3RadioButton.setText(rs.getString("Option3"));
                option4RadioButton.setText(rs.getString("Option4"));
                category.setText(rs.getString("CategoryName"));
                expire.setText(rs.getDate("ExpiryDate").toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            optionsRadioGroup.clearCheck();
            subscribeCheckBox.setChecked(false);
            if (optionsRadioGroup.getVisibility() == View.GONE) {
                optionsRadioGroup.setVisibility(View.VISIBLE);
                voteButton.setVisibility(View.VISIBLE);
                subscribeCheckBox.setVisibility(View.VISIBLE);
                reportButton.setVisibility(View.VISIBLE);
                newButton.setVisibility(View.VISIBLE);
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
            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "You have to select an option", Toast.LENGTH_SHORT);
                toast.show();
            }

        }

        public void result(int selected){
            // programmatically create a PieChart
            PieChart chart = new PieChart(getActivity().getApplicationContext());

            try {
                ResultSet rs = connectionClass.vote(id, selected);
                rs.next();
                ArrayList<Entry> entries = new ArrayList<Entry>();
                entries.add(new Entry((float) rs.getInt("Vote1"), rs.getInt("Vote1")));
                entries.add(new Entry((float) rs.getInt("Vote2"), rs.getInt("Vote2")));
                entries.add(new Entry((float) rs.getInt("Vote3"), rs.getInt("Vote3")));
                entries.add(new Entry((float) rs.getInt("Vote4"), rs.getInt("Vote4")));
                PieDataSet dataset = new PieDataSet(entries, "");
                dataset.setColors(ColorTemplate.COLORFUL_COLORS);
                dataset.setValueTextSize(16);
                ArrayList<String> labels = new ArrayList<String>();
                labels.add(rs.getString("Option1"));
                labels.add(rs.getString("Option2"));
                labels.add(rs.getString("Option3"));
                labels.add(rs.getString("Option4"));
                PieData data = new PieData(labels, dataset);
                chart.setData(data);
                chart.setDescription("Results");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            optionsRadioGroup.setVisibility(View.GONE);
            voteButton.setVisibility(View.GONE);
            reportButton.setVisibility(View.GONE);
            newButton.setVisibility(View.GONE);
            subscribeCheckBox.setVisibility(View.GONE);
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
            }else if(v.getId() == shareButton.getId()){
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("http://settlz.com/view?id="+id))
                        .build();
                shareDialog.show(content);
            }
            else if (v.getId() == nextButton.getId()) {
                next();
            } else if (v.getId() == newButton.getId()) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                ;
                boolean login = pref.getBoolean("login", false);
                //user logged in
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
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Please Login/Register", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }
    }

    public static class SearchFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        EditText argumentEditTextView;
        Button searchButton;
        Button backButton;
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
            backButton = (Button) rootView.findViewById(R.id.backButton);
            backButton.setOnClickListener(this);
            getCategories();
            spinnerCategory = (Spinner) rootView.findViewById(R.id.spinnerCategory);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(dataAdapter);

            return rootView;
        }

        public void getCategories() {
            ResultSet rs = connectionClass.getCategories();
            categories.add("");
            try {
                while (rs.next()) {
                    categories.add(rs.getString("CategoryName"));
                }
                ;
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
                if (!categoryName.equals("")) {
                    categoryId = connectionClass.getCategoryId(categoryName);
                }


                ResultSet rs = connectionClass.getSearchResults(argumentEditTextView.getText().toString(), categoryId);
                try {
                    if (searchResultsLayout.getChildCount() > 0) {
                        searchResultsLayout.removeAllViews();
                    }

                    while (rs.next()) {
                        Button button = new Button(getActivity());
                        button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        button.setText(rs.getString("Argument"));
                        button.setId(rs.getInt("PollId"));
                        button.setOnClickListener(this);
                        buttonList.add(button);
                        searchResultsLayout.addView(button);
                    }
                } catch (SQLException e) {
                    Log.d("SQLPROBLEM", e.toString());

                }

            } else if (view.getId() == backButton.getId()) {
                Fragment fragment = new PollFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }

        }
    }

    public static class SubscribeFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        ArrayList<Button> buttonList;
        LinearLayout subscribeLayout;

        public SubscribeFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_subscribe, container, false);
            buttonList = new ArrayList<Button>();
            subscribeLayout = (LinearLayout) rootView.findViewById(R.id.subscribedResultsLayout);
            connectionClass = new Database();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            if (pref.getInt("id", -1) != -1) {
                ResultSet rs = connectionClass.getSubscribedPolls(pref.getInt("id", -1));
                try {
                    while (rs.next()) {
                        Button button = new Button(getActivity());
                        button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

            return rootView;

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
        }

    }

}