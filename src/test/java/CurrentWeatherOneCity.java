import org.testng.annotations.Test;

import java.text.DecimalFormat;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CurrentWeatherOneCity extends BaseTest{
    @Test
    public void test01_ByName() {
        City city = getRandomCity();
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("q", city.getName())
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.getName(), equalTo(city.getName()));
        assertThat(res.main, not(nullValue()));
    }

    @Test
    public void test02_ByNameAndCountry() {
        City city = getRandomCity();
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("q", city.getName()+","+city.getCountry())
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.getName(), equalTo(city.getName()));
        assertThat(res.sys.getCountry(), equalTo(city.getCountry()));
        assertThat(res.getId(), equalTo(city.getId()));
        assertThat(res.main, not(nullValue()));
    }

    @Test
    public void test03_ById() {
        City city = getRandomCity();
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.getName(), equalTo(city.getName()));
        assertThat(res.sys.getCountry(), equalTo(city.getCountry()));
        assertThat(res.getId(), equalTo(city.getId()));
        assertThat(res.main, not(nullValue()));
    }

    @Test
    public void test04_ByCoordinates() {
        City city = getRandomCity();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", city.getCoord().getLat())
                .param("lon", city.getCoord().getLon())
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.getName(), equalTo(city.getName()));
        assertThat(res.sys.getCountry(), equalTo(city.getCountry()));
        assertThat(res.getId(), equalTo(city.getId()));
        assertThat(decimalFormat.format(res.coord.getLat()), equalTo(decimalFormat.format(city.coord.getLat())));
        assertThat(decimalFormat.format(res.coord.getLon()), equalTo(decimalFormat.format(city.coord.getLon())));
        assertThat(res.main, not(nullValue()));
    }

    @Test
    public void test05_ByZIP() {
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("zip", "10001")
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.getName(), equalTo("New York"));
        assertThat(res.sys.getCountry(), equalTo("US"));
        assertThat(res.main, not(nullValue()));
    }

    @Test
    public void test06_ByZIPAndCountry() {
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("zip", "10000,HR")
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.getName(), equalTo("Zagreb"));
        assertThat(res.sys.getCountry(), equalTo("HR"));
        assertThat(res.main, not(nullValue()));
    }

    @Test
    public void test07_IncorrectName() {
        given()
                .contentType("application/json")
                .param("q", fake.lorem().characters(20))
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .then()
                .statusCode(404);
    }

    @Test
    public void test08_IncorrectCountry2Chars() {
        City city = getRandomCity();
        given()
                .contentType("application/json")
                .param("q", city.getName()+","+fake.lorem().characters(2))
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .then()
                .statusCode(404);
    }

    @Test
    public void test09_IncorrectCharacterId() {
        given()
                .contentType("application/json")
                .param("id", fake.lorem().characters(10))
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .then()
                .statusCode(400);
    }

    @Test
    public void test10_IncorrectNumberId() {
        given()
                .contentType("application/json")
                .param("id", fake.number().digits(15))
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .then()
                .statusCode(404);
    }

    @Test
    public void test11_IncorrectCharacterCoordinates() {
        given()
                .contentType("application/json")
                .param("lat", fake.lorem().characters(5))
                .param("lon", fake.lorem().characters(5))
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .then()
                .statusCode(400);

    }

    @Test
    public void test12_IncorrectNumberCoordinates() {
        given()
                .contentType("application/json")
                .param("lat", fake.number().numberBetween(100,1000))
                .param("lon", fake.number().numberBetween(200,1000))
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .then()
                .statusCode(400);

    }

    @Test
    public void test13_IncorrectZIP() {
        given()
                .contentType("application/json")
                .param("zip", fake.lorem().characters(5))
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .then()
                .statusCode(404);
    }

    @Test
    public void test14_CheckTemp(){
        City city = getRandomCity();
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.main.getTemp(), greaterThanOrEqualTo(100f));
        assertThat(res.main.getTemp(), lessThan(350f));
    }

    @Test
    public void test15_CheckTempMaxMin(){
        City city = getRandomCity();
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.main.getTemp_min(), lessThanOrEqualTo(res.main.getTemp()));
        assertThat(res.main.getTemp(), lessThanOrEqualTo(res.main.getTemp_max()));
        assertThat(res.main.getTemp_min(), lessThanOrEqualTo(res.main.getTemp_max()));
    }

    @Test
    public void test16_CheckHumidity(){
        City city = getRandomCity();
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.main.getHumidity(), greaterThanOrEqualTo(0f));
        assertThat(res.main.getHumidity(), lessThanOrEqualTo(100f));
    }

    @Test
    public void test17_CheckClouds(){
        City city = getRandomCity();
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.clouds.getAll(), greaterThanOrEqualTo(0f));
        assertThat(res.clouds.getAll(),lessThanOrEqualTo(100f));
    }

    @Test
    public void test18_CheckSunriseSunset(){
        City city = getRandomCity();
        OneCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(res.sys.getSunrise(),lessThan(res.sys.getSunset()));
    }

    @Test
    public void test19_TempUnits(){
        City city = getRandomCity();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        OneCityCurrentResponse res_kelvin = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        OneCityCurrentResponse res_celsius = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("units","metric")
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        OneCityCurrentResponse res_fahrenheit = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("units","imperial")
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .as(OneCityCurrentResponse.class);

        assertThat(decimalFormat.format(res_kelvin.main.getTemp() - 273.15), equalTo(decimalFormat.format(res_celsius.main.getTemp())));
        assertThat(decimalFormat.format((res_kelvin.main.getTemp()*(9d/5))-459.67), equalTo(decimalFormat.format(res_fahrenheit.main.getTemp())));
        assertThat(decimalFormat.format((res_celsius.main.getTemp()*(9d/5))+32), equalTo(decimalFormat.format(res_fahrenheit.main.getTemp())));
    }

    @Test
    public void test20_WrongApiKey(){
        City city = getRandomCity();

        given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", fake.lorem().characters(5))
                .when()
                .get("/weather")
                .then()
                .statusCode(401);
    }

    @Test
    public void test21_IncorrectCountry3PlusChars() {
        City city = getRandomCity();
        given()
                .contentType("application/json")
                .param("q", city.getName()+","+fake.lorem().characters(3,10))
                .param("appid", API_KEY)
                .when()
                .get("/weather")
                .then()
                .statusCode(404);
    }
}
