package app.views;

import java.sql.*;

public class Conection {
    private Connection con;

    public Conection()
    {

        String url = "jdbc:postgresql://db:5432/finalproject";
        String usuario = "postgres";
        String senha = "123";


        //String url = "jdbc:postgresql://SG-projetoFinal-2073-pgsql-master.servers.mongodirector.com:6432/postgres";
        //String usuario = "sgpostgres";
        //String senha = "uXc2U0VrmUhL,Ttn";
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
