public class OneCityCurrentResponse extends BaseWeatherResponse {
    Coordinates coord;
    Sys sys;
    private int visibility;
    private int id;
    private String name;

    public int getVisibility() {
        return visibility;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }
}
