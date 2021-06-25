package app.views;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.Integer.parseInt;


@WebServlet("/alunos/*")
public class AlunosApi extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println(req.toString());
        String[] split = req.toString().split("/", 4);


        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");

        Conection con = new Conection();
        System.out.println("This is the Connection: " + con);

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        System.out.println("antes do numero aluno");


            if(split.length == 4){
                String[] split3 = split[3].split(" ", 2);
                String texto = split3[0];
                if(!texto.contains("@"))
                {
                    System.out.println("É um numero: " + texto);
                    int numeroAluno = parseInt(texto);
                    String sql = "SELECT * FROM alunos WHERE numeroDeAluno = " + numeroAluno;
                    ResultSet rs = con.selectSQL(sql);
                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                    try {
                        boolean existe = false;
                        while(rs.next()){
                            showUser(rs, jsonArrayBuilder);
                            jsonBuilder.add("alunos", jsonArrayBuilder);
                            existe = true;
                        }
                        if(!existe){
                            jsonBuilder.add("info", "Numero inexistente!");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }else{
                    System.out.println("Não é um numero: " + texto);
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

        System.out.println("Entrou no inserir");
        String password = body.getString("password");
        String nome = body.getString("nome");
        String emailFixed = new String(email.getBytes(fromCharset), toCharset);
        String passwordFixed = new String(password.getBytes(fromCharset), toCharset);
        String nomeFixed = new String(nome.getBytes(fromCharset), toCharset);
        int numeroDeAluno = body.getInt("numeroDeAluno");
        registar(emailFixed, passwordFixed, nomeFixed, numeroDeAluno, con, jsonBuilder);


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
        if(!body.containsKey("password") && !body.containsKey("nome") && !body.containsKey("numeroDeAluno"))
        {
            alterarBoolean(emailFixed, con, jsonBuilder, "estado", true);
        }else {
            if(body.containsKey("password") && body.containsKey("oldPassword")) {
                String password = body.getString("password");
                String oldPassword = body.getString("oldPassword");
                String passwordFixed = new String(password.getBytes(fromCharset), toCharset);
                String oldPasswordFixed = new String(oldPassword.getBytes(fromCharset), toCharset);
                alterarPassword(jsonBuilder, emailFixed, passwordFixed, oldPasswordFixed, con);
            }
            if(body.containsKey("nome")){
                String nome = body.getString("nome");
                String nomeFixed = new String(nome.getBytes(fromCharset), toCharset);
                alterarString(jsonBuilder, emailFixed, nomeFixed, con, "nome");
            }
            if(body.containsKey("numeroDeAluno")){
                int numeroDeAluno = body.getInt("numeroDeAluno");
                alterarInt(jsonBuilder, emailFixed, numeroDeAluno, con, "numeroDeAluno");
            }
        }

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();


    }


    private void alterarPassword(JsonObjectBuilder jsonBuilder, String email, String password, String oldPassword, Conection con) {
        System.out.println("Entrou no menu para alterar a password de 1 aluno");
        String sql = "SELECT * FROM alunos WHERE email = '" + email + "' and password = '" + oldPassword + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE alunos SET password = '" + password + "' WHERE email = '" + email + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", "Password alterada com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Nao existe nenhum aluno com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void alterarString(JsonObjectBuilder jsonBuilder, String email, String string, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 professor");
        String sql = "SELECT * FROM alunos WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE alunos SET " + campo + " = '" + string + "' WHERE email = '" + email + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Nao existe nenhum aluno com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void alterarInt(JsonObjectBuilder jsonBuilder, String email, int integer, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar a foto de perfil de 1 user");
        String sql = "SELECT * FROM alunos WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE alunos SET " + campo + " = " + integer + " WHERE email = '" + email + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Nao existe nenhum aluno com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registar(String email, String password, String nome, int numeroDeAluno, Conection con, JsonObjectBuilder jsonBuilder) {
        //Inserir um user
        System.out.println("Entrou no menu para registar conta");
        String sql = "SELECT * FROM alunos WHERE email = '" + email + "' or numeroDeAluno = " + numeroDeAluno;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(!rs.next()){
                sql = "INSERT INTO alunos (email, password, nome, numeroDeAluno, estado) values " +
                        "('" + email + "', '" + password + "', '" + nome + "', '" + numeroDeAluno + "', default)";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", "Aluno registado com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Aluno existente!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private void alterarBoolean(String email, Conection con, JsonObjectBuilder jsonBuilder, String campo, boolean valor) {
        //alterar um user
        System.out.println("Entrou no menu para alterar conta");
        String sql = "UPDATE alunos SET " + campo  + " = " + valor + " WHERE email = '" + email + "'";
        int res = con.executeSQL(sql);
        if (res > 0) {
            jsonBuilder.add("info", "Aluno alterado com sucesso!");
        } else {
            jsonBuilder.add("info", "Aconteceu algum erro");
        }
    }

    private void mostrarTodosOsUsers(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os alunos");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT * FROM alunos";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showUser(rs, jsonArrayBuilder);

                existe = true;

            }
            jsonBuilder.add("alunos", jsonArrayBuilder);

            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhum aluno!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarUmUser(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar 1 aluno");
        String[] split3 = s.split(" ", 2);
        String email = split3[0];
        String sql = "SELECT * FROM alunos WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showUser(rs, jsonArrayBuilder);

                jsonBuilder.add("alunos", jsonArrayBuilder);
                existe = true;
            }
            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhum aluno com esse email!");
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
        String numeroDeAlunoPresenteNaBaseDeDados = rs.getString("numeroDeAluno");


        JsonObjectBuilder usersBuilder = Json.createObjectBuilder();
        JsonObject usersJson = usersBuilder
                .add("email", emailPresenteNaBaseDeDados)
                .add("nome", nomePresenteNaBaseDeDados)
                .add("password", passwordPresenteNaBaseDeDados)
                .add("estado", estadoPresenteNaBaseDeDados)
                .add("numeroDeAluno", numeroDeAlunoPresenteNaBaseDeDados).build();

        jsonArrayBuilder.add(usersJson);
    }


}