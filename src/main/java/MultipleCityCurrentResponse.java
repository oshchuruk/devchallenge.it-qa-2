import java.util.Arrays;
import java.util.List;

class MultipleCityCurrentResponse {
    private int cnt;
    private int count;
    private OneCityCurrentResponse[] list;

    int getCount() {
        return count;
    }

    int getCnt() {

        return cnt;
    }

    List<OneCityCurrentResponse> transformList(){
        return Arrays.asList(list);
    }
}
