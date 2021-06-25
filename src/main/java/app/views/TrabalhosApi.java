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

@WebServlet("/trabalhos/*")
public class TrabalhosApi extends HttpServlet {

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
            mostrarTodosOsTrabalhosDeUmProfessor(split[3], con, jsonBuilder, false);
        }else{
            mostrarTodosOsTrabalhosDeTodosOsProfessores(con, jsonBuilder, false);
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

        if(body.containsKey("idTrabalho") && body.containsKey("nome") && body.containsKey("professorEmail") && body.containsKey("ano") && body.containsKey("resumo")){
            String idTrabalho = body.getString("idTrabalho");
            String nome = body.getString("nome");
            int ano = body.getInt("ano");
            String resumo = body.getString("resumo");
            String professorEmail = body.getString("professorEmail");

            String idTrabalhoFixed = new String(idTrabalho.getBytes(fromCharset), toCharset);
            String nomeFixed = new String(nome.getBytes(fromCharset), toCharset);
            String resumoFixed = new String(resumo.getBytes(fromCharset), toCharset);
            adicionarTrabalho(jsonBuilder, idTrabalhoFixed, nomeFixed, ano, resumoFixed, professorEmail, con);
        }else if (body.containsKey("idTrabalho") && body.containsKey("alunoEmail")){
            String idTrabalho = body.getString("idTrabalho");
            String alunoEmail = body.getString("alunoEmail");

            adicionarAlunoAoTrabalho(jsonBuilder, idTrabalho, alunoEmail, con);
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
        String idTrabalho = body.getString("idTrabalho");

        final Charset fromCharset = Charset.forName("windows-1252");
        final Charset toCharset = StandardCharsets.UTF_8;

        if(body.containsKey("nome")) {
            String nome = body.getString("nome");
            String fixed = new String(nome.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, idTrabalho, fixed, con, "nome");
        }
        if(body.containsKey("imagem")){
            String imagem = body.getString("imagem");
            String fixed = new String(imagem.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, idTrabalho, fixed, con, "imagem");
        }
        if(body.containsKey("resumo")){
            String resumo = body.getString("resumo");
            String fixed = new String(resumo.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, idTrabalho, fixed, con, "resumo");
        }
        if(body.containsKey("codigo")){
            String codigo = body.getString("codigo");
            String fixed = new String(codigo.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, idTrabalho, fixed, con, "codigo");
        }
        if(body.containsKey("relatorio")){
            String relatorio = body.getString("relatorio");
            String fixed = new String(relatorio.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, idTrabalho, fixed, con, "relatorio");
        }
        if(body.containsKey("informacao")){
            String informacao = body.getString("informacao");
            String fixed = new String(informacao.getBytes(fromCharset), toCharset);
            alterarString(jsonBuilder, idTrabalho, fixed, con, "informacao");
        }
        if(body.containsKey("nota")){
            System.out.println("entrou na nota");
            double nota = Float.parseFloat(body.getString("nota"));
            System.out.println(nota);
            alterarFloat(jsonBuilder, idTrabalho, nota, con, "nota");
        }
        if(body.containsKey("versao2")){
            System.out.println("entrou na versao2");
            boolean versao2 = body.getBoolean("versao2");
            System.out.println(versao2);
            alterarBoolean(jsonBuilder, idTrabalho, versao2, con, "versao2");
        }
        //TEM QUE ESTAR NO FINAL PARA NÃO HAVER CONFUSÕES
        if(body.containsKey("novoIdTrabalho")){
            System.out.println("entrou no novoIdTrabalho");
            String novoIdTrabalho = body.getString("novoIdTrabalho");
            System.out.println(novoIdTrabalho);
            alterarString(jsonBuilder, idTrabalho, novoIdTrabalho, con, "idTrabalho");
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
        if(body.containsKey("alunoEmail") && body.containsKey("idTrabalho"))
        {
            String alunoEmail = body.getString("alunoEmail");
            String idTrabalho = body.getString("idTrabalho");
            eliminarAlunoDoTrabalho(alunoEmail, idTrabalho, con, jsonBuilder);
        }else if(body.containsKey("idTrabalho")){
            String idTrabalho = body.getString("idTrabalho");
            eliminarTrabalho(idTrabalho, con, jsonBuilder);
        }

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();


    }



    private void eliminarTrabalho(String idTrabalho, Conection con, JsonObjectBuilder jsonBuilder) {
        System.out.println("Entrou no menu para eliminar o trabalho " + idTrabalho);
        String sql = "SELECT * FROM trabalhos WHERE idTrabalho = '" + idTrabalho + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "DELETE FROM trabalhos WHERE idTrabalho = '" + idTrabalho + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", "Trabalho " + idTrabalho + " eliminado com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aluno não existente no grupo");
                }

            }else{
                jsonBuilder.add("info", "Não existe nenhum aluno com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void eliminarAlunoDoTrabalho( String alunoEmail, String idTrabalho, Conection con, JsonObjectBuilder jsonBuilder) {
        System.out.println("Entrou no menu para eliminar o aluno " + alunoEmail + " do trabalho " + idTrabalho);
        String sql = "SELECT * FROM alunos WHERE email = '" + alunoEmail + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "SELECT * FROM trabalhos WHERE idTrabalho = '" + idTrabalho + "'";
                ResultSet rs2 = con.selectSQL(sql);
                if(rs2.next()){
                    sql = "DELETE FROM trabalhosAlunos WHERE idTrabalho = '" + idTrabalho + "' and alunoEmail = '" + alunoEmail + "'";
                    int res = con.executeSQL(sql);
                    if (res > 0) {
                        jsonBuilder.add("info", "Aluno " + alunoEmail +  " retirado do trabalho " + idTrabalho + " com sucesso!");
                    } else {
                        jsonBuilder.add("info", "Aluno não existente no grupo");
                    }
                }else{
                    jsonBuilder.add("info", "Não existe nenhum trabalho com esse idTrabalho!");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum aluno com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void alterarString(JsonObjectBuilder jsonBuilder, String idTrabalho, String string, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 trabalho");
        String sql = "SELECT * FROM trabalhos WHERE idTrabalho = '" + idTrabalho + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE trabalhos SET " + campo + " = '" + string + "' WHERE idTrabalho = '" + idTrabalho + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum trabalho com esse idTrabalho!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alterarFloat(JsonObjectBuilder jsonBuilder, String idTrabalho, double f, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 trabalho");
        String sql = "SELECT * FROM trabalhos WHERE idTrabalho = '" + idTrabalho + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE trabalhos SET " + campo + " = " + f + " WHERE idTrabalho = '" + idTrabalho + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum trabalho com esse idTrabalho!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void alterarBoolean(JsonObjectBuilder jsonBuilder, String idTrabalho, boolean b, Conection con, String campo) {
        System.out.println("Entrou no menu para alterar o campo " + campo + " de 1 trabalho");
        String sql = "SELECT * FROM trabalhos WHERE idTrabalho = '" + idTrabalho + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "UPDATE trabalhos SET " + campo + " = " + b + " WHERE idTrabalho = '" + idTrabalho + "'";
                int res = con.executeSQL(sql);
                if (res > 0) {
                    jsonBuilder.add("info", campo + " alterado(a) com sucesso!");
                } else {
                    jsonBuilder.add("info", "Aconteceu algum erro");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum trabalho com esse idTrabalho!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void adicionarTrabalho(JsonObjectBuilder jsonBuilder, String idTrabalho, String nome, int ano, String resumo, String professorEmail, Conection con) {
        System.out.println("Entrou no menu para adicionar um trabalho a um professor");
        String sql = "SELECT * FROM professores WHERE email = '" + professorEmail + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "SELECT * FROM trabalhos WHERE idTrabalho = '" + idTrabalho + "'";
                ResultSet rs2 = con.selectSQL(sql);
                if(!rs2.next()){
                    sql = "INSERT INTO trabalhos (idTrabalho, nome, ano, resumo, professorEmail) values " +
                            "('" + idTrabalho + "', '" + nome + "', " + ano + ", '" + resumo + "','" + professorEmail + "')";
                    int res = con.executeSQL(sql);
                    if (res > 0) {
                        jsonBuilder.add("info", "Trabalho inserido no professor com sucesso!");
                    } else {
                        jsonBuilder.add("info", "Aconteceu algum erro");
                    }
                }else{
                    jsonBuilder.add("info", "idTrabalho já existente");
                }

            }else{
                jsonBuilder.add("info", "Não existe nenhum professor com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adicionarAlunoAoTrabalho(JsonObjectBuilder jsonBuilder, String idTrabalho, String alunoEmail, Conection con) {
        System.out.println("Entrou no menu para adicionar um trabalho a um professor");
        String sql = "SELECT * FROM alunos WHERE email = '" + alunoEmail + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "SELECT * FROM trabalhosAlunos where idTrabalho = '" + idTrabalho + "' AND alunoEmail = '" + alunoEmail + "'";
                ResultSet rs2 = con.selectSQL(sql);
                if(!rs2.next()){
                    sql = "INSERT INTO trabalhosAlunos (idTrabalho, alunoEmail) values " +
                            "('" + idTrabalho + "', '" + alunoEmail + "')";
                    int res = con.executeSQL(sql);
                    if (res > 0) {
                        jsonBuilder.add("info", "Aluno inserido ao trabalho com sucesso!");
                    } else {
                        jsonBuilder.add("info", "Aconteceu algum erro");
                    }
                }else{
                    jsonBuilder.add("info", "Aluno ja pertence ao projeto!");
                }

            }else{
                jsonBuilder.add("info", "Não existe nenhum aluno com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarTodosOsTrabalhosDeTodosOsProfessores(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os trabalhos de todos os professores");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT email, nome FROM professores";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder professoresBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showAlunosDeProfessores(rs, professoresBuilder, con);

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



    private void mostrarTodosOsTrabalhosDeUmProfessor(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os trabalhos de um professor");
        String[] split3 = s.split(" ", 2);
        String email = split3[0];
        String sql = "SELECT * FROM professores WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder professoresBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showAlunosDeProfessores(rs, professoresBuilder, con);

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

    private void showAlunosDeProfessores(ResultSet rs, JsonArrayBuilder professoresBuilder, Conection con) throws SQLException {
        String emailDoProfessorPresenteNaBaseDeDados = rs.getString("email");
        String nomeDoProfessorPresenteNaBaseDeDados = rs.getString("nome");

        String sql = "select * from trabalhos where professorEmail = '" + emailDoProfessorPresenteNaBaseDeDados + "'";
        ResultSet rs2 = con.selectSQL(sql);
        JsonObjectBuilder professoresArrayBuilder = Json.createObjectBuilder();
        JsonArrayBuilder trabalhosDoProfessorArray = Json.createArrayBuilder();
        try{
            while(rs2.next()){
                String idTrabalhoDoTrabalhoPresenteNaBaseDeDados = rs2.getString("idTrabalho");
                String nomeDoTrabalhoPresenteNaBaseDeDados = rs2.getString("nome");
                String relatorioDoTrabalhoPresenteNaBaseDeDados = rs2.getString("relatorio");
                String codigoDoTrabalhoPresenteNaBaseDeDados = rs2.getString("codigo");
                String informacaoDoTrabalhoPresenteNaBaseDeDados = rs2.getString("informacao");
                String imagemDoTrabalhoPresenteNaBaseDeDados = rs2.getString("imagem");
                String resumoDoTrabalhoPresenteNaBaseDeDados = rs2.getString("resumo");
                int anoDoTrabalhoPresenteNaBaseDeDados = rs2.getInt("ano");
                String notaDoTrabalhoPresenteNaBaseDeDados = rs2.getString("nota");
                String versaoDoTrabalhoPresenteNaBaseDeDados = rs2.getString("versao2");
                if(relatorioDoTrabalhoPresenteNaBaseDeDados == null){
                    relatorioDoTrabalhoPresenteNaBaseDeDados = "";
                }
                if(codigoDoTrabalhoPresenteNaBaseDeDados == null){
                    codigoDoTrabalhoPresenteNaBaseDeDados = "";
                }
                if(informacaoDoTrabalhoPresenteNaBaseDeDados == null){
                    informacaoDoTrabalhoPresenteNaBaseDeDados = "";
                }
                if(notaDoTrabalhoPresenteNaBaseDeDados == null){
                    notaDoTrabalhoPresenteNaBaseDeDados = "";
                }
                if(imagemDoTrabalhoPresenteNaBaseDeDados == null){
                    imagemDoTrabalhoPresenteNaBaseDeDados = "https://www.provalore.com.br/wp-content/uploads/2018/07/projetos.png";
                }


                String sql2 = "select alunos.nome as alunosnome, alunos.numeroDeAluno as alunosNumero, alunos.email as alunosEmail from alunos inner join trabalhosAlunos on alunos.email = trabalhosAlunos.alunoEmail inner join trabalhos on trabalhosAlunos.idTrabalho = trabalhos.idTrabalho inner join professores on professores.email = trabalhos.professorEmail WHERE trabalhosAlunos.idTrabalho = '" + idTrabalhoDoTrabalhoPresenteNaBaseDeDados + "'";
                ResultSet rs3 = con.selectSQL(sql2);
                JsonObjectBuilder trabalhosArrayBuilder = Json.createObjectBuilder();
                JsonArrayBuilder alunosDoTrabalhoArray = Json.createArrayBuilder();
                JsonObjectBuilder alunosDoTrabalhoBuilder = Json.createObjectBuilder();
                while(rs3.next()){
                    String nomeDoAlunoPresenteNaBaseDeDados = rs3.getString("alunosnome");
                    String emailDoAlunoPresenteNaBaseDeDados = rs3.getString("alunosemail");
                    String numeroDoAlunoPresenteNaBaseDeDados = rs3.getString("alunosnumero");
                    JsonObject alunosDoTrabalhoJson = alunosDoTrabalhoBuilder
                            .add("nome", nomeDoAlunoPresenteNaBaseDeDados)
                            .add("email", emailDoAlunoPresenteNaBaseDeDados)
                            .add("numero", numeroDoAlunoPresenteNaBaseDeDados).build();
                    alunosDoTrabalhoArray.add(alunosDoTrabalhoJson);
                }


                JsonObject trabalhosDoProfessorJson = trabalhosArrayBuilder
                        .add("idTrabalho", idTrabalhoDoTrabalhoPresenteNaBaseDeDados)
                        .add("nome", nomeDoTrabalhoPresenteNaBaseDeDados)
                        .add("ano", anoDoTrabalhoPresenteNaBaseDeDados)
                        .add("imagem", imagemDoTrabalhoPresenteNaBaseDeDados)
                        .add("resumo", resumoDoTrabalhoPresenteNaBaseDeDados)
                        .add("relatorio", relatorioDoTrabalhoPresenteNaBaseDeDados)
                        .add("codigo", codigoDoTrabalhoPresenteNaBaseDeDados)
                        .add("informacao", informacaoDoTrabalhoPresenteNaBaseDeDados)
                        .add("nota", notaDoTrabalhoPresenteNaBaseDeDados)
                        .add("versao2", versaoDoTrabalhoPresenteNaBaseDeDados)
                        .add("alunos", alunosDoTrabalhoArray).build();

                trabalhosDoProfessorArray.add(trabalhosDoProfessorJson);


            }


        }catch (SQLException throwables){
            throwables.printStackTrace();
        }


        JsonObject usersJson = professoresArrayBuilder
                .add("email", emailDoProfessorPresenteNaBaseDeDados)
                .add("nome", nomeDoProfessorPresenteNaBaseDeDados)
                .add("trabalhos", trabalhosDoProfessorArray).build();



        professoresBuilder.add(usersJson);

    }


}