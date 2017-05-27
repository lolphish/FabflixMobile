package fabflix.fabflixmobile;

import android.util.Log;

import java.sql.*;

/**
 * Created by phee on 5/24/17.
 */

public class AccessDB {
    private Connection conn;
    private Statement stmt;
    private PreparedStatement ps;
    private ResultSet rs;

    public AccessDB() {
        String DB_URL = "jdbc:mysql://ec2-34-210-15-49.us-west-2.compute.amazonaws.com:8080/moviedb";
        String username = "root";
        String password = "123456789";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.e("Android", " MySQL Connection ok");
            conn = DriverManager.getConnection(DB_URL, username, password);
        } catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
            Log.e("Android-system","system get connection");
        }
    }
    public boolean notEmpty(ResultSet rs) throws SQLException {
        rs.next();
        return rs.getInt("total") != 0;
    }

    public boolean validate(String name,String pass){
        String findCustomer = String.format("select count(*) as total from customers c where c.email = '%s' and c.cpassword = '%s';", name, pass);
        try{

            ps = conn.prepareStatement(findCustomer);
            ResultSet rs = ps.executeQuery();
            return notEmpty(rs);
        }catch(Exception error){
            Log.e("Android", "ERROR");
            return false;
        }
    }

}
