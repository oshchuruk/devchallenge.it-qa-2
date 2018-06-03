import org.testng.annotations.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;


import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CurrentWeatherMultipleCitiesByIds extends BaseTest{

    @Test
    public void test01_ByIds() {
        City city1 = getRandomCity();
        City city2 = getRandomCity();

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city1.getId()+","+city2.getId())
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), equalTo(2));

        OneCityCurrentResponse res_city1 = res.transformList().get(0);
        OneCityCurrentResponse res_city2 = res.transformList().get(1);

        assertThat(res_city1.getName(), equalTo(city1.getName()));
        assertThat(res_city1.sys.getCountry(), equalTo(city1.getCountry()));
        assertThat(res_city1.getId(), equalTo(city1.getId()));
        assertThat(res_city1.main, not(nullValue()));

        assertThat(res_city2.getName(), equalTo(city2.getName()));
        assertThat(res_city2.sys.getCountry(), equalTo(city2.getCountry()));
        assertThat(res_city2.getId(), equalTo(city2.getId()));
        assertThat(res_city2.main, not(nullValue()));
    }

    @Test
    public void test02_ByMaxNumberOfIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        int i = 0;

        while(i<20){
            City city = getRandomCity();
            ids.add(city.getId());
            i++;
        }

        String ids_string = "";

        for(Integer id : ids){
            ids_string = ids_string.concat(id.toString()+",");
        }

        ids_string = ids_string.substring(0, ids_string.length()-1);

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", ids_string)
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), equalTo(20));

        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.getId(), isIn(ids));
            assertThat(res_city.main, not(nullValue()));
        }
    }

    @Test
    public void test03_ByMoreThanMaxNumberOfIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        int i = 0;

        while(i<21){
            City city = getRandomCity();
            ids.add(city.getId());
            i++;
        }

        String ids_string = "";

        for(Integer id : ids){
            ids_string = ids_string.concat(id.toString()+",");
        }

        ids_string = ids_string.substring(0, ids_string.length()-1);

        given()
                .contentType("application/json")
                .param("id", ids_string)
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .then()
                .statusCode(400);
    }

    @Test
    public void test04_IncorrectCharacterIds() {
        given()
                .contentType("application/json")
                .param("id", fake.lorem().characters(10)+","+fake.lorem().characters(10))
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .then()
                .statusCode(400);
    }

    @Test
    public void test05_IncorrectNumberIds() {
        given()
                .contentType("application/json")
                .param("id", fake.number().digits(15)+","+fake.number().digits(15))
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .then()
                .statusCode(404);
    }

    @Test
    public void test06_CheckTemp(){
        City city1 = getRandomCity();
        City city2 = getRandomCity();

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city1.getId()+","+city2.getId())
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .as(MultipleCityCurrentResponse.class);

        OneCityCurrentResponse res_city1 = res.transformList().get(0);
        OneCityCurrentResponse res_city2 = res.transformList().get(1);

        assertThat(res_city1.main.getTemp(), greaterThanOrEqualTo(100f));
        assertThat(res_city1.main.getTemp(), lessThan(350f));

        assertThat(res_city2.main.getTemp(), greaterThanOrEqualTo(100f));
        assertThat(res_city2.main.getTemp(), lessThan(350f));
    }

    @Test
    public void test07_CheckTempMaxMin(){
        City city1 = getRandomCity();
        City city2 = getRandomCity();

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city1.getId()+","+city2.getId())
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .as(MultipleCityCurrentResponse.class);

        OneCityCurrentResponse res_city1 = res.transformList().get(0);
        OneCityCurrentResponse res_city2 = res.transformList().get(1);


        assertThat(res_city1.main.getTemp_min(), lessThanOrEqualTo(res_city1.main.getTemp()));
        assertThat(res_city1.main.getTemp(), lessThanOrEqualTo(res_city1.main.getTemp_max()));
        assertThat(res_city1.main.getTemp_min(), lessThanOrEqualTo(res_city1.main.getTemp_max()));

        assertThat(res_city2.main.getTemp_min(), lessThanOrEqualTo(res_city2.main.getTemp()));
        assertThat(res_city2.main.getTemp(), lessThanOrEqualTo(res_city2.main.getTemp_max()));
        assertThat(res_city2.main.getTemp_min(), lessThanOrEqualTo(res_city2.main.getTemp_max()));

    }

    @Test
    public void test08_CheckHumidity(){
        City city1 = getRandomCity();
        City city2 = getRandomCity();

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city1.getId()+","+city2.getId())
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .as(MultipleCityCurrentResponse.class);

        OneCityCurrentResponse res_city1 = res.transformList().get(0);
        OneCityCurrentResponse res_city2 = res.transformList().get(1);

        assertThat(res_city1.main.getHumidity(), greaterThanOrEqualTo(0f));
        assertThat(res_city1.main.getHumidity(), lessThanOrEqualTo(100f));

        assertThat(res_city2.main.getHumidity(), greaterThanOrEqualTo(0f));
        assertThat(res_city2.main.getHumidity(), lessThanOrEqualTo(100f));
    }

    @Test
    public void test09_CheckClouds(){
        City city1 = getRandomCity();
        City city2 = getRandomCity();

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city1.getId()+","+city2.getId())
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .as(MultipleCityCurrentResponse.class);

        OneCityCurrentResponse res_city1 = res.transformList().get(0);
        OneCityCurrentResponse res_city2 = res.transformList().get(1);

        assertThat(res_city1.clouds.getAll(), greaterThanOrEqualTo(0f));
        assertThat(res_city1.clouds.getAll(),lessThanOrEqualTo(100f));

        assertThat(res_city2.clouds.getAll(), greaterThanOrEqualTo(0f));
        assertThat(res_city2.clouds.getAll(),lessThanOrEqualTo(100f));

    }

    @Test
    public void test10_CheckSunriseSunset(){
        City city1 = getRandomCity();
        City city2 = getRandomCity();

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("id", city1.getId()+","+city2.getId())
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .as(MultipleCityCurrentResponse.class);

        OneCityCurrentResponse res_city1 = res.transformList().get(0);
        OneCityCurrentResponse res_city2 = res.transformList().get(1);

        assertThat(res_city1.sys.getSunrise(),lessThan(res_city2.sys.getSunset()));
        assertThat(res_city1.sys.getSunrise(),lessThan(res_city2.sys.getSunset()));
    }

    @Test
    public void test11_TempUnits(){
        City city1 = getRandomCity();
        City city2 = getRandomCity();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        MultipleCityCurrentResponse res_kelvin = given()
                .contentType("application/json")
                .param("id", city1.getId()+","+city2.getId())
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .as(MultipleCityCurrentResponse.class);
        OneCityCurrentResponse res_city1_kelvin = res_kelvin.transformList().get(0);
        OneCityCurrentResponse res_city2_kelvin = res_kelvin.transformList().get(1);

        MultipleCityCurrentResponse res_celsius = given()
                .contentType("application/json")
                .param("id", city1.getId()+","+city2.getId())
                .param("units","metric")
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .as(MultipleCityCurrentResponse.class);
        OneCityCurrentResponse res_city1_celsius = res_celsius.transformList().get(0);
        OneCityCurrentResponse res_city2_celsius = res_celsius.transformList().get(1);

        MultipleCityCurrentResponse res_fahrenheit = given()
                .contentType("application/json")
                .param("id", city1.getId()+","+city2.getId())
                .param("units","imperial")
                .param("appid", API_KEY)
                .when()
                .get("/group")
                .as(MultipleCityCurrentResponse.class);
        OneCityCurrentResponse res_city1_fahrenheit = res_fahrenheit.transformList().get(0);
        OneCityCurrentResponse res_city2_fahrenheit = res_fahrenheit.transformList().get(1);


        assertThat(decimalFormat.format(res_city1_kelvin.main.getTemp() - 273.15), equalTo(decimalFormat.format(res_city1_celsius.main.getTemp())));
        assertThat(decimalFormat.format((res_city1_kelvin.main.getTemp()*(9d/5))-459.67), equalTo(decimalFormat.format(res_city1_fahrenheit.main.getTemp())));
        assertThat(decimalFormat.format((res_city1_celsius.main.getTemp()*(9d/5))+32), equalTo(decimalFormat.format(res_city1_fahrenheit.main.getTemp())));

        assertThat(decimalFormat.format(res_city2_kelvin.main.getTemp() - 273.15), equalTo(decimalFormat.format(res_city2_celsius.main.getTemp())));
        assertThat(decimalFormat.format((res_city2_kelvin.main.getTemp()*(9d/5))-459.67), equalTo(decimalFormat.format(res_city2_fahrenheit.main.getTemp())));
        assertThat(decimalFormat.format((res_city2_celsius.main.getTemp()*(9d/5))+32), equalTo(decimalFormat.format(res_city2_fahrenheit.main.getTemp())));
    }

    @Test
    public void test12_WrongApiKey(){
        City city = getRandomCity();
        given()
                .contentType("application/json")
                .param("id", city.getId())
                .param("appid", fake.lorem().characters(5))
                .when()
                .get("/group")
                .then()
                .statusCode(401);
    }
}
