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

@WebServlet("/projetosInvestigacao/*")
public class ProjetosInvestigacaoApi extends HttpServlet {

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
            mostrarTodosOsProjetosDeInvestigacaoDeUmProfessor(split[3], con, jsonBuilder, false);
        }else{
            mostrarTodosOsProjetosDeInvestigacaoDeTodosOsProfessores(con, jsonBuilder, false);
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

        if(body.containsKey("email") && body.containsKey("titulo") && body.containsKey("sigla") && body.containsKey("investigadorPrincipal") && body.containsKey("financiador") && body.containsKey("dataInicio")){
            String email = body.getString("email");
            String titulo = body.getString("titulo");
            String sigla = body.getString("sigla");
            String investigadorPrincipal = body.getString("investigadorPrincipal");
            String financiador = body.getString("financiador");
            int dataInicio = body.getInt("dataInicio");
            int dataFim = 0;
            if(body.containsKey("dataFim")){
                dataFim = body.getInt("dataFim");
            }

            String emailFixed = new String(email.getBytes(fromCharset), toCharset);
            String tituloFixed = new String(titulo.getBytes(fromCharset), toCharset);
            String siglaFixed = new String(sigla.getBytes(fromCharset), toCharset);
            String investigadorPrincipalFixed = new String(investigadorPrincipal.getBytes(fromCharset), toCharset);
            String financiadorFixed = new String(financiador.getBytes(fromCharset), toCharset);
            adicionarProjetoInvestigacao(jsonBuilder, emailFixed, tituloFixed, siglaFixed, investigadorPrincipalFixed, financiadorFixed, dataInicio, dataFim, con);
        }

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

        if(body.containsKey("titulo")) {
            String titulo = body.getString("titulo");
            String fixed = new String(titulo.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "titulo");
        }
        if(body.containsKey("sigla")){
            String sigla = body.getString("sigla");
            String fixed = new String(sigla.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "sigla");
        }
        if(body.containsKey("investigadorPrincipal")){
            String investigadorPrincipal = body.getString("investigadorPrincipal");
            String fixed = new String(investigadorPrincipal.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "investigadorPrincipal");
        }
        if(body.containsKey("financiador")){
            String financiador = body.getString("financiador");
            String fixed = new String(financiador.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "financiador");
        }
        if(body.containsKey("dataInicio")){
            int dataInicio = body.getInt("dataInicio");
            alterarInt(jsonBuilder, id, dataInicio, con, "dataInicio");
        }
        if(body.containsKey("dataFim")){
            int dataFim = body.getInt("dataFim");
            alterarInt(jsonBuilder, id, dataFim, con, "dataFim");
        }
        if(body.containsKey("link")){
            String link = body.getString("link");
            String fixed = new String(link.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "link");
        }
        if(body.containsKey("imagem")){
            String imagem = body.getString("imagem");
            String fixed = new String(imagem.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "imagem");
        }
        if(body.containsKey("resumo")){
            String resumo = body.getString("resumo");
            String fixed = new String(resumo.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, fixed, con, "resumo");
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
        if(body.containsKey("id") && body.containsKey("email")){
            int id = body.getInt("id");
            String professorEmail = body.getString("email");
            eliminarTrabalho(id, professorEmail, con, jsonBuilder);
        }
        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();


    }



    private void eliminarTrabalho(int id, String email, Conection con, JsonObjectBuilder jsonBuilder) {
        System.out.println("Entrou no menu para eliminar o ProjetosInvestigacao " + id);
        String sql = "SELECT * FROM ProjetosInvestigacao WHERE id = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "DELETE FROM ProjetosInvestigacao WHERE email = '" + email + "' and id = " + id;
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", "ProjetosInvestigacao " + id + " eliminado com sucesso!");
                } else {
                    jsonBuilder.add("info", "ProjetosInvestigacao não existente no professor");
                }

            }else{
                jsonBuilder.add("info", "Não existe nenhum ProjetosInvestigacao com esse id!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void alterarString(JsonObjectBuilder jsonBuilder, int id, String string, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 projeto de investigacao");
        String sql = "SELECT * FROM ProjetosInvestigacao WHERE id = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE ProjetosInvestigacao SET " + campo + " = '" + string + "' WHERE id = " + id;
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum projeto de investigacao com esse id!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alterarInt(JsonObjectBuilder jsonBuilder, int id, int f, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 trabalho");
        String sql = "SELECT * FROM ProjetosInvestigacao WHERE id = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE ProjetosInvestigacao SET " + campo + " = " + f + " WHERE id = " + id;
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum projeto de investigacao com esse id!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void adicionarProjetoInvestigacao(JsonObjectBuilder jsonBuilder, String professorEmail, String titulo, String sigla, String investigadorPrincipal, String financiador, int dataInicio, int dataFim, Conection con) {
        System.out.println("Entrou no menu para adicionar um trabalho a um professor");
        String sql = "SELECT * FROM professores WHERE email = '" + professorEmail + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "INSERT INTO ProjetosInvestigacao (email, titulo, sigla, investigadorPrincipal, financiador, dataInicio, dataFim) values " +
                        "('" + professorEmail + "', '" + titulo + "', '" + sigla + "', '" + investigadorPrincipal + "', '" + financiador + "', " + dataInicio + ", " + dataFim + ")";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", "Projeto de investigação inserido no professor com sucesso!");
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


    private void mostrarTodosOsProjetosDeInvestigacaoDeTodosOsProfessores(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os trabalhos de todos os professores");
        System.out.println("This is the Connection: " + con);
        System.out.println("SQL: SELECT email, nome FROM professores" );
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



    private void mostrarTodosOsProjetosDeInvestigacaoDeUmProfessor(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
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

        String sql = "select * from ProjetosInvestigacao where email = '" + emailDoProfessorPresenteNaBaseDeDados + "'";
        ResultSet rs2 = con.selectSQL(sql);
        JsonObjectBuilder professoresArrayBuilder = Json.createObjectBuilder();
        JsonArrayBuilder ProjetosInvestigacaoDoProfessorArray = Json.createArrayBuilder();
        JsonObjectBuilder ProjetosInvestigacaoArrayBuilder = Json.createObjectBuilder();
        try{
            while(rs2.next()){
                int idProjetoInvestigacaoDoTrabalhoPresenteNaBaseDeDados = rs2.getInt("id");
                String tituloDoProjetoinvestigacaoPresenteNaBaseDeDados = rs2.getString("titulo");
                String siglaDoProjetoinvestigacaoPresenteNaBaseDeDados = rs2.getString("sigla");
                String investigadorPrincipalDoProjetoInvestigacaoPresenteNaBaseDeDados = rs2.getString("investigadorPrincipal");
                String financiadorDoProjetoInvestigacaoPresenteNaBaseDeDados = rs2.getString("financiador");
                int anoInicioDoProjetoInvestigacaoPresenteNaBaseDeDados = rs2.getInt("dataInicio");
                int anoFimDoProjetoInvestigacaoPresenteNaBaseDeDados = rs2.getInt("dataFim");
                String linkDoProjetoInvestigacaoPresenteNaBaseDeDados = rs2.getString("link");
                String imagemDoProjetoInvestigacaoPresenteNaBaseDeDados = rs2.getString("imagem");
                String resumoDoProjetoInvestigacaoPresenteNaBaseDeDados = rs2.getString("resumo");
                if(imagemDoProjetoInvestigacaoPresenteNaBaseDeDados == null){
                    imagemDoProjetoInvestigacaoPresenteNaBaseDeDados = "https://www.blog.ipv7.com.br/wp-content/uploads/2018/08/Projeto.jpg";
                }
                if(linkDoProjetoInvestigacaoPresenteNaBaseDeDados == null){
                    linkDoProjetoInvestigacaoPresenteNaBaseDeDados = "";
                }
                if(resumoDoProjetoInvestigacaoPresenteNaBaseDeDados == null){
                    resumoDoProjetoInvestigacaoPresenteNaBaseDeDados = "";
                }


                JsonObject ProjetosInvestigacaoDoProfessorJson = ProjetosInvestigacaoArrayBuilder
                        .add("id", idProjetoInvestigacaoDoTrabalhoPresenteNaBaseDeDados)
                        .add("titulo", tituloDoProjetoinvestigacaoPresenteNaBaseDeDados)
                        .add("sigla", siglaDoProjetoinvestigacaoPresenteNaBaseDeDados)
                        .add("investigadorPrincipal", investigadorPrincipalDoProjetoInvestigacaoPresenteNaBaseDeDados)
                        .add("financiador", financiadorDoProjetoInvestigacaoPresenteNaBaseDeDados)
                        .add("dataInicio", anoInicioDoProjetoInvestigacaoPresenteNaBaseDeDados)
                        .add("dataFim", anoFimDoProjetoInvestigacaoPresenteNaBaseDeDados)
                        .add("link", linkDoProjetoInvestigacaoPresenteNaBaseDeDados)
                        .add("imagem", imagemDoProjetoInvestigacaoPresenteNaBaseDeDados)
                        .add("resumo", resumoDoProjetoInvestigacaoPresenteNaBaseDeDados).build();

                ProjetosInvestigacaoDoProfessorArray.add(ProjetosInvestigacaoDoProfessorJson);


            }


        }catch (SQLException throwables){
            throwables.printStackTrace();
        }


        JsonObject usersJson = professoresArrayBuilder
                .add("email", emailDoProfessorPresenteNaBaseDeDados)
                .add("nome", nomeDoProfessorPresenteNaBaseDeDados)
                .add("projetosInvestigacao", ProjetosInvestigacaoDoProfessorArray).build();



        professoresBuilder.add(usersJson);

    }


}