package devtech.settlz;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Point;
import android.os.StrictMode;
import android.util.Log;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_login) {

        } else if (id == R.id.nav_register) {
            Fragment fragment = new RegisterFragment();
            getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();

        } else if (id == R.id.nav_profile) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class RegisterFragment extends Fragment implements View.OnClickListener{
        Database connectionClass;
        Statement stmt = null;
        EditText usernameEditText;
        EditText passwordEditText;
        EditText verifyEditText;
        Button backButton;
        Button registerButton;
        public RegisterFragment(){

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_register,container,false);
            connectionClass = new Database();
            usernameEditText=(EditText)rootView.findViewById(R.id.usernameEditText);
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
        }

        public void back() {
            Log.d("ERRORTAG","I GOT TO THE BACK METHOD");
            Fragment fragment = new PollFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public static class PollFragment extends Fragment implements View.OnClickListener {
        Database connectionClass;
        Statement stmt = null;
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
            random();
            return rootView;
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
