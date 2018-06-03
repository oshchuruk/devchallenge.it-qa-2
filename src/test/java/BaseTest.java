import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeTest;

import java.io.FileReader;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class BaseTest {
    public static final String API_KEY = "a5a7899705217def5fd4c973242c01fb";

    Faker fake = new Faker();
    Gson gson = new Gson();
    FileReader source;

    {
        try {
            source = new FileReader("src\\main\\resources\\city.list.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    City[] cities_array = gson.fromJson(source, City[].class);

    List<City> cities = Arrays.asList(cities_array);

    public City getRandomCity(){
        return cities.get(fake.number().numberBetween(0, cities.size()-1));
    }

    @BeforeTest
    public void setUp(){
        RestAssured.baseURI = "https://api.openweathermap.org/data/2.5";
    }


}
