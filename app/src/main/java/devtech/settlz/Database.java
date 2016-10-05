package devtech.settlz;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {
    String ip = "142.55.49.224";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "master";
    String un = "sa";
    String password = "Devtech1$";
    Connection conn = null;

    public Database(){
        this.connect();
    }

    @SuppressLint("NewApi")
    public void connect() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String ConnURL = null;
        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection("jdbc:jtds:sqlserver://142.55.49.224/master",
                    "sa", "Devtech1$");
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
    }

    public ResultSet randomPoll(){
        String query = "Select PollId, Argument, CategoryName, ExpiryDate, Option1, Option2, Option3, Option4 " +
                "from Polls " +
                "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                "INNER JOIN Categories ON Polls.CategoryCategoryId = Categories.CategoryId " +
                "ORDER BY NEWID();";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet nextPoll (int id){
        String query = "Select PollId, Argument, CategoryName, ExpiryDate, Option1, Option2, Option3, Option4 " +
                "from Polls " +
                "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                "INNER JOIN Categories ON Polls.CategoryCategoryId = Categories.CategoryId " +
                "WHERE PollId != " +id+
                "ORDER BY NEWID();";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet vote (int pollId, int selected){

        ResultSet results = null;
        try {
            //Update Votes table
            String query = "Select PollId, OptionsId, VotesId " +
                    "from Polls " +
                    "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                    "INNER JOIN Votes ON Options.Vote_VotesId = Votes.VotesId " +
                    "WHERE PollId = " +pollId+
                    ";";
            ResultSet rs = null;
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            int optionsId = rs.getInt("OptionsId");
            int votesId = rs.getInt("VotesId"); //Store the VotesId to retrieve for results activity
            String update = "UPDATE Votes " +
                    "SET Vote"+ selected + "= Vote" + selected + "+1 " +
                    "WHERE VotesId = " +rs.getInt("VotesId")+";";
            Statement stmt2 = conn.createStatement();
            stmt2.executeUpdate(update);

            String resultQuery= "Select Option1, Option2, Option3, Option4, Vote1, Vote2, Vote3, Vote4 " +
                    "FROM Options " +
                    "INNER JOIN Votes ON Options.Vote_VotesId = Votes.VotesId " +
                    "WHERE OptionsId = " +optionsId+" AND VotesId = "+votesId+";";
            Statement stmt3 = conn.createStatement();
            results = stmt3.executeQuery(resultQuery);
            return results;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;

    }

    public int register(String email, String password) {
        try {
            String query="INSERT INTO Users (Password, Email) " +
                     "VALUES ('"+password+"', '"+email+"');";

            Statement stmt = conn.createStatement();
            stmt.executeQuery(query);

            String userid = "SELECT UserId " +
                    "FROM Users " +
                    "WHERE Email="+email+";";

            Statement stmt2 = conn.createStatement();
            ResultSet rs = stmt2.executeQuery(userid);
            rs.next();
            return rs.getInt("UserId");
//            return 2;



        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 2;
    }

    public ResultSet getSubscribedPolls(int userId) {
        String query="SELECT Argument, PollId " +
                "From Polls " +
                "INNER JOIN PollUser " +
                "ON PollId = PollUser.Polls_PollId " +
                "INNER JOIN Users " +
                "ON PollUser.Users_UserId = Users.UserId " +
                "WHERE UserId = "+userId+";";
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            return rs;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public boolean verifyEmail(String email) {
        String query="SELECT * FROM USERS WHERE email='"+email+"';";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            //rs.next() returns false if the ResultSet is empty because the cursor is before first line
            //this means email is available to register
            if(rs.next() == false){
                return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void login(String email, String password) {
    }
}