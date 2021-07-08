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

@WebServlet("/logsAdmin/*")
public class LogsAdminApi extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println(req.toString());
        String[] split = req.toString().split("/", 5);

        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");

        Conection con = new Conection();
        System.out.println("This is the Connection: " + con);

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        if(split.length > 3){
            if(split.length > 4){
                mostrarTodosOsLogsDeTodosOsAdminsDeFormaCronologica(con, jsonBuilder);
            }else{
                mostrarTodosOsLogsDeUmAdmin(split[3], con, jsonBuilder, false);
            }
        }else{
            mostrarTodosOsLogsDeTodosOsAdmins(con, jsonBuilder, false);
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
        String tipoDeAlteracao = body.getString("tipoDeAlteracao");
        String tabela = body.getString("tabela");
        String idRegisto = body.getString("idRegisto");
        String estadoFinal = body.getString("estadoFinal");

        System.out.println(idRegisto + " " + estadoFinal);

        String emailFixed = new String(email.getBytes(fromCharset), toCharset);
        String tipoDeAlteracaoFixed = new String(tipoDeAlteracao.getBytes(fromCharset), toCharset);
        String tabelaFixed = new String(tabela.getBytes(fromCharset), toCharset);
        String idRegistoFixed = new String(idRegisto.getBytes(fromCharset), toCharset);
        String estadoFinalFixed = new String(estadoFinal.getBytes(fromCharset), toCharset);

        System.out.println(idRegistoFixed + " " + estadoFinalFixed);

        adicionarLogAdmin(jsonBuilder, emailFixed, tipoDeAlteracaoFixed, tabelaFixed, idRegistoFixed, estadoFinalFixed, con);


        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();

    }




    private void adicionarLogAdmin(JsonObjectBuilder jsonBuilder, String adminEmail, String tipoDeAlteracao, String tabela, String Registo, String estadoFinal, Conection con) {
        System.out.println("Entrou no menu para adicionar um log a um admin");
        String sql = "INSERT INTO logsAdmin (email, tipoDeAlteracao, tabela, idRegisto, estadoFinal) values " +
                "('" + adminEmail + "', '" + tipoDeAlteracao + "', '" + tabela + "', '" + Registo + "', '" + estadoFinal + "')";
        int res = con.executeSQL(sql);
        if (res > 0) {
            jsonBuilder.add("info", "log inserido no admin com sucesso!");
        } else {
            jsonBuilder.add("info", "Aconteceu algum erro");
        }

    }


    private void mostrarTodosOsLogsDeTodosOsAdmins(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os trabalhos de todos os professores");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT email, nome FROM admin";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder professoresBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showProjetosInvestigacaoDeProfessores(rs, professoresBuilder, con);

                existe = true;

            }
            jsonBuilder.add("admin", professoresBuilder);

            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhum admin!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarTodosOsLogsDeUmAdmin(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os trabalhos de um professor");
        String[] split3 = s.split(" ", 2);
        String email = split3[0];
        String sql = "SELECT * FROM admin WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder professoresBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showProjetosInvestigacaoDeProfessores(rs, professoresBuilder, con);

                jsonBuilder.add("admin", professoresBuilder);
                existe = true;
            }
            if(!existe){
                jsonBuilder.add("info", "Não existe nenhum admin com esse email!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showProjetosInvestigacaoDeProfessores(ResultSet rs, JsonArrayBuilder adminBuilder, Conection con) throws SQLException {
        String emailDoAdminPresenteNaBaseDeDados = rs.getString("email");
        String nomeDoAdminPresenteNaBaseDeDados = rs.getString("nome");

        String sql = "select * from logsAdmin where email = '" + emailDoAdminPresenteNaBaseDeDados + "'";
        ResultSet rs2 = con.selectSQL(sql);
        JsonObjectBuilder adminArrayBuilder = Json.createObjectBuilder();
        JsonArrayBuilder logsDoAdminArray = Json.createArrayBuilder();
        JsonObjectBuilder LogsArrayBuilder = Json.createObjectBuilder();
        try{
            while(rs2.next()){
                int idLogPresenteNaBaseDeDados = rs2.getInt("idLog");
                String tabelaPresenteNaBaseDeDados = rs2.getString("tabela");
                String estadoFinalPresenteNaBaseDeDados = rs2.getString("estadoFinal");
                String idRegistoPresenteNaBaseDeDados = rs2.getString("idRegisto");
                String emailPresenteNaBaseDeDados = rs2.getString("email");
                String dateTimePresenteNaBaseDeDados = rs2.getString("dateTime");


                JsonObject LogDoAdminJson = LogsArrayBuilder
                        .add("idLog", idLogPresenteNaBaseDeDados)
                        .add("tabela", tabelaPresenteNaBaseDeDados)
                        .add("estadoFinal", estadoFinalPresenteNaBaseDeDados)
                        .add("idRegisto", idRegistoPresenteNaBaseDeDados)
                        .add("email", emailPresenteNaBaseDeDados)
                        .add("dateTime", dateTimePresenteNaBaseDeDados).build();

                logsDoAdminArray.add(LogDoAdminJson);


            }


        }catch (SQLException throwables){
            throwables.printStackTrace();
        }


        JsonObject usersJson = adminArrayBuilder
                .add("email", emailDoAdminPresenteNaBaseDeDados)
                .add("nome", nomeDoAdminPresenteNaBaseDeDados)
                .add("logs", logsDoAdminArray).build();



        adminBuilder.add(usersJson);

    }

    private void mostrarTodosOsLogsDeTodosOsAdminsDeFormaCronologica(Conection con, JsonObjectBuilder jsonBuilder){
        System.out.println("Entrou no menu para mostrar todos os logs de todos os admins de forma cronologica");
        String sql = "SELECT * FROM logsAdmin order by idLog DESC";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder LogsDoAdminArrayBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                int idLogPresenteNaBaseDeDados = rs.getInt("idLog");
                String tipoDeAlteracaoPresenteNaBaseDeDados = rs.getString("tipoDeAlteracao");
                String tabelaPresenteNaBaseDeDados = rs.getString("tabela");
                String idRegistoPresenteNaBaseDeDados = rs.getString("idRegisto");
                String estadoFinalPresenteNaBaseDeDados = rs.getString("estadoFinal");
                String emailPresenteNaBaseDeDados = rs.getString("email");
                String dateTimePresenteNaBaseDeDados = rs.getString("dateTime");


                JsonObjectBuilder usersBuilder = Json.createObjectBuilder();
                JsonObject usersJson = usersBuilder
                        .add("idLog", idLogPresenteNaBaseDeDados)
                        .add("tipoDeAlteracao", tipoDeAlteracaoPresenteNaBaseDeDados)
                        .add("tabela", tabelaPresenteNaBaseDeDados)
                        .add("idRegisto", idRegistoPresenteNaBaseDeDados)
                        .add("estadoFinal", estadoFinalPresenteNaBaseDeDados)
                        .add("email", emailPresenteNaBaseDeDados)
                        .add("dateTime", dateTimePresenteNaBaseDeDados).build();

                LogsDoAdminArrayBuilder.add(usersJson);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        jsonBuilder.add("logs", LogsDoAdminArrayBuilder);
    }


}