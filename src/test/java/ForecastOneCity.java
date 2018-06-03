import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

public class ForecastOneCity  extends BaseTest{
    @Test
    public void test01_ByName() {
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("q", city.getName())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        assertThat(res.city.getName(), equalTo(city.getName()));
        assertThat(res.getCnt(), not(0));

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main, not(nullValue()));
        }
    }

    @Test
    public void test02_ByNameAndCountry() {
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("q", city.getName()+","+city.getCountry())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        assertThat(res.city.getName(), equalTo(city.getName()));
        assertThat(res.city.getCountry(), equalTo(city.getCountry()));
        assertThat(res.getCnt(), not(0));

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main, not(nullValue()));
        }
    }

    @Test
    public void test03_ById() {
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        assertThat(res.city.getName(), equalTo(city.getName()));
        assertThat(res.city.getCountry(), equalTo(city.getCountry()));
        assertThat(res.city.getId(),equalTo(city.getId()));
        assertThat(res.getCnt(), not(0));

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main, not(nullValue()));
        }
    }

    @Test
    public void test04_ByCoordinates() {
        City city = getRandomCity();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("lat", city.getCoord().getLat())
                .param("lon", city.getCoord().getLon())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        assertThat(res.city.getName(), equalTo(city.getName()));
        assertThat(res.city.getCountry(), equalTo(city.getCountry()));
        assertThat(res.city.getId(),equalTo(city.getId()));
        assertThat(decimalFormat.format(res.city.coord.getLat()), equalTo(decimalFormat.format(city.coord.getLat())));
        assertThat(decimalFormat.format(res.city.coord.getLon()), equalTo(decimalFormat.format(city.coord.getLon())));
        assertThat(res.getCnt(), not(0));

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main, not(nullValue()));
        }
    }

    @Test
    public void test05_ByZIP() {
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("zip", "10001")
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        assertThat(res.city.getName(), equalTo("New York"));
        assertThat(res.city.getCountry(), equalTo("US"));

        assertThat(res.getCnt(), not(0));

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main, not(nullValue()));
        }
    }

    @Test
    public void test06_ByZIPAndCountry() {
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("zip", "10000,HR")
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        assertThat(res.city.getName(), equalTo("Zagreb"));
        assertThat(res.city.getCountry(), equalTo("HR"));

        assertThat(res.getCnt(), not(0));

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main, not(nullValue()));
        }
    }

    @Test
    public void test07_IncorrectName() {
        given()
                .contentType("application/json")
                .param("q", fake.lorem().characters(20))
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
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
                .get("/forecast")
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
                .get("/forecast")
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
                .get("/forecast")
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
                .get("/forecast")
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
                .get("/forecast")
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
                .get("/forecast")
                .then()
                .statusCode(404);
    }


    @Test
    public void test14_CheckTemp(){
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main.getTemp(), greaterThanOrEqualTo(100f));
            assertThat(res_forecast.main.getTemp(), lessThan(350f));
        }

    }

    @Test
    public void test15_CheckTempMaxMin(){
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main.getTemp_min(), lessThanOrEqualTo(res_forecast.main.getTemp()));
            assertThat(res_forecast.main.getTemp(), lessThanOrEqualTo(res_forecast.main.getTemp_max()));
            assertThat(res_forecast.main.getTemp_min(), lessThanOrEqualTo(res_forecast.main.getTemp_max()));
        }
   }

    @Test
    public void test16_CheckHumidity(){
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main.getHumidity(), greaterThanOrEqualTo(0f));
            assertThat(res_forecast.main.getHumidity(), lessThanOrEqualTo(100f));
        }
    }

    @Test
    public void test17_CheckClouds(){
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.clouds.getAll(), greaterThanOrEqualTo(0f));
            assertThat(res_forecast.clouds.getAll(),lessThanOrEqualTo(100f));
        }
    }

    @Test
    public void test18_TempUnits(){
        City city = getRandomCity();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        OneCityFutureResponse res_kelvin = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        OneCityFutureResponse res_celsius = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("units","metric")
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        OneCityFutureResponse res_fahrenheit = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("units","imperial")
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        FutureResponse res_forecast = res_kelvin.transformList().get(0);

        assertThat(decimalFormat.format(res_forecast.main.getTemp() - 273.15), equalTo(decimalFormat.format(res_celsius.transformList().get(0).main.getTemp())));
        assertThat(decimalFormat.format((res_forecast.main.getTemp()*(9d/5))-459.67), equalTo(decimalFormat.format(res_fahrenheit.transformList().get(0).main.getTemp())));
        assertThat(decimalFormat.format((res_celsius.transformList().get(0).main.getTemp()*(9d/5))+32), equalTo(decimalFormat.format(res_fahrenheit.transformList().get(0).main.getTemp())));

    }

    @Test
    public void test19_CheckCount() {
        City city = getRandomCity();
        int num = fake.number().numberBetween(1,40);
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("cnt", num)
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        assertThat(res.getCnt(), equalTo(num));

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main, not(nullValue()));
        }
    }

    @Test
    public void test20_CheckDefaultCount() {
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        assertThat(res.getCnt(), equalTo(40));

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main, not(nullValue()));
        }
    }

    @Test
    public void test21_CheckCountMoreThanMax() {
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("cnt", 41)
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        assertThat(res.getCnt(), equalTo(40));

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main, not(nullValue()));
        }
    }

    @Test
    public void test22_CheckZeroCount() {
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("cnt", 0)
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);

        assertThat(res.getCnt(), equalTo(40));

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.main, not(nullValue()));
        }
    }

    @Test
    public void test23_CheckThatDatesGoOneByOne(){
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);
        long previousdate = 0;

        for(FutureResponse res_forecast : res.transformList()){
            assertThat(res_forecast.getDt(), greaterThan(previousdate));
            previousdate = res_forecast.getDt();
        }
    }

    @Test
    public void test24_CompareDateAndDtText(){
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));


        for(FutureResponse res_forecast : res.transformList()){
            Date dt = new Date(res_forecast.getDt()*1000);
            try {
                Date dt_text = format.parse(res_forecast.getDt_text());
                assertThat(dt, equalTo(dt_text));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test25_CheckFiveDays(){
        City city = getRandomCity();
        OneCityFutureResponse res = given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .as(OneCityFutureResponse.class);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        FutureResponse first_date = res.transformList().get(0);
        FutureResponse last_date = res.transformList().get(res.transformList().size()-1);

        assertThat(first_date.getDt()+(5*24*60*60)-(3*60*60), equalTo(last_date.getDt()));
    }

    @Test
    public void test26_WrongApiKey(){
        City city = getRandomCity();

        given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", fake.lorem().characters(5))
                .when()
                .get("/forecast")
                .then()
                .statusCode(401);
    }

    @Test
    public void test27_IncorrectCountry3PlusChars() {
        City city = getRandomCity();
        given()
                .contentType("application/json")
                .param("q", city.getName()+","+fake.lorem().characters(3,10))
                .param("appid", API_KEY)
                .when()
                .get("/forecast")
                .then()
                .statusCode(404);
    }
}
