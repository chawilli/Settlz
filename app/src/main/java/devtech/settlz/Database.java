package devtech.settlz;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;


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

    public ResultSet randomPoll(String currentDate){
        //c = Calendar.getInstance();
        //df = new SimpleDateFormat("yyyy-MM-dd");
        //currentDate = df.format(c.getTime());
        String query = "Select PollId, Argument, CategoryName, ExpiryDate, Option1, Option2, Option3, Option4 " +
                "from Polls " +
                "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                "INNER JOIN Categories ON Polls.CategoryCategoryId = Categories.CategoryId " +
                "WHERE ExpiryDate > '"+currentDate+"' " +
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

    public ResultSet nextPoll (int id, String currentDate){
        String query = "Select PollId, Argument, CategoryName, ExpiryDate, Option1, Option2, Option3, Option4 " +
                "from Polls " +
                "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                "INNER JOIN Categories ON Polls.CategoryCategoryId = Categories.CategoryId " +
                "WHERE PollId != " +id+ " AND ExpiryDate > '"+currentDate+"' " +
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
        return -1;
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

    public int login(String email, String password) {
        try {

            String query = "SELECT UserId " +
                    "FROM Users " +
                    "WHERE Email = '"+email+"' AND Password ='"+password+"';";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()){
                return rs.getInt("UserId");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void changePassword(int id, String password) {
        try {

            String query = "UPDATE Users " +
                    "SET Password='"+password+"' " +
                    "WHERE UserId="+id+"";

            Statement stmt = conn.createStatement();
            stmt.executeQuery(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //USED WHEN YOU PRESS A BUTTON IN SUBSCRIBEFRAGMENT
    public ResultSet subscribedPoll(int pollId) {
        String query = "Select PollId, Argument, CategoryName, ExpiryDate, Option1, Option2, Option3, Option4 " +
                "from Polls " +
                "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                "INNER JOIN Categories ON Polls.CategoryCategoryId = Categories.CategoryId " +
                "WHERE PollId = "+pollId+";";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet getCategories(){
        String query = "Select CategoryId, CategoryName " +
                "from Categories;";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public int getCategoryId(String name){
        String query = "Select CategoryId, CategoryName " +
                "from Categories " +
                "where CategoryName = '"+name+"';";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            return rs.getInt("CategoryId");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int create(String argument, String option1, String option2, String option3, String option4, String s, int categoryId, int userId) {
        try {
//            DECLARE @voteid int
//            DECLARE @twitterid int
//            DECLARE @facebookid int
//            DECLARE @optionsid int
            int voteId;
            int twitterId;
            int facebookId;
            int optionsId;
            String insertVoteQuery = "INSERT INTO Votes (Vote1,Vote2,Vote3,Vote4) " +
                    "VALUES (0,0,0,0)";
            String getVoteIdQuery = "SELECT TOP 1 VotesId FROM Votes " +
                    "ORDER BY VotesId DESC;";
            String getOptionsQuery = "SELECT TOP 1 OptionsId FROM Options " +
                    "ORDER BY OptionsId DESC";
            String insertTwitterQuery = "INSERT INTO Twitters (TimesShared) " +
                    "VALUES(0);";
            String getTwitterQuery = "SELECT TOP 1 TwitterId FROM Twitters " +
                    "ORDER BY TwitterId DESC";
            String insertFacebookQuery = "INSERT INTO Facebooks (TimesShared) " +
                    "VALUES(0);";
            String getFacebookQuery = "SELECT TOP 1 FacebookId FROM Facebooks " +
                    "ORDER BY FacebookId DESC";

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(insertVoteQuery);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getVoteIdQuery);
            rs.next();
            voteId = rs.getInt("VotesId");

            String insertOptionsQuery = "INSERT INTO Options (Option1,Option2,Option3,Option4,Vote_VotesId) " +
                    "VALUES('"+option1+"','"+option2+"','"+option3+"','"+option4+"',"+voteId+");";
            stmt = conn.createStatement();
            stmt.executeUpdate(insertOptionsQuery);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(getOptionsQuery);
            rs.next();
            optionsId = rs.getInt("OptionsId");

            stmt = conn.createStatement();
            stmt.executeUpdate(insertTwitterQuery);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(getTwitterQuery);
            rs.next();
            twitterId = rs.getInt("TwitterId");

            stmt = conn.createStatement();
            stmt.executeUpdate(insertFacebookQuery);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(getFacebookQuery);
            rs.next();
            facebookId = rs.getInt("FacebookId");

            String insertPollsQuery = "INSERT INTO Polls (Argument,ReportCount,PollStatus,ExpiryDate,CategoryCategoryId,Option_OptionsId,Twitter_TwitterId,Facebook_FacebookId) " +
                    "VALUES('"+argument+"',0,'True','"+s+"',"+categoryId+","+optionsId+","+twitterId+","+facebookId+");";
            String getPollsQuery = "SELECT TOP 1 PollId FROM Polls " +
                    "ORDER BY PollId DESC";
            stmt = conn.createStatement();
            stmt.executeUpdate(insertPollsQuery);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(getPollsQuery);
            rs.next();
            int pollId = rs.getInt("PollId");

            String insertPollUserQuery= "INSERT INTO PollUser (Polls_PollId, Users_UserId) VALUES("+pollId+", "+userId+");";
            stmt = conn.createStatement();
            stmt.executeUpdate(insertPollUserQuery);

            return pollId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet getSearchResults(String s, int categoryId) {
            String query="SELECT TOP 10 PollId, Argument FROM Polls WHERE ";
        if(categoryId!=-1){
            query+="CategoryCategoryId = "+categoryId+" AND Argument LIKE '%%"+s+"%%' ";
        }
        else{
            query+="Argument LIKE '%%"+s+"%%' ";
        }

//        String query ="SELECT TOP 2 PollId, Argument FROM Polls WHERE CategoryCategoryId = 7 AND Argument LIKE '%%%%';";
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
}
