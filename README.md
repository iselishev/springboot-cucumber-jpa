## Example of Spring Boot + JPA + Cucumber + REST Assured, Test Allure Report, Jacoco Test Report


#### To trigger tests execution, please follow the steps below:
```
$ git clone https://github.com/iselishev/springboot-cucumber-jpa.git
$ gradlew clean test -Dspring.profiles.active=test
$ gradlew allureReport
$ gradlew clean jacocoTestReport -Dspring.profiles.active=test
...



