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

@WebServlet("/recursosDigitais/*")
public class RecursosDigitaisApi extends HttpServlet {

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
            mostrarTodosOsRecursosDigitaisDeUmProfessor(split[3], con, jsonBuilder, false);
        }else{
            mostrarTodosOsRecursosDigitaisDeTodosOsProfessores(con, jsonBuilder, false);
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
        String descricao = body.getString("descricao");
        String url = body.getString("url");

        String emailFixed = new String(email.getBytes(fromCharset), toCharset);
        String descricaoFixed = new String(descricao.getBytes(fromCharset), toCharset);
        String urlFixed = new String(url.getBytes(fromCharset), toCharset);
        adicionarRecursoDigital(jsonBuilder, emailFixed, descricaoFixed, urlFixed, con);


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
        int id = body.getInt("id");

        final Charset fromCharset = Charset.forName("windows-1252");
        final Charset toCharset = StandardCharsets.UTF_8;

        if(body.containsKey("descricao")) {
            String descricao = body.getString("descricao");
            String fixed = new String(descricao.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "descricao");
        }
        if(body.containsKey("url")){
            String url = body.getString("url");
            String fixed = new String(url.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "url");
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
        int id = body.getInt("id");
        eliminarRecursoDigital(id, con, jsonBuilder);
        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();


    }



    private void eliminarRecursoDigital(int id, Conection con, JsonObjectBuilder jsonBuilder) {
        System.out.println("Entrou no menu para eliminar o recursosDigitais " + id);
        String sql = "SELECT * FROM recursosDigitais WHERE id = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "DELETE FROM recursosDigitais WHERE id = " + id;
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", "recursosDigitais " + id + " eliminado com sucesso!");
                } else {
                    jsonBuilder.add("info", "recursosDigitais não existente no professor");
                }

            }else{
                jsonBuilder.add("info", "Não existe nenhum recursosDigitais com esse id!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void alterarString(JsonObjectBuilder jsonBuilder, int id, String string, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 projeto de investigacao");
        String sql = "SELECT * FROM recursosDigitais WHERE id = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE recursosDigitais SET " + campo + " = '" + string + "' WHERE id = " + id;
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum recurso digital com esse id!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void adicionarRecursoDigital(JsonObjectBuilder jsonBuilder, String professorEmail, String descricao, String url, Conection con) {
        System.out.println("Entrou no menu para adicionar um recurso digital a um professor");
            String sql = "INSERT INTO recursosDigitais (email, descricao, url) values " +
                    "('" + professorEmail + "', '" + descricao + "', '" + url + "')";
            int res = con.executeSQL(sql);
            if (res > 0) {
                jsonBuilder.add("info", "Recurso digital inserido no professor com sucesso!");
            } else {
                jsonBuilder.add("info", "Aconteceu algum erro");
            }

    }


    private void mostrarTodosOsRecursosDigitaisDeTodosOsProfessores(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os trabalhos de todos os professores");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT email, nome FROM professores";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder professoresBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showProjetosInvestigacaoDeProfessores(rs, professoresBuilder, con);

                existe = true;

            }
            jsonBuilder.add("professores", professoresBuilder);

            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhum professor!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarTodosOsRecursosDigitaisDeUmProfessor(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os trabalhos de um professor");
        System.out.println("SQL: SELECT * FROM professores WHERE email = ' + email + '" );
        String[] split3 = s.split(" ", 2);
        String email = split3[0];
        String sql = "SELECT * FROM professores WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder professoresBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showProjetosInvestigacaoDeProfessores(rs, professoresBuilder, con);

                jsonBuilder.add("professores", professoresBuilder);
                existe = true;
            }
            if(!existe){
                jsonBuilder.add("info", "Não existe nenhum professor com esse email!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showProjetosInvestigacaoDeProfessores(ResultSet rs, JsonArrayBuilder professoresBuilder, Conection con) throws SQLException {
        String emailDoProfessorPresenteNaBaseDeDados = rs.getString("email");
        String nomeDoProfessorPresenteNaBaseDeDados = rs.getString("nome");

        String sql = "select * from recursosDigitais where email = '" + emailDoProfessorPresenteNaBaseDeDados + "'";
        ResultSet rs2 = con.selectSQL(sql);
        JsonObjectBuilder professoresArrayBuilder = Json.createObjectBuilder();
        JsonArrayBuilder RecursosDigitaisDoProfessorArray = Json.createArrayBuilder();
        JsonObjectBuilder RecursosDigitaisArrayBuilder = Json.createObjectBuilder();
        try{
            while(rs2.next()){
                int idRecursosDigitaisPresenteNaBaseDeDados = rs2.getInt("id");
                String descricaoPresenteNaBaseDeDados = rs2.getString("descricao");
                String urlPresenteNaBaseDeDados = rs2.getString("url");


                JsonObject RecursosDigitaisDoProfessorJson = RecursosDigitaisArrayBuilder
                        .add("id", idRecursosDigitaisPresenteNaBaseDeDados)
                        .add("descricao", descricaoPresenteNaBaseDeDados)
                        .add("url", urlPresenteNaBaseDeDados).build();

                RecursosDigitaisDoProfessorArray.add(RecursosDigitaisDoProfessorJson);


            }


        }catch (SQLException throwables){
            throwables.printStackTrace();
        }


        JsonObject usersJson = professoresArrayBuilder
                .add("email", emailDoProfessorPresenteNaBaseDeDados)
                .add("nome", nomeDoProfessorPresenteNaBaseDeDados)
                .add("recursosDigitais", RecursosDigitaisDoProfessorArray).build();



        professoresBuilder.add(usersJson);

    }


}