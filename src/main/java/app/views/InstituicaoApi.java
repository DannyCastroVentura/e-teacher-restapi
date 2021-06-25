package app.views;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.Integer.parseInt;

@WebServlet("/instituicao/*")
public class InstituicaoApi extends HttpServlet {


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

        if(split.length == 4){

            mostrarUmaInstituicao(split[3], con, jsonBuilder, false);


        }else{
            mostrarTodasAsInstituicoes(con, jsonBuilder, false);
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

        String email = body.getString("email");

        System.out.println("Entrou no inserir");
        String password = body.getString("password");
        String nome = body.getString("nome");
        int numeroDeAluno = body.getInt("numeroDeAluno");
        registar(email, password, nome, numeroDeAluno, con, jsonBuilder);


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
        String email = body.getString("email");
        if(!body.containsKey("fotoPerfil") && !body.containsKey("fotoFundo") && !body.containsKey("password") && !body.containsKey("nome") && !body.containsKey("resumo") && !body.containsKey("exp") && !body.containsKey("idArea"))
        {
            alterarBoolean(email, con, jsonBuilder, "estado", true);
        }else {
            if(body.containsKey("fotoPerfil")) {
                String fotoPerfil = body.getString("fotoPerfil");
                alterarString(jsonBuilder, email, fotoPerfil, con, "fotoPerfil");
            }
            if(body.containsKey("fotoFundo")){
                String fotoFundo = body.getString("fotoFundo");
                alterarString(jsonBuilder, email, fotoFundo, con, "fotoFundo");
            }
            if(body.containsKey("password")){
                String password = body.getString("password");
                alterarString(jsonBuilder, email, password, con, "password");
            }
            if(body.containsKey("nome")){
                String nome = body.getString("nome");
                alterarString(jsonBuilder, email, nome, con, "nome");
            }
            if(body.containsKey("resumo")){
                String resumo = body.getString("resumo");
                alterarString(jsonBuilder, email, resumo, con, "resumo");
            }
            if(body.containsKey("exp")){
                String exp = body.getString("exp");
                alterarString(jsonBuilder, email, exp, con, "exp");
            }
            if(body.containsKey("idArea")){
                int idArea = body.getInt("idArea");
                alterarInt(jsonBuilder, email, idArea, con, "idArea");
            }
        }

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();


    }


    private void alterarString(JsonObjectBuilder jsonBuilder, String email, String string, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 professor");
        String sql = "SELECT * FROM professores WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE professores SET " + campo + " = '" + string + "' WHERE email = '" + email + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Nao existe nenhum professor com esse email!");
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
                jsonBuilder.add("info", "Nao existe nenhum professor com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registar(String email, String password, String nome, int numeroDeAluno, Conection con, JsonObjectBuilder jsonBuilder) {
        //Inserir um user
        System.out.println("Entrou no menu para registar conta");
        String sql = "INSERT INTO alunos (email, password, nome, numeroDeAluno, estado) values " +
                "('" + email + "', '" + password + "', '" + nome + "', '" + numeroDeAluno + "', default)";
        int res = con.executeSQL(sql);
        if (res > 0) {
            jsonBuilder.add("info", "Aluno registado com sucesso!");
        } else {
            jsonBuilder.add("info", "Aconteceu algum erro");
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

    private void mostrarTodasAsInstituicoes(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todas os instituicoes");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT * FROM instituicao order by nome";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showInstituicao(rs, jsonArrayBuilder);

                existe = true;

            }
            jsonBuilder.add("instituicao", jsonArrayBuilder);

            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhuma instituicao!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarUmaInstituicao(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar 1 instituicao");
        String[] split3 = s.split(" ", 2);
        int id = parseInt(split3[0]);
        String sql = "SELECT * FROM instituicao WHERE idInstituicao = " + id;
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showInstituicao(rs, jsonArrayBuilder);

                jsonBuilder.add("instituicao", jsonArrayBuilder);
                existe = true;
            }
            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhuma instituicao com essa sigla!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showInstituicao(ResultSet rs, JsonArrayBuilder jsonArrayBuilder) throws SQLException {
        String idInstituicaoPresenteNaBaseDeDados = rs.getString("idInstituicao");
        String siglaPresenteNaBaseDeDados = rs.getString("sigla");
        String nomePresenteNaBaseDeDados = rs.getString("nome");


        JsonObjectBuilder usersBuilder = Json.createObjectBuilder();
        JsonObject usersJson = usersBuilder
                .add("idInstituicao", idInstituicaoPresenteNaBaseDeDados)
                .add("sigla", siglaPresenteNaBaseDeDados)
                .add("nome", nomePresenteNaBaseDeDados).build();

        jsonArrayBuilder.add(usersJson);
    }
}
