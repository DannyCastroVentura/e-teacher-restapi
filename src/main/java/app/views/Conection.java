package app.views;

import java.sql.*;

public class Conection {
    private Connection con;

    public Conection()
    {

        String url = "jdbc:postgresql://db:5432/postgres";
        String usuario = "postgres";
        String senha = "123";

        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, usuario, senha);
            System.out.println("Conexão realizada com sucesso!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Houve um erro!");
            e.printStackTrace();
        }
    }

    public int executeSQL(String sql) {
        try{
            Statement stm = con.createStatement();
            return stm.executeUpdate(sql);
        } catch(SQLException throwables){
            throwables.printStackTrace();
            return 0;
        }
    }

    public ResultSet selectSQL(String sql){
        try{
            Statement stm = con.createStatement();
            return stm.executeQuery(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

}
