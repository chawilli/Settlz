package devtech.settlz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.StrictMode;
import android.util.Log;
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
    //    Connection connect;
//    String ipaddress, db, username, password;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionClass = new Database();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pollTextView = (TextView)findViewById(R.id.pollTextView);
        option1RadioButton = (RadioButton)findViewById(R.id.option1RadioButton);
        option2RadioButton = (RadioButton)findViewById(R.id.option2RadioButton);
        option3RadioButton = (RadioButton)findViewById(R.id.option3RadioButton);
        option4RadioButton = (RadioButton)findViewById(R.id.option4RadioButton);
        category = (TextView)findViewById(R.id.categoryTextView);
        expire = (TextView)findViewById(R.id.expiredTextView);
        nextButton = (Button) findViewById(R.id.nextButton);
        voteButton = (Button) findViewById(R.id.voteButton);
        optionsRadioGroup = (RadioGroup) findViewById(R.id.optionsRadioGroup);
        subscribeCheckBox = (CheckBox)findViewById(R.id.subscribeCheckBox);
        layout = (LinearLayout) findViewById(R.id.linearLayout);
        chart = new PieChart(getApplicationContext());
        reportButton = (Button) findViewById(R.id.reportButton);
        newButton = (Button) findViewById(R.id.newButton);
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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //ButtonListener method for nextButton
    public void next(View view) {
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

    //ButtonListener method for voteButton
    public void vote(View view) {
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
        PieChart chart = new PieChart(getApplicationContext());

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
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = (int)(metrics.heightPixels*0.9);
        int width = (int)(metrics.widthPixels*0.9);
        layout.addView(chart,height,width); // add the programmatically created chart
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

        } else if (id == R.id.nav_profile) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
