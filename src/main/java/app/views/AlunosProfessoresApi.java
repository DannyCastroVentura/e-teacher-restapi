package app.views;

import javax.json.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/alunosProfessores/*")
public class AlunosProfessoresApi extends HttpServlet {

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
            mostrarTodosOsProfessoresDeUmAluno(split[3], con, jsonBuilder, false);
        }else{
            mostrarTodosOsProfessoresDeTodosOsAlunos(con, jsonBuilder, false);
        }
        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();

    }

    private void mostrarTodosOsProfessoresDeTodosOsAlunos(Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os professores de todos os alunos");
        System.out.println("This is the Connection: " + con);
        String sql = "SELECT * FROM alunos";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder alunosBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {
                showProfessoresDeAlunos(rs, alunosBuilder, con);

                existe = true;

            }
            jsonBuilder.add("alunos", alunosBuilder);

            if(!existe){
                jsonBuilder.add("info", "Nao existe nenhum user!");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarTodosOsProfessoresDeUmAluno(String s, Conection con, JsonObjectBuilder jsonBuilder, boolean existe) {
        System.out.println("Entrou no menu para mostrar todos os professores de um aluno");
        String[] split3 = s.split(" ", 2);
        String email = split3[0];
        String sql = "SELECT * FROM alunos WHERE email = '" + email + "'";
        ResultSet rs = con.selectSQL(sql);
        JsonArrayBuilder alunosBuilder = Json.createArrayBuilder();
        try {
            while (rs.next()) {

                showProfessoresDeAlunos(rs, alunosBuilder, con);

                jsonBuilder.add("alunos", alunosBuilder);
                existe = true;
            }
            if(!existe){
                jsonBuilder.add("info", "Não existe nenhum user com esse email!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void showProfessoresDeAlunos(ResultSet rs, JsonArrayBuilder alunosBuilder, Conection con) throws SQLException {

        String emailDoAlunoPresenteNaBaseDeDados = rs.getString("email");
        String nomeDoAlunoPresenteNaBaseDeDados = rs.getString("nome");
        String numeroDoAlunoPresenteNaBaseDeDados = rs.getString("numeroDeAluno");

        String sql = "SELECT professores.email as professorEmail, professores.nome as professorNome, professores.fotoPerfil as professorFotoPerfil FROM alunos inner join professoresAlunos on alunos.email = professoresAlunos.alunoEmail inner join professores on professoresAlunos.professorEmail = professores.email where alunos.email = '" + emailDoAlunoPresenteNaBaseDeDados + "'";
        ResultSet rs2 = con.selectSQL(sql);
        JsonObjectBuilder alunosArrayBuilder = Json.createObjectBuilder();
        JsonArrayBuilder professoresDoAlunoArray = Json.createArrayBuilder();
        JsonObjectBuilder professoresDoAlunoBuilder = Json.createObjectBuilder();
        System.out.println("chegou aqui");
        try{
            while(rs2.next()){
                String emailDoProfessorPresenteNaBaseDeDados = rs2.getString("professorEmail");
                String nomeDoProfessorPresenteNaBaseDeDados = rs2.getString("professorNome");
                String fotoPerfilPresenteNaBaseDeDados = rs2.getString("professorFotoPerfil");
                if(fotoPerfilPresenteNaBaseDeDados == null){
                    fotoPerfilPresenteNaBaseDeDados = "https://www.legal-tech.de/wp-content/uploads/Profilbild-Platzhalter.png";
                }
                System.out.println(nomeDoAlunoPresenteNaBaseDeDados);
                JsonObject professoresDoAlunoJson = professoresDoAlunoBuilder
                        .add("email", emailDoProfessorPresenteNaBaseDeDados)
                        .add("nome", nomeDoProfessorPresenteNaBaseDeDados)
                        .add("fotoPerfil", fotoPerfilPresenteNaBaseDeDados).build();

                professoresDoAlunoArray.add(professoresDoAlunoJson);
            }


        }catch (SQLException throwables){
            throwables.printStackTrace();
        }


        JsonObject usersJson = alunosArrayBuilder
                .add("email", emailDoAlunoPresenteNaBaseDeDados)
                .add("nome", nomeDoAlunoPresenteNaBaseDeDados)
                .add("numero", numeroDoAlunoPresenteNaBaseDeDados)
                .add("professores", professoresDoAlunoArray).build();



        alunosBuilder.add(usersJson);

    }


}