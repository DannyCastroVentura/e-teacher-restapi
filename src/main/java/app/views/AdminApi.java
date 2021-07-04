package app.views;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

@WebServlet("/admin/*")
public class AdminApi extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println(req.toString());
        String[] split = req.toString().split("/", 4);
        System.out.println(Arrays.toString(split));
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");

        Conection con = new Conection();
        System.out.println("This is the Connection: " + con);

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        if(split.length > 3){
            mostrarUmUser(split[3], con, jsonBuilder, false);
        }else{
            mostrarTodosOsUsers(con, jsonBuilder, false);
        }
        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonObject body = Json.createReader(req.getReader()).readObject();
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        Conection con = new Conection();

        final Charset fromCharset = Charset.forName("windows-1252");
        final Charset toCharset = StandardCharsets.UTF_8;

        String email = body.getString("email");
        String emailFixed = new String(email.getBytes(fromCharset), toCharset);

        System.out.println("Entrou no inserir");
        String password = body.getString("password");
        String nome = body.getString("nome");
        String passwordFixed = new String(password.getBytes(fromCharset), toCharset);
        String nomeFixed = new String(nome.getBytes(fromCharset), toCharset);
        registar(emailFixed, passwordFixed, nomeFixed, con, jsonBuilder);


        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonObject body = Json.createReader(req.getReader()).readObject();
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        Conection con = new Conection();

        final Charset fromCharset = Charset.forName("windows-1252");
        final Charset toCharset = StandardCharsets.UTF_8;

        String email = body.getString("email");
        String emailFixed = new String(email.getBytes(fromCharset), toCharset);

        if(body.containsKey("password")){
            String password = body.getString("password");
            String fixed = new String(password.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, emailFixed, fixed, false, con, "password");
        }
        if(body.containsKey("nome")){
            String nome = body.getString("nome");
            String fixed = new String(nome.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, emailFixed, fixed, false, con, "nome");
        }

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonObject body = Json.createReader(req.getReader()).readObject();
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        Conection con = new Conection();

        final Charset fromCharset = Charset.forName("windows-1252");
        final Charset toCharset = StandardCharsets.UTF_8;

        String email = body.getString("email");
        String emailFixed = new String(email.getBytes(fromCharset), toCharset);

        apagarAdmin(jsonBuilder, emailFixed, con);

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();

    }
    private void apagarAdmin(JsonObjectBuilder jsonBuilder, String email, Conection con) {
        System.out.println("Entrou no menu para apagar um admin");
        String sql = "DELETE FROM admin WHERE email = '" + email + "'";
        int res = con.executeSQL(sql);
        try {
            if (res > 0) {
                jsonBuilder.add("info", " Admin eliminado com sucesso!");
            } else {
                jsonBuilder.add("info", "Professor não existente!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void alterarString(JsonObjectBuilder jsonBuilder, String email, String string, boolean seEFoto, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 admin");
        String sql = "SELECT * FROM admin WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE admin SET " + campo + " = '" + string + "' WHERE email = '" + email + "'";
                if(seEFoto && string.contentEquals(""))
                {
                    sql = "UPDATE admin SET " + campo + " = null WHERE email = '" + email + "'";
                }
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum admin com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void registar(String email, String password, String nome, Conection con, JsonObjectBuilder jsonBuilder) {
        //Inserir um user
        System.out.println("Entrou no menu para registar admin");
        String sql = "INSERT INTO admin (email, password, nome) values " +
                "('" + email + "', '" + password + "', '" + nome + "')";
        int res = con.executeSQL(sql);
        if (res > 0) {
            jsonBuilder.add("info", "Admin registado com sucesso!");
        } else {
            jsonBuilder.add("info", "Aconteceu algum erro");
        }
    }

    private void mostrarTodosOsUsers(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os admins");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT * FROM admin";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showUser(rs, jsonArrayBuilder);

                existe = true;

            }
            jsonBuilder.add("admin", jsonArrayBuilder);

            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhum admin!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarUmUser(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar 1 admin");
        String[] split3 = s.split(" ", 2);
        String email = split3[0];
        String sql = "SELECT * FROM admin WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showUser(rs, jsonArrayBuilder);

                jsonBuilder.add("admin", jsonArrayBuilder);
                existe = true;
            }
            if(!existe){
                jsonBuilder.add("info", "Não existe nenhum admin com esse email!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showUser(ResultSet rs, JsonArrayBuilder jsonArrayBuilder) throws SQLException {
        String emailPresenteNaBaseDeDados = rs.getString("email");
        String passwordPresenteNaBaseDeDados = rs.getString("password");
        String nomePresenteNaBaseDeDados = rs.getString("nome");


        JsonObjectBuilder usersBuilder = Json.createObjectBuilder();
        JsonObject usersJson = usersBuilder
                .add("email", emailPresenteNaBaseDeDados)
                .add("nome", nomePresenteNaBaseDeDados)
                .add("password", passwordPresenteNaBaseDeDados).build();

        jsonArrayBuilder.add(usersJson);
    }


}