package devtech.settlz;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Database {
    String ip = "142.55.49.224";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "master";
    String un = "sa";
    String password = "Devtech1$";
    Connection conn = null;

    public Database() {
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

    public ResultSet randomPoll(String currentDate) {
        PreparedStatement preparedStmt = null;
        String query = "Select PollId, Argument, CategoryName, ExpiryDate, Option1, Option2, Option3, Option4, Facebook_FacebookId, Twitter_TwitterId " +
                "from Polls " +
                "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                "INNER JOIN Categories ON Polls.CategoryCategoryId = Categories.CategoryId " +
                "WHERE ExpiryDate > ? AND ReportCount < 3 " +
                "ORDER BY NEWID();";


        ResultSet rs = null;
        try {
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, currentDate);
            rs = preparedStmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet nextPoll(int id, String currentDate) {
        PreparedStatement preparedStmt = null;
        String query = "Select PollId, Argument, CategoryName, ExpiryDate, Option1, Option2, Option3, Option4, Facebook_FacebookId, Twitter_TwitterId " +
                "from Polls " +
                "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                "INNER JOIN Categories ON Polls.CategoryCategoryId = Categories.CategoryId " +
                "WHERE PollId != ? AND ExpiryDate > ? AND ReportCount < 3 " +
                "ORDER BY NEWID();";

        ResultSet rs = null;
        try {
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, id);
            preparedStmt.setString(2, currentDate);
            rs = preparedStmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet vote(int pollId, int selected) {

        ResultSet results = null;
        try {
            PreparedStatement preparedStmt = null;
            String query = "Select PollId, OptionsId, VotesId " +
                    "from Polls " +
                    "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                    "INNER JOIN Votes ON Options.Vote_VotesId = Votes.VotesId " +
                    "WHERE PollId = ?";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, pollId);
            ResultSet rs = null;
            rs = preparedStmt.executeQuery();
            rs.next();
            int optionsId = rs.getInt("OptionsId");
            int votesId = rs.getInt("VotesId");
            if (selected != -1) {
                String update = "UPDATE Votes " +
                        "SET Vote" + selected + "= Vote" + selected + "+1 " +
                        "WHERE VotesId = " + rs.getInt("VotesId") + ";";
                Statement stmt2 = conn.createStatement();
                stmt2.executeUpdate(update);
            }

            String resultQuery = "Select Option1, Option2, Option3, Option4, Vote1, Vote2, Vote3, Vote4 " +
                    "FROM Options " +
                    "INNER JOIN Votes ON Options.Vote_VotesId = Votes.VotesId " +
                    "WHERE OptionsId = " + optionsId + " AND VotesId = " + votesId + ";";
            Statement stmt3 = conn.createStatement();
            results = stmt3.executeQuery(resultQuery);
            return results;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;

    }

    public ResultSet setSubscribePoll(int pollId, int userId) {
        String query = "INSERT INTO PollUser (Polls_PollId, Users_UserId) VALUES(" + pollId + ", " + userId + ");";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet deleteSubscribedPoll(int pollId, int userId) {
        String query = "DELETE FROM PollUser " +
                "WHERE Polls_PollId = " + pollId + " AND Users_UserId = " + userId + ";";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public Boolean checkPollUser(int pollId, int userId) {
        String query = "SELECT Polls_PollId, Users_UserId " +
                "FROM PollUser " +
                "WHERE Polls_PollId = " + pollId + " AND Users_UserId = " + userId + ";";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int register(String email, String password, int security, String answer) {
        try {

            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);

            String dateCreated = year + "-" + month + "-" + day;


            String query = "INSERT INTO Users (Password, Email, Created, SecurityId, Answer) " +
                    "VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, password);
            preparedStmt.setString(2, email);
            preparedStmt.setString(3, dateCreated);
            preparedStmt.setInt(4, security);
            preparedStmt.setString(5, answer);
            preparedStmt.executeUpdate();

            String userid = "SELECT UserId " +
                    "FROM Users " +
                    "WHERE Email='" + email + "';";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(userid);
            rs.next();
            return rs.getInt("UserId");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet getSubscribedPolls(int userId, int top) {
        String query = "SELECT TOP " + top + " Argument, PollId " +
                "From Polls " +
                "INNER JOIN PollUser " +
                "ON PollId = PollUser.Polls_PollId " +
                "INNER JOIN Users " +
                "ON PollUser.Users_UserId = Users.UserId " +
                "WHERE UserId = " + userId + " " +
                "ORDER BY Argument;";
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
        String query = "SELECT * FROM USERS WHERE email='" + email + "';";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next() == false) {
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
                    "WHERE Email = ? AND Password =?;";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, email);
            preparedStmt.setString(2, password);

            ResultSet rs = preparedStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("UserId");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean banned(String email, String password) {
        try {
            String query = "SELECT UserId, Banned " +
                    "FROM Users " +
                    "WHERE Email = '" + email + "' AND Password ='" + password + "' AND Banned = 1;";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void changePassword(int id, String password) {
        try {

            String query = "UPDATE Users " +
                    "SET Password='" + password + "' " +
                    "WHERE UserId=" + id + "";

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet subscribedPoll(int pollId) {
        String query = "Select PollId, Argument, CategoryName, ExpiryDate, Option1, Option2, Option3, Option4, Facebook_FacebookId, Twitter_TwitterId " +
                "from Polls " +
                "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                "INNER JOIN Categories ON Polls.CategoryCategoryId = Categories.CategoryId " +
                "WHERE PollId = " + pollId + ";";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet subscribedExpiredPoll(int pollId) {
        String query = "Select PollId, OptionsId, VotesId " +
                "from Polls " +
                "INNER JOIN Options ON Polls.Option_OptionsId = Options.OptionsId " +
                "INNER JOIN Votes ON Options.Vote_VotesId = Votes.VotesId " +
                "WHERE PollId = " + pollId +
                ";";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public ResultSet getCategories() {
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

    public int getCategoryId(String name) {
        String query = "Select CategoryId, CategoryName " +
                "from Categories " +
                "where CategoryName = '" + name + "';";

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

    public int create(String argument, String option1, String option2, String option3, String option4, String expiryDate, int categoryId, int userId) {
        try {
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
                    "VALUES('" + option1 + "','" + option2 + "','" + option3 + "','" + option4 + "'," + voteId + ");";
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

            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);

            Log.d("CREATETEST", "I GOT TO HERE");
            String dateCreated = year + "-" + month + "-" + day;
            String insertPollsQuery = "INSERT INTO Polls (Argument,ReportCount,PollStatus,ExpiryDate,CategoryCategoryId,Option_OptionsId,Twitter_TwitterId,Facebook_FacebookId, User_UserId, DateCreated) " +
                    "VALUES(?,0,'True', ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStmt = conn.prepareStatement(insertPollsQuery);
            preparedStmt.setString(1, argument);
            preparedStmt.setString(2, expiryDate);
            preparedStmt.setInt(3, categoryId);
            preparedStmt.setInt(4, optionsId);
            preparedStmt.setInt(5, twitterId);
            preparedStmt.setInt(6, facebookId);
            preparedStmt.setInt(7, userId);
            preparedStmt.setString(8, dateCreated);
            preparedStmt.executeUpdate();


            String getPollsQuery = "SELECT TOP 1 PollId FROM Polls " +
                    "ORDER BY PollId DESC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(getPollsQuery);
            rs.next();
            int pollId = rs.getInt("PollId");
            Log.d("CREATETEST", "I GOT TO HERE2");

            String insertPollUserQuery = "INSERT INTO PollUser (Polls_PollId, Users_UserId) VALUES(" + pollId + ", " + userId + ");";
            stmt = conn.createStatement();
            stmt.executeUpdate(insertPollUserQuery);

            return pollId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet getSearchResults(String s, int categoryId) {
        String query = "SELECT TOP 10 PollId, Argument FROM Polls WHERE ReportCount < 3 AND ";


        if (categoryId != -1) {
            query += "CategoryCategoryId = ? AND Argument LIKE ? ";
        } else {
            query += "Argument LIKE ?";
        }
        query += "ORDER BY Argument;";

        ResultSet rs = null;
        try {
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            if (categoryId != -1) {
                preparedStmt.setInt(1, categoryId);
                preparedStmt.setString(2, "%%" + s + "%%");
            } else {
                preparedStmt.setString(1, "%%" + s + "%%");
            }
            rs = preparedStmt.executeQuery();
            return rs;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;

    }

    public void updateShareCount(int facebookId) {
        String query = "UPDATE Facebooks Set TimesShared = TimesShared + 1 WHERE FacebookId = '" + facebookId + "'";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTwitterCount(int twitterId) {
        String query = "UPDATE Twitters Set TimesShared = TimesShared + 1 WHERE TwitterId = '" + twitterId + "'";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reportPoll(int id) {
        String query = "UPDATE Polls Set ReportCount = ReportCount + 1 WHERE PollId = " + id;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getQuestions() {
        String query = "Select securityId, securityQuestion " +
                "from Security;";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public String getQuestion(String email) {
        String query = "SELECT email, securityQuestion, Answer from Users INNER JOIN Security ON Users.SecurityId = Security.securityId Where Users.Email = '" + email + "';";
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            return rs.getString("securityQuestion");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public int getQuestionId(String question) {
        String query = "Select securityId, securityQuestion " +
                "from Security " +
                "where securityQuestion = '" + question + "';";

        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            return rs.getInt("securityId");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean verifyAnswer(String email, String answer) {
        String query = "SELECT Answer FROM USERS WHERE email='" + email + "';";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            if (rs.getString("Answer").toLowerCase().equals(answer.toLowerCase())) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void forgotPassword(String email, String changedPassword) {
        try {

            String query = "UPDATE Users " +
                    "SET Password='" + changedPassword + "' " +
                    "WHERE Email='" + email + "'";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeQuestion(int id, int questionId, String answer) {
        try {

            String query = "UPDATE Users " +
                    "SET SecurityId=" + questionId + ", Answer='" + answer + "' " +
                    "WHERE UserId=" + id;
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
