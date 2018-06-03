import java.util.Arrays;
import java.util.List;

class OneCityFutureResponse {
    private int cnt;
    private FutureResponse[] list;
    City city;

    int getCnt() {

        return cnt;
    }

    List<FutureResponse> transformList(){
        return Arrays.asList(list);
    }
}
