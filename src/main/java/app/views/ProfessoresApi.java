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

@WebServlet("/professores/*")
public class ProfessoresApi extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println(req.toString());
        String[] split = req.toString().split("/", 5);
        System.out.println(Arrays.toString(split));
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");

        Conection con = new Conection();
        System.out.println("This is the Connection: " + con);

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        if(split.length > 3){
            if(split.length > 4){
                mostrarPasswordDeUmUser(split[3], con, jsonBuilder);
            }else{
                mostrarUmUser(split[3], con, jsonBuilder, false);
            }
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
        if(!body.containsKey("fotoPerfil") && !body.containsKey("fotoFundo") && !body.containsKey("password") && !body.containsKey("nome") && !body.containsKey("resumo") && !body.containsKey("exp") && !body.containsKey("idArea") && !body.containsKey("orcidId"))
        {
            alterarBoolean(emailFixed, con, jsonBuilder, "estado", true);
        }else{

            if(body.containsKey("fotoPerfil")) {
                String fotoPerfil = body.getString("fotoPerfil");
                String fixed = new String(fotoPerfil.getBytes(fromCharset), toCharset);
                alterarString(jsonBuilder, emailFixed, fixed, true, con, "fotoPerfil");
            }
            if(body.containsKey("fotoFundo")){
                String fotoFundo = body.getString("fotoFundo");
                String fixed = new String(fotoFundo.getBytes(fromCharset), toCharset);
                alterarString(jsonBuilder, emailFixed, fixed, true, con, "fotoFundo");
            }
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
            if(body.containsKey("resumo")){
                String resumo = body.getString("resumo");
                String fixed = new String(resumo.getBytes(fromCharset), toCharset);
                alterarString(jsonBuilder, emailFixed, fixed, false, con, "resumo");
            }
            if(body.containsKey("exp")){
                int exp = body.getInt("exp");
                alterarInt(jsonBuilder, emailFixed, exp, con, "exp");
            }
            if(body.containsKey("idArea")){
                int idArea = body.getInt("idArea");
                alterarInt(jsonBuilder, emailFixed, idArea, con, "idArea");
            }

            if(body.containsKey("orcidId")){
                System.out.println("vai alterar o orcidId");
                String orcidId = body.getString("orcidId");
                String fixed = new String(orcidId.getBytes(fromCharset), toCharset);
                alterarString(jsonBuilder, emailFixed, fixed, false, con, "orcidId");
            }

        }

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();


    }


    private void mostrarPasswordDeUmUser(String split, Conection con, JsonObjectBuilder jsonBuilder){
        String[] split3 = split.split(" ", 2);
        String[] split4 = split3[0].split("/", 2);
        String email = split4[0];
        System.out.println("Entrou no menu para mostrar a password de 1 professor");
        String sql = "SELECT * FROM professores WHERE email = '" + email + "'";
        System.out.println(split);
        ResultSet rs = con.selectSQL(sql);
        try {
            while(rs.next()) {
                System.out.println(rs.getString("password"));
                jsonBuilder.add("password", rs.getString("password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void alterarString(JsonObjectBuilder jsonBuilder, String email, String string, boolean seEFoto, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 professor");
        String sql = "SELECT * FROM professores WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE professores SET " + campo + " = '" + string + "' WHERE email = '" + email + "'";
                if(seEFoto && string.contentEquals(""))
                {
                    sql = "UPDATE professores SET " + campo + " = null WHERE email = '" + email + "'";
                }
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum professor com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void alterarInt(JsonObjectBuilder jsonBuilder, String email, int integer, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar a foto de perfil de 1 user");
        String sql = "SELECT * FROM professores WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE professores SET " + campo + " = " + integer + " WHERE email = '" + email + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum professor com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registar(String email, String password, String nome, Conection con, JsonObjectBuilder jsonBuilder) {
        //Inserir um user
        System.out.println("Entrou no menu para registar conta");
        String sql = "INSERT INTO professores (email, password, nome) values " +
                "('" + email + "', '" + password + "', '" + nome + "')";
        int res = con.executeSQL(sql);
        if (res > 0) {
            jsonBuilder.add("info", "Professor registado com sucesso!");
        } else {
            jsonBuilder.add("info", "Aconteceu algum erro");
        }
    }

    private void alterarBoolean(String email, Conection con, JsonObjectBuilder jsonBuilder, String campo, boolean valor) {
        //alterar um user
        System.out.println("Entrou no menu para alterar conta");
        String sql = "UPDATE professores SET " + campo  + " = " + valor + " WHERE email = '" + email + "'";
        int res = con.executeSQL(sql);
        if (res > 0) {
            jsonBuilder.add("info", "Professor alterado com sucesso!");
        } else {
            jsonBuilder.add("info", "Aconteceu algum erro");
        }
    }

    private void mostrarTodosOsUsers(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os users");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT * FROM professores";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showUser(rs, jsonArrayBuilder);

                existe = true;

            }
            jsonBuilder.add("professores", jsonArrayBuilder);

            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhum user!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarUmUser(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar 1 user");
        String[] split3 = s.split(" ", 2);
        String email = split3[0];
        String sql = "SELECT * FROM professores WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showUser(rs, jsonArrayBuilder);

                jsonBuilder.add("professores", jsonArrayBuilder);
                existe = true;
            }
            if(!existe){
                jsonBuilder.add("info", "Não existe nenhum user com esse email!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showUser(ResultSet rs, JsonArrayBuilder jsonArrayBuilder) throws SQLException {
        String emailPresenteNaBaseDeDados = rs.getString("email");
        String passwordPresenteNaBaseDeDados = rs.getString("password");
        String nomePresenteNaBaseDeDados = rs.getString("nome");
        String estadoPresenteNaBaseDeDados = rs.getString("estado");
        String fotoFundoPresenteNaBaseDeDados = rs.getString("fotoFundo");
        String fotoPerfilPresenteNaBaseDeDados = rs.getString("fotoPerfil");
        String resumoPresenteNaBaseDeDados = rs.getString("resumo");
        int expPresenteNaBaseDeDados = rs.getInt("exp");
        String idAreaPresenteNaBaseDeDados = rs.getString("idArea");
        String orcidIdPresenteNaBaseDeDados = rs.getString("orcidId");
        if(fotoPerfilPresenteNaBaseDeDados == null){
            fotoPerfilPresenteNaBaseDeDados = "https://www.legal-tech.de/wp-content/uploads/Profilbild-Platzhalter.png";
        }
        if(fotoFundoPresenteNaBaseDeDados == null){
            fotoFundoPresenteNaBaseDeDados = "https://www.tarabba.com.au/wp-content/uploads/2015/09/Empty-Background.png";
        }
        if(resumoPresenteNaBaseDeDados == null){
            resumoPresenteNaBaseDeDados = "";
        }
        if(idAreaPresenteNaBaseDeDados == null){
            idAreaPresenteNaBaseDeDados = "";
        }
        if(orcidIdPresenteNaBaseDeDados == null){
            orcidIdPresenteNaBaseDeDados = "";
        }


        JsonObjectBuilder usersBuilder = Json.createObjectBuilder();
        JsonObject usersJson = usersBuilder
                .add("email", emailPresenteNaBaseDeDados)
                .add("nome", nomePresenteNaBaseDeDados)
                .add("password", passwordPresenteNaBaseDeDados)
                .add("estado", estadoPresenteNaBaseDeDados)
                .add("fotoPerfil", fotoPerfilPresenteNaBaseDeDados)
                .add("fotoFundo", fotoFundoPresenteNaBaseDeDados)
                .add("resumo", resumoPresenteNaBaseDeDados)
                .add("exp", expPresenteNaBaseDeDados)
                .add("idArea", idAreaPresenteNaBaseDeDados)
                .add("orcidId", orcidIdPresenteNaBaseDeDados).build();

        jsonArrayBuilder.add(usersJson);
    }


}