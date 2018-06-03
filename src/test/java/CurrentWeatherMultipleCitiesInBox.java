import org.testng.annotations.Test;

import java.text.DecimalFormat;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class CurrentWeatherMultipleCitiesInBox extends BaseTest {
    private static final Integer max_lon_left = -124;
    private static final Integer max_lon_right = -67;
    private static final Integer max_lat_bottom = 25;
    private static final Integer max_lat_top = 48;


    private String getRandomCoordinatesInTheUS(){
        Integer lon_left = fake.number().numberBetween(max_lon_left, max_lon_right-10);
        Integer lon_right = fake.number().numberBetween(lon_left, max_lon_right);
        Integer lat_bottom = fake.number().numberBetween(max_lat_top-10, max_lat_bottom);
        Integer lat_top = fake.number().numberBetween(max_lat_top, lat_bottom);
        return lon_left.toString() + "," + lat_bottom.toString() + "," + lon_right.toString() + "," + lat_top.toString();
    }

    @Test
    public void test01_ByBox() {
        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("bbox", getRandomCoordinatesInTheUS() + "," + fake.number().numberBetween(10,100))
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), not(0));

        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.main, not(nullValue()));
            assertThat(res_city.coord.Lat, lessThanOrEqualTo(max_lat_top.floatValue()));
            assertThat(res_city.coord.Lat, greaterThanOrEqualTo((max_lat_bottom.floatValue())));
            assertThat(res_city.coord.Lon, lessThanOrEqualTo(max_lon_right.floatValue()));
            assertThat(res_city.coord.Lon, greaterThanOrEqualTo(max_lon_left.floatValue()));
        }
    }

    @Test
    public void test02_WithNoZoom() {
        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("bbox", getRandomCoordinatesInTheUS() + "," + 0)
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), is(0));
    }

    @Test
    public void test03_WithoutZoomParameter() {
        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("bbox", getRandomCoordinatesInTheUS())
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), not(0));

        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.main, not(nullValue()));
            assertThat(res_city.coord.Lat, lessThanOrEqualTo(max_lat_top.floatValue()));
            assertThat(res_city.coord.Lat, greaterThanOrEqualTo((max_lat_bottom.floatValue())));
            assertThat(res_city.coord.Lon, lessThanOrEqualTo(max_lon_right.floatValue()));
            assertThat(res_city.coord.Lon, greaterThanOrEqualTo(max_lon_left.floatValue()));
        }
    }

    @Test
    public void test04_NotEnoughCoordinates() {
        given()
                .contentType("application/json")
                .param("bbox", fake.number().numberBetween(max_lon_left, max_lon_right) + ','
                        + fake.number().numberBetween(max_lat_top, max_lat_bottom) + ','
                        + fake.number().numberBetween(max_lon_left, max_lon_right))
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .then()
                .statusCode(400);
    }

    @Test
    public void test05_IncorrectCoordinates() {
        given()
                .contentType("application/json")
                .param("bbox", fake.number().digits(20))
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .then()
                .statusCode(400);
    }


    @Test
    public void test06_CoordinatesInWater() {
        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("bbox", "-60,25,-31,43")
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), equalTo(0));
    }

    @Test
    public void test07_CheckTemp(){
        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("bbox", getRandomCoordinatesInTheUS() + "," + fake.number().numberBetween(10,100))
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), not(0));

        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.main.getTemp(), greaterThanOrEqualTo(100f));
            assertThat(res_city.main.getTemp(), lessThan(350f));
        }
    }

    @Test
    public void test08_CheckTempMaxMin(){
        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("bbox", getRandomCoordinatesInTheUS() + "," + fake.number().numberBetween(10,100))
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), not(0));


        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.main.getTemp_min(), lessThanOrEqualTo(res_city.main.getTemp()));
            assertThat(res_city.main.getTemp(), lessThanOrEqualTo(res_city.main.getTemp_max()));
            assertThat(res_city.main.getTemp_min(), lessThanOrEqualTo(res_city.main.getTemp_max()));
        }
    }

    @Test
    public void test09_CheckHumidity(){
        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("bbox", getRandomCoordinatesInTheUS() + "," + fake.number().numberBetween(10,100))
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), not(0));


        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.main.getHumidity(), greaterThanOrEqualTo(0f));
            assertThat(res_city.main.getHumidity(), lessThanOrEqualTo(100f));
        }
    }

    @Test
    public void test10_CheckClouds(){
        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("bbox", getRandomCoordinatesInTheUS() + "," + fake.number().numberBetween(10,100))
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), not(0));


        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.clouds.getAll(), greaterThanOrEqualTo(0f));
            assertThat(res_city.clouds.getAll(),lessThanOrEqualTo(100f));

        }
    }

    @Test
    public void test11_CheckSunriseSunset(){
        MultipleCityCurrentResponse res = given()
                .contentType("application/json")
                .param("bbox", getRandomCoordinatesInTheUS() + "," + fake.number().numberBetween(10,100))
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res.getCnt(), not(0));

        for(OneCityCurrentResponse res_city : res.transformList()){
            assertThat(res_city.sys.getSunrise(),lessThan(res_city.sys.getSunset()));
        }
    }

    @Test
    public void test12_TempUnits(){
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String coordinates = getRandomCoordinatesInTheUS() + "," + fake.number().numberBetween(10,100) ;

        MultipleCityCurrentResponse res_kelvin = given()
                .contentType("application/json")
                .param("bbox", coordinates)
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res_kelvin.getCnt(), not(0));
        OneCityCurrentResponse res_city1_kelvin = res_kelvin.transformList().get(0);
        OneCityCurrentResponse res_city2_kelvin = res_kelvin.transformList().get(1);

        MultipleCityCurrentResponse res_celsius = given()
                .contentType("application/json")
                .param("bbox", coordinates)
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res_celsius.getCnt(), not(0));
        OneCityCurrentResponse res_city1_celsius = res_celsius.transformList().get(0);
        OneCityCurrentResponse res_city2_celsius = res_celsius.transformList().get(1);

        MultipleCityCurrentResponse res_fahrenheit = given()
                .contentType("application/json")
                .param("bbox", coordinates)
                .param("appid", API_KEY)
                .when()
                .get("/box/city")
                .as(MultipleCityCurrentResponse.class);

        assertThat(res_fahrenheit.getCnt(), not(0));
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
    public void test13_WrongApiKey(){
        given()
                .contentType("application/json")
                .param("bbox", getRandomCoordinatesInTheUS() + "," + fake.number().numberBetween(10,100))
                .param("appid", fake.lorem().characters(5))
                .when()
                .get("/box/city")
                .then()
                .statusCode(401);
    }
}
