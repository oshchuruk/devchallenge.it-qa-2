public class BaseWeatherResponse {
    private long dt;
    Main main;
    public Weather[] weather;
    Clouds clouds;
    public Wind wind;
    public Rain rain;
    public Snow snow;


    long getDt() {
        return dt;
    }

}
