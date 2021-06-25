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

@WebServlet("/orientacoes/*")
public class OrientacoesApi extends HttpServlet {

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
            mostrarTodasAsOrientacoesDeUmProfessor(split[3], con, jsonBuilder, false);
        }else{
            mostrarTodasAsOrientacoesDeTodosOsProfessores(con, jsonBuilder, false);
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
        System.out.println("chegou ao post dos orientacoes");
        if(body.containsKey("email") && body.containsKey("titulo") && body.containsKey("alunoEmail") && body.containsKey("dataInicio") && body.containsKey("idInstituicao")){
            System.out.println("chegou aqui");
            String email = body.getString("email");
            String nomeCurso = body.getString("nomeCurso");
            String tema = body.getString("tema");
            String relatorio = body.getString("relatorio");
            String link = body.getString("link");
            boolean titulo = body.getBoolean("titulo");
            String alunoEmail = body.getString("alunoEmail");
            System.out.println("antes dos int");
            int idInstituicao = body.getInt("idInstituicao");
            int dataInicio = body.getInt("dataInicio");
            int dataFim = 0;
            if(body.containsKey("dataFim")){
                dataFim = body.getInt("dataFim");
            }
            System.out.println("depois dos int");
            String emailFixed = new String(email.getBytes(fromCharset), toCharset);
            String nomeCursoFixed = new String(nomeCurso.getBytes(fromCharset), toCharset);
            String temaFixed = new String(tema.getBytes(fromCharset), toCharset);
            String relatorioFixed = new String(relatorio.getBytes(fromCharset), toCharset);
            String linkFixed = new String(link.getBytes(fromCharset), toCharset);
            String alunoEmailFixed = new String(alunoEmail.getBytes(fromCharset), toCharset);
            adicionarOrientacao(jsonBuilder, emailFixed, titulo, alunoEmailFixed, idInstituicao, dataInicio, dataFim, nomeCursoFixed, temaFixed, relatorioFixed, linkFixed, con);
        }

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();

    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonObject body = Json.createReader(req.getReader()).readObject();

        final Charset fromCharset = Charset.forName("windows-1252");
        final Charset toCharset = StandardCharsets.UTF_8;

        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        Conection con = new Conection();
        int id = body.getInt("id");
        String emailProfessor = body.getString("email");

        if(body.containsKey("titulo")) {
            boolean titulo = body.getBoolean("titulo");
            alterarBoolean(jsonBuilder, id, titulo, con, "titulo", emailProfessor);
        }

        if(body.containsKey("nomeCurso")) {
            String nomeCurso = body.getString("nomeCurso");
            String nomeCursoFixed = new String(nomeCurso.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, nomeCursoFixed, con, "nomeCurso", emailProfessor);
        }

        if(body.containsKey("tema")) {
            String tema = body.getString("tema");
            String temaFixed = new String(tema.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, temaFixed, con, "tema", emailProfessor);
        }

        if(body.containsKey("relatorio")) {
            String relatorio = body.getString("relatorio");
            String relatorioFixed = new String(relatorio.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, relatorioFixed, con, "relatorio", emailProfessor);
        }

        if(body.containsKey("link")) {
            String link = body.getString("link");
            String linkFixed = new String(link.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, id, linkFixed, con, "link", emailProfessor);
        }

        if(body.containsKey("idInstituicao")){
            int idInstituicao = body.getInt("idInstituicao");
            alterarInt(jsonBuilder, id, idInstituicao, con, "idInstituicao" , emailProfessor);
        }
        if(body.containsKey("dataInicio")){
            int dataInicio = body.getInt("dataInicio");
            alterarInt(jsonBuilder, id, dataInicio, con, "dataInicio", emailProfessor);
        }
        if(body.containsKey("dataFim")){
            int dataFim = body.getInt("dataFim");
            alterarInt(jsonBuilder, id, dataFim, con, "dataFim", emailProfessor);
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

        final Charset fromCharset = Charset.forName("windows-1252");
        final Charset toCharset = StandardCharsets.UTF_8;

        Conection con = new Conection();
        if(body.containsKey("id") && body.containsKey("email")){
            int id = body.getInt("id");
            String email = body.getString("email");
            String emailFixed = new String(email.getBytes(fromCharset), toCharset);
            eliminarOrientacao(id, emailFixed, con, jsonBuilder);
        }

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();


    }



    private void eliminarOrientacao(int id, String email, Conection con, JsonObjectBuilder jsonBuilder) {
        System.out.println("Entrou no menu para eliminar o ProjetosInvestigacao " + id);
        String sql = "SELECT * FROM Orientacoes WHERE id = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "DELETE FROM Orientacoes WHERE id = " + id + " and email = '" + email + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", "Orientacao " + id + " eliminado com sucesso!");
                } else {
                    jsonBuilder.add("info", "Não existe nenhuma Orientacao com esse id e com esse emailProfessor!");
                }

            }else{
                jsonBuilder.add("info", "Não existe nenhum Orientacao com esse id!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void alterarBoolean(JsonObjectBuilder jsonBuilder, int id, boolean boleano, Conection con, String campo, String emailProfessor) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 projeto de investigacao");
        String sql = "SELECT * FROM Orientacoes WHERE id = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE Orientacoes SET " + campo + " = " + boleano + " WHERE id = " + id + " and email = '" + emailProfessor + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhuma orientacao com esse id e com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void alterarString(JsonObjectBuilder jsonBuilder, int id, String string, Conection con, String campo, String emailProfessor) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 projeto de investigacao");
        String sql = "SELECT * FROM Orientacoes WHERE id = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE Orientacoes SET " + campo + " = '" + string + "' WHERE id = " + id + " and email = '" + emailProfessor + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhuma orientacao com esse id e com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alterarInt(JsonObjectBuilder jsonBuilder, int id, int f, Conection con, String campo, String emailProfessor) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 trabalho");
        String sql = "SELECT * FROM Orientacoes WHERE id = " + id;
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE Orientacoes SET " + campo + " = " + f + " WHERE id = " + id + " and email = '" + emailProfessor + "'";
                System.out.println(sql);
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Professor não tem essa orientacao");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhuma orientacao com esse id e com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void adicionarOrientacao(JsonObjectBuilder jsonBuilder, String professorEmail, boolean titulo, String alunoEmail, int idInstituicao, int dataInicio, int dataFim, String nomeCurso, String tema, String relatorio, String link, Conection con) {
        System.out.println("Entrou no menu para adicionar uma orientacao a um professor");
        String sql = "SELECT * FROM professores WHERE email = '" + professorEmail + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "INSERT INTO Orientacoes (email, titulo, alunoEmail, dataInicio, dataFim, idInstituicao, nomeCurso, tema, relatorio, link) values " +
                        "('" + professorEmail + "', " + titulo + ", '" + alunoEmail + "', " + dataInicio + ", " + dataFim + ", " + idInstituicao + ", '" + nomeCurso + "',  '" + tema + "',  '" + relatorio + "',  '" + link + "' )";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", "Orientacao inserida no professor com sucesso!");
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


    private void mostrarTodasAsOrientacoesDeTodosOsProfessores(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
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



    private void mostrarTodasAsOrientacoesDeUmProfessor(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os trabalhos de um professor");
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

        String sql = "select distinct p.email, i.sigla, i.nome, i.idInstituicao from Orientacoes inner join instituicao i on Orientacoes.idInstituicao = i.idInstituicao inner join professores p on Orientacoes.email = p.email where p.email = '" + emailDoProfessorPresenteNaBaseDeDados + "'";
        ResultSet rs2 = con.selectSQL(sql);
        JsonObjectBuilder professoresArrayBuilder = Json.createObjectBuilder();
        JsonArrayBuilder OrientacaoDoProfessorArray = Json.createArrayBuilder();
        JsonArrayBuilder InstituicaoDaOrientacaoArray = Json.createArrayBuilder();
        JsonObjectBuilder OrientacaoArrayBuilder = Json.createObjectBuilder();
        JsonObjectBuilder InstituicaoArrayBuilder = Json.createObjectBuilder();
        try{

            while(rs2.next()){
                int idInstituicaoPresenteNaBaseDeDados = rs2.getInt("idinstituicao");
                String nomeDaInstituicaoPresenteNaBaseDeDados = rs2.getString("nome");
                String siglaDaInstituicaoPresenteNaBaseDeDados = rs2.getString("sigla");
                String sql2 = "select o.id as idOrientacoes, o.titulo as tituloOrientacoes, o.nomeCurso as nomeCursoOrientacoes, " +
                        "o.tema as temaOrientacoes, o.relatorio as relatorioOrientacoes, o.link as linkOrientacoes, " +
                        "o.alunoEmail as alunoEmailOrientacoes, o.dataInicio as dataInicioOrientacoes, " +
                        "o.dataFim as dataFimOrientacoes, a.nome as alunoNome, a.numeroDeAluno as alunoNumero " +
                        "from Orientacoes o inner join professores p on o.email = p.email inner join instituicao i on o.idInstituicao = i.idInstituicao inner join alunos a on a.email = o.alunoEmail where i.idInstituicao = " + idInstituicaoPresenteNaBaseDeDados + " and p.email = '" + emailDoProfessorPresenteNaBaseDeDados + "' order by o.idInstituicao";
                ResultSet rs3 = con.selectSQL(sql2);
                while(rs3.next()){
                    int idOrientacaoDoTrabalhoPresenteNaBaseDeDados = rs3.getInt("idOrientacoes");
                    String tituloDaOrientacaoPresenteNaBaseDeDados = rs3.getString("tituloOrientacoes");
                    String nomeCursoDaOrientacaoPresenteNaBaseDeDados = rs3.getString("nomeCursoOrientacoes");
                    String temaDaOrientacaoPresenteNaBaseDeDados = rs3.getString("temaOrientacoes");
                    String relatorioDaOrientacaoPresenteNaBaseDeDados = rs3.getString("relatorioOrientacoes");
                    String linkDaOrientacaoPresenteNaBaseDeDados = rs3.getString("linkOrientacoes");
                    String emailDoAlunoDaOrientacaoPresenteNaBaseDeDados = rs3.getString("alunoEmailOrientacoes");
                    String numeroDoAlunoDaOrientacaoPresenteNaBaseDeDados = rs3.getString("alunoNumero");
                    String nomeDoAlunoDaOrientacaoPresenteNaBaseDeDados = rs3.getString("alunoNome");
                    int dataInicioDaOrientacaoPresenteNaBaseDeDados = rs3.getInt("dataInicioOrientacoes");
                    int dataFimDaOrientacaoPresenteNaBaseDeDados = rs3.getInt("dataFimOrientacoes");

                    JsonObject OrientacaoDoProfessorJson = OrientacaoArrayBuilder
                            .add("id", idOrientacaoDoTrabalhoPresenteNaBaseDeDados)
                            .add("nomeCurso", nomeCursoDaOrientacaoPresenteNaBaseDeDados)
                            .add("tema", temaDaOrientacaoPresenteNaBaseDeDados)
                            .add("relatorio", relatorioDaOrientacaoPresenteNaBaseDeDados)
                            .add("link", linkDaOrientacaoPresenteNaBaseDeDados)
                            .add("titulo", tituloDaOrientacaoPresenteNaBaseDeDados)
                            .add("alunoEmail", emailDoAlunoDaOrientacaoPresenteNaBaseDeDados)
                            .add("alunoNumero", numeroDoAlunoDaOrientacaoPresenteNaBaseDeDados)
                            .add("alunoNome", nomeDoAlunoDaOrientacaoPresenteNaBaseDeDados)
                            .add("dataInicio", dataInicioDaOrientacaoPresenteNaBaseDeDados)
                            .add("dataFim", dataFimDaOrientacaoPresenteNaBaseDeDados).build();

                    OrientacaoDoProfessorArray.add(OrientacaoDoProfessorJson);


                }
                JsonObject InstituicaoDaOrientacaoJson = InstituicaoArrayBuilder
                        .add("id", idInstituicaoPresenteNaBaseDeDados)
                        .add("nome", nomeDaInstituicaoPresenteNaBaseDeDados)
                        .add("Sigla", siglaDaInstituicaoPresenteNaBaseDeDados)
                        .add("Orientacoes", OrientacaoDoProfessorArray).build();

                InstituicaoDaOrientacaoArray.add(InstituicaoDaOrientacaoJson);


            }





        }catch (SQLException throwables){
            throwables.printStackTrace();
        }


        JsonObject usersJson = professoresArrayBuilder
                .add("email", emailDoProfessorPresenteNaBaseDeDados)
                .add("nome", nomeDoProfessorPresenteNaBaseDeDados)
                .add("instituicoes", InstituicaoDaOrientacaoArray).build();



        professoresBuilder.add(usersJson);

    }


}