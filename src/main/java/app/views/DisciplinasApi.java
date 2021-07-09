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

@WebServlet("/disciplinas/*")
public class DisciplinasApi extends HttpServlet {


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

            mostrarUmaDisciplina(split[3], con, jsonBuilder, false);


        }else{
            mostrarTodasAsDisciplinas(con, jsonBuilder, false);
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

        System.out.println("Entrou no inserir");
        String sigla = body.getString("sigla");
        String nome = body.getString("nome");

        String siglaFixed = new String(sigla.getBytes(fromCharset), toCharset);
        String nomeFixed = new String(nome.getBytes(fromCharset), toCharset);

        registar(siglaFixed, nomeFixed, con, jsonBuilder);


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

        int id = body.getInt("id");
        if(body.containsKey("sigla")) {
            String sigla = body.getString("sigla");
            String fixed = new String(sigla.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "sigla");
        }
        if(body.containsKey("nome")){
            String nome = body.getString("nome");
            String fixed = new String(nome.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "nome");
        }

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("chegou ao doDelete");
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonObject body = Json.createReader(req.getReader()).readObject();
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        Conection con = new Conection();


        int id = body.getInt("id");
        apagarDisciplina(jsonBuilder, id, con);

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();

    }


    private void apagarDisciplina(JsonObjectBuilder jsonBuilder, int id, Conection con) {
        System.out.println("Entrou no menu para apagar um disciplina");
        String sql = "DELETE FROM disciplinas WHERE idDisciplinas = " + id;
        int res = con.executeSQL(sql);
        try {
            if (res > 0) {
                jsonBuilder.add("info", " disciplina eliminada com sucesso!");
            } else {
                jsonBuilder.add("info", "disciplina não existente!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alterarString(JsonObjectBuilder jsonBuilder, int id , String string, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 disciplina");
        String sql = "SELECT * FROM disciplinas WHERE idDisciplinas = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE disciplinas SET " + campo + " = '" + string + "' WHERE idDisciplinas = " + id;
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Nao existe nenhuma disciplina com esse id!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void registar(String sigla, String nome, Conection con, JsonObjectBuilder jsonBuilder) {
        //Inserir uma disciplina
        System.out.println("Entrou no menu para registar uma disciplina");
        String sql = "INSERT INTO disciplinas (sigla, nome) values " +
                "('" + sigla + "', '" + nome + "')";
        int res = con.executeSQL(sql);
        if (res > 0) {
            jsonBuilder.add("info", "Disciplina registada com sucesso!");
        } else {
            jsonBuilder.add("info", "Aconteceu algum erro");
        }
    }


    private void mostrarTodasAsDisciplinas(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todas as disciplinas");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT * FROM disciplinas order by nome";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showDisciplinas(rs, jsonArrayBuilder);

                existe = true;

            }
            jsonBuilder.add("disciplinas", jsonArrayBuilder);

            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhuma disciplina!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarUmaDisciplina(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar 1 instituicao");
        String[] split3 = s.split(" ", 2);
        int id = parseInt(split3[0]);
        String sql = "SELECT * FROM disciplinas WHERE idDisciplinas = " + id;
        System.out.println(sql);
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showDisciplinas(rs, jsonArrayBuilder);

                jsonBuilder.add("disciplinas", jsonArrayBuilder);
                existe = true;
            }
            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhuma disciplina com essa sigla!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showDisciplinas(ResultSet rs, JsonArrayBuilder jsonArrayBuilder) throws SQLException {
        String idDisciplinasPresenteNaBaseDeDados = rs.getString("idDisciplinas");
        String siglaPresenteNaBaseDeDados = rs.getString("sigla");
        String nomePresenteNaBaseDeDados = rs.getString("nome");


        JsonObjectBuilder usersBuilder = Json.createObjectBuilder();
        JsonObject usersJson = usersBuilder
                .add("idDisciplinas", idDisciplinasPresenteNaBaseDeDados)
                .add("sigla", siglaPresenteNaBaseDeDados)
                .add("nome", nomePresenteNaBaseDeDados).build();

        jsonArrayBuilder.add(usersJson);
    }
}
