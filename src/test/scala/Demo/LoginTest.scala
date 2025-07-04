package Demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import Demo.Data._
import scala.concurrent.duration._

class LoginTest extends Simulation{

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    //Verificar de forma general para todas las solicitudes.    

  // 2 Scenario Definition: Login
  val scn = scenario("Login").
    exec(http("login")
      .post(s"users/login")
      .body(StringBody(s"""{"email": "$email", "password": "$password"}""")).asJson
       //Recibir información de la cuenta
      .check(status.is(200))
      .check(jsonPath("$.token").saveAs("authToken"))
    )
  .pause(1.second)
  // 3 Scenario Definition: Add Contact
  .exec(
      http("Create Contact")
        .post(s"contacts")
        .header("Authorization", "Bearer ${authToken}")
        .body(StringBody(s"""{"firstName": "Ok","lastName": "Gomez","birthdate": "1970-01-01","email": "jdoe@fake.com","phone": "8005555555","street1": "1 Main St.","street2": "Apartment A","city": "Anytown","stateProvince": "KS","postalCode": "12345","country": "USA"}""")).asJson
        .check(status.is(201))
    )


  // 4 Load Scenario
  setUp(
    scn.inject(rampUsers(10).during(50))
  ).protocols(httpConf);
}
