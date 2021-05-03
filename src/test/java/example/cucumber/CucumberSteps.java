package example.cucumber;

import example.student.Student;
import example.student.StudentRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

@Slf4j
public class CucumberSteps {
    @Autowired
    private StudentRepository studentRepository;

    @Value("${app.host:http://localhost}")
    private String appHost;
    @Value("${local.server.port}")
    private int port;

    private List<Student> responseStudents = new ArrayList<>();


    @Given("only following student records exist in DB")
    public void onlyFollowingStudentRecordsExistInDB(List<Student> studentsStored) {
        studentRepository.deleteAll();
        studentsStored.forEach(x -> studentRepository.save(x));

        List<Student> allStudents = studentRepository.findAll();
        log.info("students tota " + studentRepository.findAll().size());
    }

    @When("User sends GET request to \\/students")
    public void userSendsGETRequestToStudents() {
        Response response = given()
                .baseUri(appHost + ":" + port)
                .when()
                .get("/students")
                .andReturn();
        Assertions.assertEquals(200, response.statusCode());
        responseStudents = response.jsonPath().getList(".", Student.class);
    }

    @Then("following records are returned in response")
    public void followingRecordsAreReturnedInresponse(List<Student> expectedStudents) {
        List<String> responseStudentsStrings = responseStudents.stream().map(s -> {
            s.setId(null);
            return s.toString();
        }).collect(Collectors.toList());
        List<String> expectedStudentsStrings = expectedStudents.stream().map(Student::toString).collect(Collectors.toList());
        Assertions.assertEquals(expectedStudentsStrings.size(), responseStudentsStrings.size(), "Total amount of students in Rest API result does not match expectations");
        expectedStudentsStrings.forEach(x -> Assertions.assertTrue(responseStudentsStrings.contains(x)));
    }

    @When("User sends DELETE request to \\/students with id for '{}' with passport '{}'")
    public void userSendsDELETERequestToStudentsWithIdForStudent(String name, String passport) {
        Long deleteId = getStudentIdByNameAndPassport(name, passport);
        log.info(String.format("Student with ID %s will be deleted", deleteId));

        Response response = given()
                .baseUri(appHost + ":" + port)
                .header("Content-type", "application/json")
                .when()
                .delete("/students/" + deleteId)
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());
    }

    private Long getStudentIdByNameAndPassport(String name, String passport) {
        Optional<Student> student = studentRepository.findAll().stream().filter(s -> s.getName().equals(name) && s.getPassportNumber().equals(passport)).findFirst();
        Long id = student.get().getId();
        if (id == null)
            throw new IllegalArgumentException("No such student is present in DB");
        return id;
    }

    @When("User sends GET request to \\/students with id for '{}' with passport '{}'")
    public void userSendsGETRequestToStudentsWithIdForStudent(String name, String passport) {
        Long getId = getStudentIdByNameAndPassport(name, passport);
        log.info(String.format("Student with ID %s will be retrieved", getId));

        Response response = given()
                .baseUri(appHost + ":" + port)
                .when()
                .get("/students/" + getId)
                .andReturn();
        Assertions.assertEquals(200, response.statusCode());
        Student respStudent = response.jsonPath().getObject("", Student.class);
        responseStudents.add(respStudent);
    }


    @When("User sends POST request to \\/students with following info")
    public void userSendsPOSTRequestToStudentsWithFollowingInfo(List<Student> dtStudents) {
        dtStudents.forEach(dtStudent->{
            String requestBody = "{\n" +
                    "  \"name\": \""+dtStudent.getName()+"\",\n" +
                    "  \"passportNumber\": \""+dtStudent.getPassportNumber()+"\"\n}";
            Response response = given()
                    .baseUri(appHost + ":" + port)
                    .header("Content-type", "application/json")
                    .and()
                    .body(requestBody)
                    .when()
                    .post("/students")
                    .then()
                    .extract().response();
            Assertions.assertEquals(201, response.statusCode());
        });

    }

}
