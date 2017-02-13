import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import models.Product;
import org.junit.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData.DataPart;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.RequestBuilder;
import play.test.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

public class ApplicationTest extends WithApplication {

    @Test
    public void goodRegister() {
        RequestBuilder rb = new RequestBuilder().method(POST).uri("/products");

        Materializer mat = app.injector().instanceOf(Materializer.class);
        DataPart dp1 = new DataPart("title", "sample");
        DataPart dp2 = new DataPart("description", "cheap");
        DataPart dp3 = new DataPart("price", "198");
        File file = new File(app.path().getPath() + "/public/images/default.png");
        FilePart<Source<ByteString, ?>> fp =
                new FilePart<>("image", "default.txt", "image/png", FileIO.fromFile(file));

        assertEquals(CREATED, route(rb.bodyMultipart(Arrays.asList(dp1, dp2, dp3, fp), mat)).status());
    }

    @Test
    public void badRegister() {
        RequestBuilder rb = new RequestBuilder().method(POST).uri("/products");
        List<HashMap<String, String>> mapList = new ArrayList<>();

        // priceに文字が含まれている
        HashMap<String, String> m1 = new HashMap<>();
        m1.put("title", "bad_product");
        m1.put("price", "100yen");
        mapList.add(m1);

        // titleの入力がない
        HashMap<String, String> m2 = new HashMap<>();
        m2.put("price", "100");
        mapList.add(m2);

        for( HashMap<String, String> hashMap : mapList ) {
            Result result = route(rb.bodyForm(hashMap));
            assertEquals(BAD_REQUEST, result.status());
        }
    }

    @Test
    public void goodUpdate() {
        RequestBuilder rb = new RequestBuilder().method(POST).uri("/products/" + findMinId());

        Materializer mat = app.injector().instanceOf(Materializer.class);
        DataPart dID = new DataPart("id", findMinId());
        DataPart dp1 = new DataPart("title", "updated product");
        DataPart dp2 = new DataPart("description", "updated");
        DataPart dp3 = new DataPart("price", "12345");
        File file = new File(app.path().getPath() + "/public/images/default.png");
        FilePart<Source<ByteString, ?>> fp =
                new FilePart<>("image", "default.txt", "image/png", FileIO.fromFile(file));

        assertEquals(CREATED, route(rb.bodyMultipart(Arrays.asList(dID, dp1, dp2, dp3, fp), mat)).status());
    }

    @Test
    public void badUpdate() {
        // 存在しないID
        assertEquals(BAD_REQUEST, route(new RequestBuilder().method(POST).uri("/products/0")).status());
    }

    @Test
    public void goodSearch() {
        RequestBuilder rb = new RequestBuilder().method(GET);
        List<Result> goodList = new ArrayList<>();

        goodList.add(route(rb.uri("/products")));
        goodList.add(route(rb.uri("/products/1")));
        goodList.add(route(rb.uri("/products?keyword=cheap")));
        goodList.add(route(rb.uri("/products?keyword=cheap&max=500&min=50")));

        for(Result result : goodList) {
            assertEquals(OK, result.status());
        }
    }

    @Test
    public void badSearch() {
        // maxやminに文字が含まれている
        assertEquals(BAD_REQUEST, route(new RequestBuilder().method(GET).uri("/products?max=100yen")).status());
    }

    @Test
    public void goodDelete() {
        assertEquals(OK, route(new RequestBuilder().method(DELETE).uri("/products/" + findMinId())).status());
    }

    @Test
    public void badDelete() {
        // 存在しないID
        assertEquals(BAD_REQUEST, route(new RequestBuilder().method(DELETE).uri("/products/0")).status());
    }

    private String findMinId() {
        for (long i=0; i<Long.MAX_VALUE; i++) {
            if (Product.find.byId(i) != null) {
                return String.valueOf(i);
            }
        }
        return "0";
    }

}
