package app.views;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/professoresAlunos/*")
public class ProfessoresAlunosApi extends HttpServlet {

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
            mostrarTodosOsAlunosDeUmProfessor(split[3], con, jsonBuilder, false);
        }else{
            mostrarTodosOsAlunosDeTodosOsProfessores(con, jsonBuilder, false);
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


        if(body.containsKey("professorEmail") && body.containsKey("alunoEmail")){
            String professorEmail = body.getString("professorEmail");
            String alunoEmail = body.getString("alunoEmail");
            adicionarAluno(jsonBuilder, professorEmail, alunoEmail , "email", con);
        }else if(body.containsKey("professorEmail") && body.containsKey("alunoNumero")){
            String professorEmail = body.getString("professorEmail");
            String alunoNumero = body.getString("alunoNumero");
            adicionarAluno(jsonBuilder, professorEmail, alunoNumero , "numeroDeAluno", con);
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


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonObject body = Json.createReader(req.getReader()).readObject();
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        Conection con = new Conection();
        if(body.containsKey("alunoEmail") && body.containsKey("professorEmail"))
        {
            String alunoEmail = body.getString("alunoEmail");
            String professorEmail = body.getString("professorEmail");
            eliminarAlunoDoProfessor(alunoEmail, professorEmail, con, jsonBuilder);
        }

        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();


    }



    private void eliminarAlunoDoProfessor( String alunoEmail, String professorEmail, Conection con, JsonObjectBuilder jsonBuilder) {
        System.out.println("Entrou no menu para eliminar o aluno " + alunoEmail + " do professor " + professorEmail);
        String sql = "SELECT * FROM alunos WHERE email = '" + alunoEmail + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "SELECT * FROM professores WHERE email = '" + professorEmail + "'";
                ResultSet rs2 = con.selectSQL(sql);
                if(rs2.next()){
                    sql = "DELETE FROM professoresAlunos WHERE professorEmail = '" + professorEmail + "' and alunoEmail = '" + alunoEmail + "'";
                    int res = con.executeSQL(sql);
                    if (res > 0) {
                        jsonBuilder.add("info", "Aluno " + alunoEmail +  " retirado do professor " + professorEmail + " com sucesso!");
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
                jsonBuilder.add("info", "Não existe nenhum professor com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void adicionarAluno(JsonObjectBuilder jsonBuilder, String professorEmail, String aluno, String campo, Conection con) {
        System.out.println("Entrou no menu para adicionar um aluno a um professor");
        String sql = "SELECT * FROM professores WHERE email = '" + professorEmail + "'";
        ResultSet rs = con.selectSQL(sql);
        try {
            if(rs.next()) {
                sql = "SELECT * FROM alunos WHERE " + campo + " = '" + aluno + "'";
                rs = con.selectSQL(sql);
                if(rs.next()) {
                    String AlunoEmail = rs.getString("email");
                    sql = "SELECT * FROM professoresAlunos WHERE professorEmail = '" + professorEmail + "' and alunoEmail = '" + AlunoEmail + "'";
                    ResultSet rs2 = con.selectSQL(sql);
                    if(!rs2.next()){
                        sql = "INSERT INTO professoresAlunos (professorEmail, alunoEmail) values " +
                                "('" + professorEmail + "', '" + AlunoEmail + "')";
                        int res = con.executeSQL(sql);
                        if (res > 0) {
                            jsonBuilder.add("info", "Aluno inserido no professorAluno com sucesso!");
                        } else {
                            jsonBuilder.add("info", "Aconteceu algum erro");
                        }
                    }else{
                        jsonBuilder.add("info", "Aluno já pertencente a esse professor");
                    }

                } else {
                    jsonBuilder.add("info", "Não existe nenhum aluno com esse email!");
                }
            }else{
                jsonBuilder.add("info", "Não existe nenhum professor com esse email!");
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
                jsonBuilder.add("info", "Não existe nenhum professor com esse email!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alterarBoolean(String email, Conection con, JsonObjectBuilder jsonBuilder, String campo, boolean valor) {
        //alterar um user
        System.out.println("Entrou no menu para alterar conta");
        String sql = "UPDATE professores SET " + campo  + " = " + valor + " WHERE email = '" + email + "'";
        int res = con.executeSQL(sql);
        if (res > 0) {
            jsonBuilder.add("info", "Professor alterado com sucesso!");
        } else {
            jsonBuilder.add("info", "Aconteceu algum erro");
        }
    }

    private void mostrarTodosOsAlunosDeTodosOsProfessores(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os Alunos de todos os professores");
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
                jsonBuilder.add("info", "Nao existe nenhum user!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarTodosOsAlunosDeUmProfessor(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os alunos de um professor");
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
                jsonBuilder.add("info", "Não existe nenhum user com esse email!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showAlunosDeProfessores(ResultSet rs, JsonArrayBuilder professoresBuilder, Conection con) throws SQLException {
        String emailDoProfessorPresenteNaBaseDeDados = rs.getString("email");
        String nomeDoProfessorPresenteNaBaseDeDados = rs.getString("nome");

        String sql = "SELECT alunos.email as alunosEmail, alunos.nome as alunosNome, alunos.numeroDeAluno as alunosNumero FROM alunos inner join professoresAlunos on alunos.email = professoresAlunos.alunoEmail inner join professores on professoresAlunos.professorEmail = professores.email where professores.email = '" + emailDoProfessorPresenteNaBaseDeDados + "'";
        ResultSet rs2 = con.selectSQL(sql);
        JsonObjectBuilder professoresArrayBuilder = Json.createObjectBuilder();
        JsonArrayBuilder alunosDoProfessorArray = Json.createArrayBuilder();
        JsonObjectBuilder alunosDoProfessorBuilder = Json.createObjectBuilder();
        System.out.println("chegou aqui");
        try{
            while(rs2.next()){
                String emailDoAlunoPresenteNaBaseDeDados = rs2.getString("alunosemail");
                String nomeDoAlunoPresenteNaBaseDeDados = rs2.getString("alunosnome");
                String numeroDoAlunoPresenteNaBaseDeDados = rs2.getString("alunosNumero");
                System.out.println(nomeDoAlunoPresenteNaBaseDeDados);
                JsonObject alunosDoProfessorJson = alunosDoProfessorBuilder
                        .add("email", emailDoAlunoPresenteNaBaseDeDados)
                        .add("nome", nomeDoAlunoPresenteNaBaseDeDados)
                        .add("numero", numeroDoAlunoPresenteNaBaseDeDados).build();

                alunosDoProfessorArray.add(alunosDoProfessorJson);
            }


        }catch (SQLException throwables){
            throwables.printStackTrace();
        }


        JsonObject usersJson = professoresArrayBuilder
                .add("email", emailDoProfessorPresenteNaBaseDeDados)
                .add("nome", nomeDoProfessorPresenteNaBaseDeDados)
                .add("alunos", alunosDoProfessorArray).build();



        professoresBuilder.add(usersJson);

    }


}