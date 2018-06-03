import org.testng.annotations.Test;

import java.text.DecimalFormat;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CurrentWeatherMultipleCitiesInCircle extends BaseTest {
    @Test
    public void test01_ByCircle() {
        City city = getRandomCity();
        int count = fake.number().numberBetween(5,10);

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", count)
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCount(), equalTo(count));

        boolean flag_city = false;

        for(OneCityCurrentResponse res_city : res.transformList()){
            if(res_city.getId() == city.getId()){
                flag_city = true;
            }


            assertThat(res_city.main, not(nullValue()));
        }

        assertThat(flag_city, is(true));


    }

    @Test
    public void test02_ByMaxNumberOfCount() {
        City city = getRandomCity();

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", 50)
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCount(), equalTo(50));

        boolean flag = false;
        for(OneCityCurrentResponse res_city : res.transformList()){
            if(res_city.getId() == city.getId()){
                flag = true;
            }
            assertThat(res_city.main, not(nullValue()));
        }

        assertThat(flag, is(true));
    }

    @Test
    public void test03_ByMoreThanMaxNumberOfCount() {
        City city = getRandomCity();

        given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", 51)
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .then()
                .statusCode(400);
    }

    @Test
    public void test04_IncorrectCharacterCoordinates() {
        given()
                .contentType("application/json")
                .param("lat", fake.lorem().characters(5))
                .param("lon", fake.lorem().characters(5))
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .then()
                .statusCode(400);
    }

    @Test
    public void test05_IncorrectNumberCoordinates() {
        given()
                .contentType("application/json")
                .param("lat", fake.number().digits(5))
                .param("lon", fake.number().digits(5))
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .then()

                .statusCode(400);
    }

    @Test
    public void test06_DefaultCount() {
        City city = getRandomCity();

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCount(), equalTo(10));
    }

    @Test
    public void test07_CoordinatesInWater() {
        int count = fake.number().numberBetween(5,10);

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", 31)
                .param("lon", -50)
                .param("cnt", count)
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCount(), equalTo(0));
    }

    @Test
    public void test08_CheckTemp(){
        City city = getRandomCity();
        int count = fake.number().numberBetween(5,10);

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", count)
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCount(), equalTo(count));


        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.main.getTemp(), greaterThanOrEqualTo(100f));
            assertThat(res_city.main.getTemp(), lessThan(350f));
        }
    }

    @Test
    public void test09_CheckTempMaxMin(){
        City city = getRandomCity();
        int count = fake.number().numberBetween(5,10);

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", count)
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCount(), equalTo(count));


        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.main.getTemp_min(), lessThanOrEqualTo(res_city.main.getTemp()));
            assertThat(res_city.main.getTemp(), lessThanOrEqualTo(res_city.main.getTemp_max()));
            assertThat(res_city.main.getTemp_min(), lessThanOrEqualTo(res_city.main.getTemp_max()));
        }
    }

    @Test
    public void test10_CheckHumidity(){
        City city = getRandomCity();
        int count = fake.number().numberBetween(5,10);

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", count)
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCount(), equalTo(count));


        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.main.getHumidity(), greaterThanOrEqualTo(0f));
            assertThat(res_city.main.getHumidity(), lessThanOrEqualTo(100f));
        }
    }

    @Test
    public void test11_CheckClouds(){
        City city = getRandomCity();
        int count = fake.number().numberBetween(5,10);

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", count)
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCount(), equalTo(count));


        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.clouds.getAll(), greaterThanOrEqualTo(0f));
            assertThat(res_city.clouds.getAll(),lessThanOrEqualTo(100f));

        }
    }

    @Test
    public void test12_CheckSunriseSunset(){
        City city = getRandomCity();
        int count = fake.number().numberBetween(5,10);

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", count)
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCount(), equalTo(count));


        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.sys.getSunrise(),lessThan(res_city.sys.getSunset()));
        }
    }

    @Test
    public void test13_TempUnits(){
        City city = getRandomCity();
        int count = fake.number().numberBetween(5,10);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        MultipleCityCurrentResponse res_kelvin = given()
                .contentType("application/json")

                .param("cnt", count).param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);
        OneCityCurrentResponse res_city1_kelvin = res_kelvin.transformList().get(0);
        OneCityCurrentResponse res_city2_kelvin = res_kelvin.transformList().get(1);

        MultipleCityCurrentResponse res_celsius = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", count)
                .param("units","metric")
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);
        OneCityCurrentResponse res_city1_celsius = res_celsius.transformList().get(0);
        OneCityCurrentResponse res_city2_celsius = res_celsius.transformList().get(1);

        MultipleCityCurrentResponse res_fahrenheit = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", count)
                .param("units","imperial")
                .param("appid", API_KEY)
                .when()
                .get("/find")
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
    public void test14_WrongApiKey(){
        City city = getRandomCity();

        given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("appid", fake.lorem().characters(5))
                .when()
                .get("/box/city")
                .then()
                .statusCode(401);
    }

    @Test
    public void test15_CountryIsReturned() {
        City city = getRandomCity();
        int count = fake.number().numberBetween(5,10);

        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("lat", city.coord.getLat())
                .param("lon", city.coord.getLon())
                .param("cnt", count)
                .param("appid", API_KEY)
                .when()
                .get("/find")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCount(), equalTo(count));

        boolean flag_country = false;
        for(OneCityCurrentResponse res_city : res.transformList()){


            if(res_city.sys.getCountry().equals(city.getCountry())){
                flag_country = true;
            }
            assertThat(res_city.main, not(nullValue()));
        }

        assertThat(flag_country, is(true));

    }
}


