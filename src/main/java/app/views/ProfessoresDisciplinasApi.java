package app.views;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/professoresDisciplinas/*")
public class ProfessoresDisciplinasApi extends HttpServlet {

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
            mostrarTodoasAsDisciplinasDeUmProfessor(split[3], con, jsonBuilder, false);
        }else{
            mostrarTodasAsDisciplinasDeTodosOsProfessores(con, jsonBuilder, false);
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

        System.out.println("chegou ao adicionar nova disciplina no professor");
        if(body.containsKey("professorEmail") && body.containsKey("idDisciplina") && body.containsKey("semestre") && body.containsKey("idInstituicao")){
            String professorEmail = body.getString("professorEmail");
            int idDisciplina = body.getInt("idDisciplina");
            int semestre = body.getInt("semestre");
            int idInstituicao = body.getInt("idInstituicao");
            System.out.println("Vai entrar agr na funcao");
            adicionarDisciplina(jsonBuilder, professorEmail, idDisciplina , idInstituicao, semestre, con);
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
        System.out.println("chegou no put");
        if(body.containsKey("idInstituicao") && body.containsKey("idDisciplina") && body.containsKey("professorEmail"))
        {
            System.out.println("entrou no if");
            int idInstituicao = body.getInt("idInstituicao");
            int idDisciplina = body.getInt("idDisciplina");
            String professorEmail = body.getString("professorEmail");
            removerAtivo(idInstituicao, idDisciplina, professorEmail, con, jsonBuilder);
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

        System.out.println("chegou ao apagar disciplina do professor");
        if(body.containsKey("professorEmail") && body.containsKey("idDisciplina") && body.containsKey("idInstituicao")){
            String professorEmail = body.getString("professorEmail");
            int idDisciplina = body.getInt("idDisciplina");
            int idInstituicao = body.getInt("idInstituicao");
            System.out.println("Vai entrar agr na funcao");
            apagarDisciplina(jsonBuilder, professorEmail, idDisciplina , idInstituicao, con);
        }

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();

    }


    private void removerAtivo(int idInstituicao, int idDisciplina, String professorEmail, Conection con, JsonObjectBuilder jsonBuilder){
        System.out.println("Chegou ao meno remover uma disciplina de ativa de um professor");
        String sql = "SELECT * FROM instituicao WHERE idInstituicao = " + idInstituicao;
        ResultSet rs = con.selectSQL(sql);
        try{
            if(rs.next()){
                sql = "SELECT * FROM disciplinas WHERE idDisciplinas = " + idDisciplina;
                rs = con.selectSQL(sql);
                if(rs.next()){
                    sql = "SELECT * FROM professores WHERE email = '" + professorEmail + "'";
                    rs = con.selectSQL(sql);
                    if(rs.next()){
                        sql = "SELECT * FROM professorDisciplinas WHERE email = '" + professorEmail + "' and idDisciplinas = " + idDisciplina + " and idInstituicao = " + idInstituicao + " and estadoDisciplina = 't'";
                        rs = con.selectSQL(sql);
                        if(rs.next()){
                            sql = "SELECT * FROM professorDisciplinas WHERE email = '" + professorEmail + "' and idDisciplinas = " + idDisciplina + " and idInstituicao = " + idInstituicao + " and estadoDisciplina = 'f'";
                            rs = con.selectSQL(sql);
                            if(rs.next()){
                                sql = "DELETE FROM professorDisciplinas WHERE estadoDisciplina = 't' and email = '" + professorEmail + "' and idDisciplinas = " + idDisciplina + " and idInstituicao = " + idInstituicao;
                                int res = con.executeSQL(sql);
                                if (res > 0) {
                                    jsonBuilder.add("info", "Disciplina removida das ativas no professor com sucesso!");
                                } else {
                                    jsonBuilder.add("info", "Aconteceu algum erro");
                                }
                            }else{
                                sql = "UPDATE professorDisciplinas SET estadoDisciplina = 'f' WHERE estadoDisciplina = 't' and email = '" + professorEmail + "' and idDisciplinas = " + idDisciplina + " and idInstituicao = " + idInstituicao;
                                int res = con.executeSQL(sql);
                                if (res > 0) {
                                    jsonBuilder.add("info", "Disciplina removida das ativas no professor com sucesso!");
                                } else {
                                    jsonBuilder.add("info", "Aconteceu algum erro");
                                }
                            }

                        }else{
                            jsonBuilder.add("info", "Não existe nenhuma disciplina nesse professor nessa instituicao!");
                        }
                    }else{
                        jsonBuilder.add("info", "Não existe nenhum professor com esse email!");
                    }
                }else{
                    jsonBuilder.add("info", "Não existe nenhuma disciplina com esse id!");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhuma instituicao com esse id!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void apagarDisciplina(JsonObjectBuilder jsonBuilder, String professorEmail, int idDisciplina, int idInstituicao, Conection con) {
        System.out.println("Entrou no menu para apagar uma disciplina a um professor");
        String sql = "SELECT * FROM professores WHERE email = '" + professorEmail + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "SELECT * FROM disciplinas WHERE idDisciplinas = " + idDisciplina;
                rs = con.selectSQL(sql);
                if(rs.next()) {
                    sql = "SELECT * FROM instituicao WHERE idInstituicao = " + idInstituicao;
                    ResultSet rs2 = con.selectSQL(sql);
                    if(rs2.next()){
                        sql = "DELETE FROM professorDisciplinas WHERE idDisciplinas = " + idDisciplina + " and idInstituicao = " + idInstituicao + " and email = '" + professorEmail + "'";
                        int res = con.executeSQL(sql);
                        if (res > 0) {
                            jsonBuilder.add("info", "Disciplina apagada no professorDisciplinas com sucesso!");
                        } else {
                            jsonBuilder.add("info", "Aconteceu algum erro");
                        }
                    }else{
                        jsonBuilder.add("info", "Não existe nenhuma instituicao com esse id!");
                    }

                } else {
                    jsonBuilder.add("info", "Não existe nenhuma disciplina com esse id!");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum professor com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adicionarDisciplina(JsonObjectBuilder jsonBuilder, String professorEmail, int idDisciplina, int idInstituicao, int semestre, Conection con) {
        System.out.println("Entrou no menu para adicionar uma disciplina a um professor");
        String sql = "SELECT * FROM professores WHERE email = '" + professorEmail + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "SELECT * FROM disciplinas WHERE idDisciplinas = " + idDisciplina;
                rs = con.selectSQL(sql);
                if(rs.next()) {
                    sql = "SELECT * FROM instituicao WHERE idInstituicao = " + idInstituicao;
                    ResultSet rs2 = con.selectSQL(sql);
                    if(rs2.next()){
                        sql = "SELECT * FROM professorDisciplinas WHERE idInstituicao = " + idInstituicao + " and email = '" + professorEmail + "' and idDisciplinas = " + idDisciplina + " and estadoDisciplina = 't'";
                        ResultSet rs3 = con.selectSQL(sql);
                        if(!rs3.next()){
                            sql = "INSERT INTO professorDisciplinas (email, idDisciplinas, idInstituicao, semestre) values " +
                                    "('" + professorEmail + "', " + idDisciplina + ", " + idInstituicao + ", " + semestre + ")";
                            int res = con.executeSQL(sql);
                            if (res > 0) {
                                jsonBuilder.add("info", "Disciplina inserida no professorDisciplinas com sucesso!");
                            } else {
                                jsonBuilder.add("info", "Aconteceu algum erro");
                            }
                        }else{
                            jsonBuilder.add("info", "Disciplina já pertencente a esse professor nessa mesma instituicao!");
                        }
                    }else{
                        jsonBuilder.add("info", "Não existe nenhuma instituicao com esse id!");
                    }

                } else {
                    jsonBuilder.add("info", "Não existe nenhuma disciplina com esse id!");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum professor com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void mostrarTodasAsDisciplinasDeTodosOsProfessores(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todas os disciplinas de todos os professores");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT email, nome FROM professores";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder professoresBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showDisciplinasDeProfessores(rs, professoresBuilder, con);

                existe = true;

            }
            jsonBuilder.add("professores", professoresBuilder);

            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhum user!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarTodoasAsDisciplinasDeUmProfessor(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os alunos de um professor");
        String[] split3 = s.split(" ", 2);
        String email = split3[0];
        String sql = "SELECT * FROM professores WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder professoresBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showDisciplinasDeProfessores(rs, professoresBuilder, con);

                jsonBuilder.add("professores", professoresBuilder);
                existe = true;
            }
            if(!existe){
                jsonBuilder.add("info", "Não existe nenhum user com esse email!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showDisciplinasDeProfessores(ResultSet rs, JsonArrayBuilder professoresBuilder, Conection con) throws SQLException {
        String emailDoProfessorPresenteNaBaseDeDados = rs.getString("email");
        String nomeDoProfessorPresenteNaBaseDeDados = rs.getString("nome");

        String sql = "select distinct professorDisciplinas.email as emailProfessor, i.idInstituicao as idInstituicao, i.nome as nomeInstituicao, i.sigla as siglaInstituicao from professorDisciplinas inner join instituicao i on i.idInstituicao = professorDisciplinas.idInstituicao where professorDisciplinas.email = '" + emailDoProfessorPresenteNaBaseDeDados + "';";
        ResultSet rs2 = con.selectSQL(sql);
        JsonObjectBuilder professoresArrayBuilder = Json.createObjectBuilder();
        JsonArrayBuilder instituicaoDasDisciplinasArray = Json.createArrayBuilder();
        JsonObjectBuilder instituicaoDasDisciplinasBuilder = Json.createObjectBuilder();
        JsonObjectBuilder disciplinasDoProfessorBuilder = Json.createObjectBuilder();
        JsonArrayBuilder disciplinasDoProfessorArray = Json.createArrayBuilder();
        System.out.println("chegou aqui");
        try{
            while(rs2.next()){
                int idInstituicaoPresenteNaBaseDeDados = rs2.getInt("idInstituicao");
                String nomeDaInstituicaoPresenteNaBaseDeDados = rs2.getString("nomeInstituicao");
                String siglaDaInstituicaoPresenteNaBaseDeDados = rs2.getString("siglaInstituicao");

                String sql2 = "select professorDisciplinas.email as emailProfessor, d.idDisciplinas as idDisciplinas, d.sigla as siglaDisciplinas, d.nome as nomeDisciplinas, professorDisciplinas.estadoDisciplina as estadoDisciplina, professorDisciplinas.semestre as semestre from professorDisciplinas inner join instituicao i on i.idInstituicao = professorDisciplinas.idInstituicao inner join disciplinas d on professorDisciplinas.idDisciplinas = d.idDisciplinas where professorDisciplinas.email = '" + emailDoProfessorPresenteNaBaseDeDados + "' and i.idInstituicao = " + idInstituicaoPresenteNaBaseDeDados + " order by professorDisciplinas.idInstituicao ;";
                ResultSet rs3 = con.selectSQL(sql2);

                while(rs3.next()){
                    int idDisciplinaPresenteNaBaseDeDados = rs3.getInt("idDisciplinas");
                    String siglaDaDisciplinaPresenteNaBaseDeDados = rs3.getString("siglaDisciplinas");
                    String nomeDaDisciplinaPresenteNaBaseDeDados = rs3.getString("nomeDisciplinas");
                    String estadoDaDisciplinaPresenteNaBaseDeDados = rs3.getString("estadoDisciplina");
                    String semestreDaDisciplinaPresenteNaBaseDeDados = rs3.getString("semestre");
                    JsonObject disciplinasDoProfessorJson = disciplinasDoProfessorBuilder
                            .add("idDisciplinas", idDisciplinaPresenteNaBaseDeDados)
                            .add("sigla", siglaDaDisciplinaPresenteNaBaseDeDados)
                            .add("nome", nomeDaDisciplinaPresenteNaBaseDeDados)
                            .add("estado", estadoDaDisciplinaPresenteNaBaseDeDados)
                            .add("semestre", semestreDaDisciplinaPresenteNaBaseDeDados).build();

                    disciplinasDoProfessorArray.add(disciplinasDoProfessorJson);
                }

                JsonObject instituicaoDasDisciplinasJson = instituicaoDasDisciplinasBuilder
                        .add("idInstituicao", idInstituicaoPresenteNaBaseDeDados)
                        .add("nome", nomeDaInstituicaoPresenteNaBaseDeDados)
                        .add("sigla", siglaDaInstituicaoPresenteNaBaseDeDados)
                        .add("disciplinas", disciplinasDoProfessorArray).build();

                instituicaoDasDisciplinasArray.add(instituicaoDasDisciplinasJson);
            }


        }catch (SQLException throwables){
            throwables.printStackTrace();
        }


        JsonObject usersJson = professoresArrayBuilder
                .add("email", emailDoProfessorPresenteNaBaseDeDados)
                .add("nome", nomeDoProfessorPresenteNaBaseDeDados)
                .add("instituicao", instituicaoDasDisciplinasArray).build();



        professoresBuilder.add(usersJson);

    }


}