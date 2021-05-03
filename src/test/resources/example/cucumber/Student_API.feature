Feature: Students REST API Checks

  Scenario: Check Get Request returns all records from DB
    Given only following student records exist in DB
      | name | passportNumber |
      | Nick | AAA1234        |
      | Liza | AAA5678        |
    When User sends GET request to /students
    Then following records are returned in response
      | name | passportNumber |
      | Nick | AAA1234        |
      | Liza | AAA5678        |

  Scenario: Check Get Request with param returns correct record info
    Given only following student records exist in DB
      | name | passportNumber |
      | Nick | AAA1234        |
      | Liza | AAA5678        |
    When User sends GET request to /students with id for 'Liza' with passport 'AAA5678'
    Then following records are returned in response
      | name | passportNumber |
      | Liza | AAA5678        |


  Scenario: Check Delete Request removes record from
    Given only following student records exist in DB
      | name | passportNumber |
      | Tom  | ZZZ1357        |
      | Nick | AAA1234        |
      | Liza | AAA5678        |
    When User sends DELETE request to /students with id for 'Nick' with passport 'AAA1234'
    And User sends GET request to /students
    Then following records are returned in response
      | name | passportNumber |
      | Tom  | ZZZ1357        |
      | Liza | AAA5678        |

  Scenario: Check Post Request adds new record
    Given only following student records exist in DB
      | name | passportNumber |
      | Nick | AAA1234        |
      | Tom  | ZZZ1357        |
    When User sends POST request to /students with following info
      | name  | passportNumber |
      | Lenny | LLL5678        |
    And User sends GET request to /students
    Then following records are returned in response
      | name  | passportNumber |
      | Nick  | AAA1234        |
      | Lenny | LLL5678        |
      | Tom   | ZZZ1357        |
