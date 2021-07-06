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

@WebServlet("/areas/*")
public class AreaApi extends HttpServlet {

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
            mostrarUmaArea(split[3], con, jsonBuilder, false);
        }else{
            mostrarTodasAsAreas(con, jsonBuilder, false);
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

        System.out.println("Entrou no inserir");
        String nome = body.getString("nome");
        String cor = body.getString("cor");
        adicionarArea(nome, cor, con, jsonBuilder);

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
        System.out.println("chegou ao alterar area");
        int id = body.getInt("id");
        if(body.containsKey("cor")) {
            String cor = body.getString("cor");
            String fixed = new String(cor.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "cor");
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
        apagarArea(jsonBuilder, id, con);

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();

    }

    private void apagarArea(JsonObjectBuilder jsonBuilder, int id, Conection con) {
        System.out.println("Entrou no menu para apagar uma area");
        String sql = "DELETE FROM areas WHERE idArea = " + id;
        int res = con.executeSQL(sql);
        try {
            if (res > 0) {
                jsonBuilder.add("info", " area eliminada com sucesso!");
            } else {
                jsonBuilder.add("info", "area não existente!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void alterarString(JsonObjectBuilder jsonBuilder, int id, String string, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 professor");
        String sql = "SELECT * FROM areas WHERE idArea = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE areas SET " + campo + " = '" + string + "' WHERE idArea = " + id;
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhuma area com esse id!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void adicionarArea(String nome, String cor, Conection con, JsonObjectBuilder jsonBuilder) {
        //Inserir uma area
        System.out.println("Entrou no menu para registar conta");
        String sql = "INSERT INTO areas (nome, cor) values " +
                "('" + nome + "', '" + cor + "')";
        int res = con.executeSQL(sql);
        if (res > 0) {
            jsonBuilder.add("info", "Area registada com sucesso!");
        } else {
            jsonBuilder.add("info", "Aconteceu algum erro");
        }
    }


    private void mostrarTodasAsAreas(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos as areas");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT * FROM areas";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showUser(rs, jsonArrayBuilder);

                existe = true;

            }
            jsonBuilder.add("areas", jsonArrayBuilder);

            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhum area!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarUmaArea(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar 1 area");
        String[] split3 = s.split(" ", 2);
        String idArea = split3[0];
        String sql = "SELECT * FROM areas WHERE idArea = '" + idArea + "'";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showUser(rs, jsonArrayBuilder);

                jsonBuilder.add("areas", jsonArrayBuilder);
                existe = true;
            }
            if(!existe){
                jsonBuilder.add("info", "Não existe nenhuma area com esse nome!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showUser(ResultSet rs, JsonArrayBuilder jsonArrayBuilder) throws SQLException {
        String idAreaPresenteNaBaseDeDados = rs.getString("idArea");
        String nomePresenteNaBaseDeDados = rs.getString("nome");
        String corPresenteNaBaseDeDados = rs.getString("cor");


        JsonObjectBuilder usersBuilder = Json.createObjectBuilder();
        JsonObject usersJson = usersBuilder
                .add("idArea", idAreaPresenteNaBaseDeDados)
                .add("nome", nomePresenteNaBaseDeDados)
                .add("cor", corPresenteNaBaseDeDados).build();

        jsonArrayBuilder.add(usersJson);
    }


}