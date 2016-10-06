package devtech.settlz;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Fragment fragment = new PollFragment();
        getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();

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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_create) {
            // Handle the create poll button
        } else if (id == R.id.nav_featured){
            Fragment fragment = new PollFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_login) {
            Fragment fragment = new LoginFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.nav_register) {
            Fragment fragment = new RegisterFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.nav_profile) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);;
            boolean login = pref.getBoolean("login",false);
            //user logged in
            if(login){
                Fragment fragment = new ProfileFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame,fragment);
                ft.addToBackStack(null);
                ft.commit();
            }else{
                Fragment fragment = new LoginFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame,fragment);
                ft.addToBackStack(null);
                ft.commit();
                Toast toast = Toast.makeText(this.getApplicationContext(), "Login Account Required", Toast.LENGTH_SHORT);
                toast.show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class ProfileFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        LinearLayout subscribeLayout;
        Button logoutButton;
        Button changeButton;
        EditText passwordEditText;
        EditText verifyEditText;
        EditText changedEditText;
        ArrayList<Button> buttonList;

        public ProfileFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            connectionClass = new Database();
            buttonList = new ArrayList<Button>();
            passwordEditText = (EditText) rootView.findViewById(R.id.passwordEditText);
            verifyEditText = (EditText) rootView.findViewById(R.id.verifyEditText);
            changedEditText = (EditText) rootView.findViewById(R.id.changedEditText);
            logoutButton = (Button)rootView.findViewById(R.id.logoutButton);
            logoutButton.setOnClickListener(this);
            changeButton = (Button)rootView.findViewById(R.id.changeButton);
            changeButton.setOnClickListener(this);

            subscribeLayout = (LinearLayout)rootView.findViewById(R.id.subscribeLayout);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if(pref.getInt("id",-1) != -1){
                ResultSet rs = connectionClass.getSubscribedPolls(pref.getInt("id",-1));
                try{
                    while(rs.next()){
                        Button button = new Button(getActivity());
                        button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        button.setText(rs.getString("Argument"));
                        button.setId(rs.getInt("PollId"));
                        button.setOnClickListener(this);
                        buttonList.add(button);
                        subscribeLayout.addView(button);
                    }
                }catch(SQLException e){
                    Log.d("SQLPROBLEM",e.toString());

                }


            }

            return rootView;
        }

        public void logout(){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("id");
            editor.remove("email");
            editor.remove("password");
            editor.putBoolean("login",false);
            editor.commit();
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "You have been logged out!", Toast.LENGTH_SHORT);
            toast.show();
            Fragment fragment = new PollFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == logoutButton.getId()){
                this.logout();
            }
            if (v.getId() == changeButton.getId()) {
                this.changePassword();
            }
            for(int i=0;i<buttonList.size();i++){
                if(v.getId() == buttonList.get(i).getId()){
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("pollid",buttonList.get(i).getId());
                    Fragment fragment = new PollFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame,fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    editor.commit();
                }
            }
        }

        public void changePassword() {
            String password = passwordEditText.getText().toString();
            String changedPassword = changedEditText.getText().toString();
            String verifyPassword = verifyEditText.getText().toString();
            if(!password.isEmpty() && password.length() >= 6 && !changedPassword.isEmpty() && changedPassword.length() >= 6 && !verifyPassword.isEmpty() && verifyPassword.length() >= 6 && verifyPassword.equals(changedPassword)){

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if(password.equals(pref.getString("password",""))){
                    int id = pref.getInt("id",-1);
                    connectionClass.changePassword(id, changedPassword);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("password",changedPassword);
                    editor.commit();
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Password updated", Toast.LENGTH_SHORT);
                    toast.show();
                    Fragment fragment = new ProfileFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame,fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }else{
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_SHORT);
                    toast.show();
                }


            }else{
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
            emailEditText = (EditText)rootView.findViewById(R.id.emailEditText);
            passwordEditText = (EditText)rootView.findViewById(R.id.passwordEditText);
            registerButton = (Button)rootView.findViewById(R.id.registerButton);
            registerButton.setOnClickListener(this);
            loginButton = (Button)rootView.findViewById(R.id.loginButton);
            loginButton.setOnClickListener(this);
            backButton = (Button)rootView.findViewById(R.id.backButton);
            backButton.setOnClickListener(this);
            forgotButton = (Button)rootView.findViewById(R.id.forgotButton);
            forgotButton.setOnClickListener(this);
            return rootView;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == backButton.getId()){
                this.back();
            }else if(v.getId() == registerButton.getId()){
                this.register();
            }else if(v.getId() == loginButton.getId()){
                this.login();
            }else if(v.getId() == forgotButton.getId()){

            }
        }

        public void login(){
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            connectionClass.login(email, password);
            int id = connectionClass.login(email,password);

            if(!password.isEmpty() && id != -1 && !email.isEmpty()){
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.putBoolean("login", true);
                    editor.putInt("id", id);
                    editor.commit();
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Thank you for logging in!", Toast.LENGTH_SHORT);
                    toast.show();
                    Fragment fragment = new ProfileFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            else{
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid information", Toast.LENGTH_SHORT);
                toast.show();
            }

        }

        public void register(){
            Fragment fragment = new RegisterFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        public void back() {
            Fragment fragment = new PollFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public static class RegisterFragment extends Fragment implements View.OnClickListener{
        Database connectionClass;
//        Statement stmt = null;
        EditText emailEditText;
        EditText passwordEditText;
        EditText verifyEditText;
        Button backButton;
        Button registerButton;
        public RegisterFragment(){

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_register,container,false);
            connectionClass = new Database();
            emailEditText=(EditText)rootView.findViewById(R.id.emailEditText);
            passwordEditText=(EditText)rootView.findViewById(R.id.passwordEditText);
            verifyEditText=(EditText)rootView.findViewById(R.id.verifyEditText);
            backButton=(Button)rootView.findViewById(R.id.backButton);
            backButton.setOnClickListener(this);
            registerButton=(Button)rootView.findViewById(R.id.registerButton);
            registerButton.setOnClickListener(this);
            return rootView;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == backButton.getId()){
                this.back();
            }else if(v.getId() == registerButton.getId()){
                this.register();
            }
        }

        public void register() {
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();
            boolean emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if(password.equals(verifyEditText.getText().toString()) && !password.isEmpty() && password.length() >= 6 && emailValid){

                if(connectionClass.verifyEmail(email)){

                    int id = connectionClass.register(email,password);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("email",email);
                    editor.putString("password",password);
                    editor.putBoolean("login",true);
                    editor.putInt("id",id);
                    editor.commit();

                    Fragment fragment = new ProfileFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame,fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }else{
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Email Already in Use", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }else{
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid Information", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        public void back() {
            Fragment fragment = new PollFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.addToBackStack(null);
            ft.commit();
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
        // get a layout defined in xml
        LinearLayout layout;
        // programmatically create a PieChart
        PieChart chart;
        public PollFragment(){

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.content_main,container,false);
            connectionClass = new Database();
            pollTextView = (TextView)rootView.findViewById(R.id.pollTextView);
            option1RadioButton = (RadioButton)rootView.findViewById(R.id.option1RadioButton);
            option2RadioButton = (RadioButton)rootView.findViewById(R.id.option2RadioButton);
            option3RadioButton = (RadioButton)rootView.findViewById(R.id.option3RadioButton);
            option4RadioButton = (RadioButton)rootView.findViewById(R.id.option4RadioButton);
            category = (TextView)rootView.findViewById(R.id.categoryTextView);
            expire = (TextView)rootView.findViewById(R.id.expiredTextView);
            nextButton = (Button) rootView.findViewById(R.id.nextButton);
            nextButton.setOnClickListener(this);
            voteButton = (Button) rootView.findViewById(R.id.voteButton);
            voteButton.setOnClickListener(this);
            optionsRadioGroup = (RadioGroup) rootView.findViewById(R.id.optionsRadioGroup);
            subscribeCheckBox = (CheckBox)rootView.findViewById(R.id.subscribeCheckBox);
            layout = (LinearLayout) rootView.findViewById(R.id.linearLayout);
            chart = new PieChart(getActivity().getApplicationContext());
            reportButton = (Button) rootView.findViewById(R.id.reportButton);
            newButton = (Button) rootView.findViewById(R.id.newButton);

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = pref.edit();
            int pollId = pref.getInt("pollid",0);
            if(pollId != 0){
                subscribedPoll(pollId);
                editor.remove("pollid");
                editor.commit();
            }else{
                random();
            }


            return rootView;
        }

        private void subscribedPoll(int pollId) {
            //Load a random poll
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void random(){
            //Load a random poll
            ResultSet rs = connectionClass.randomPoll();
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
            ResultSet rs = connectionClass.nextPoll(id);
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
            if(optionsRadioGroup.getVisibility() == View.GONE){
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
            int selected =0;
            if (option1RadioButton.isChecked()){
                selected = 1;
            }
            else if (option2RadioButton.isChecked()){
                selected = 2;
            }
            else if (option3RadioButton.isChecked()){
                selected = 3;
            }
            else if (option4RadioButton.isChecked()){
                selected = 4;
            }

            if(selected !=0){
                // programmatically create a PieChart
                PieChart chart = new PieChart(getActivity().getApplicationContext());

                try {
                    ResultSet rs = connectionClass.vote(id, selected);
                    rs.next();
                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    entries.add(new Entry((float)rs.getInt("Vote1"),rs.getInt("Vote1")));
                    entries.add(new Entry((float)rs.getInt("Vote2"),rs.getInt("Vote2")));
                    entries.add(new Entry((float)rs.getInt("Vote3"),rs.getInt("Vote3")));
                    entries.add(new Entry((float)rs.getInt("Vote4"),rs.getInt("Vote4")));
                    PieDataSet dataset = new PieDataSet(entries,"");
                    dataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    dataset.setValueTextSize(16);
                    ArrayList<String> labels= new ArrayList<String>();
                    labels.add(rs.getString("Option1"));
                    labels.add(rs.getString("Option2"));
                    labels.add(rs.getString("Option3"));
                    labels.add(rs.getString("Option4"));
                    PieData data = new PieData(labels,dataset);
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
                int height = (int)(metrics.heightPixels*0.9);
                int width = (int)(metrics.widthPixels*0.9);
                layout.addView(chart,height,width); // add the programmatically created chart
            } else{
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "You have to select an option!", Toast.LENGTH_SHORT);
                toast.show();
            }

        }

        @Override
        public void onClick(View v) {
            if(v.getId() == voteButton.getId()){
                vote();
            }else if(v.getId() == nextButton.getId()){
                next();
            }
        }
    }


}
