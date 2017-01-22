import org.junit.*;
import play.api.libs.json.JsValue;
import play.api.libs.json.Json;
import play.mvc.*;
import play.test.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

public class ApplicationTest extends WithApplication {

    @Test
    public void goodRegister() {

        List<JsValue> jsValues = new ArrayList<>();
        // 商品パラメータ全指定
        jsValues.add(Json.parse("{\"id\":7,\"image_url\":\"product7\",\"title\":\"product7\"," +
                "\"description\":\"it is lucky product\",\"price\":777}"));
        // priceのみ指定なし
        jsValues.add(Json.parse("{\"id\":8,\"image_url\":\"product8\",\"title\":\"product8\"," +
                "\"description\":\"it is lucky product\"}"));

        Http.RequestBuilder rb = new Http.RequestBuilder().method(POST).uri("/products");
        List<Result> goodList = new ArrayList<>();

        for( JsValue jsValue : jsValues ) {
            Result result = route(rb.bodyJson(jsValue));
            assertEquals(CREATED, result.status());
        }
    }

    @Test
    public void badRegister() {

        Http.RequestBuilder rb = new Http.RequestBuilder().method(POST).uri("/products");

        String t101 = new StringWriter(){{ for(int i = 0; i<101; i++) write("a"); }}.toString();
        String t501 = new StringWriter(){{ for(int i = 0; i<501; i++) write("a"); }}.toString();

        List<String> strl = new ArrayList<>();
        // idの指定なし（id=0）
        strl.add("{\"image_url\":\"product7\",\"title\":\"product7\",\"description\":\"it is lucky product\"}");
        // image_urlの指定なし
        strl.add("{\"id\":7,\"title\":\"product7\",\"description\":\"it is lucky product\"}");
        // titleの指定なし
        strl.add("{\"id\":7,\"image_url\":\"product7\",\"description\":\"it is lucky product\"}");
        // descriptionの指定なし
        strl.add("{\"id\":7,\"image_url\":\"product7\",\"title\":\"product7\"}");
        // priceがマイナス
        strl.add("{\"id\":7,\"image_url\":\"product7\",\"title\":\"product7\",\"description\":\"lucky\",\"price\":-7}");
        // titleが100文字超え
        strl.add("{\"id\":7,\"image_url\":\"product7\",\"title\":\"" + t101 + "\",\"description\":\"lucky\"}");
        // descriptionが500文字超え
        strl.add("{\"id\":7,\"image_url\":\"product7\",\"title\":\"" + t501 + "\",\"description\":\"lucky\"}");
        // idに文字を指定
        strl.add("{\"id\":\"id\",\"image_url\":\"product7\",\"title\":\"p7\",\"description\":\"lucky\",\"price\":777}");
        // priceに文字を指定
        strl.add("{\"id\":7,\"image_url\":\"product7\",\"title\":\"p7\",\"description\":\"lucky\",\"price\":\"a\"}");

        for( String str : strl ) {
            Result result = route(rb.bodyJson(Json.parse(str)));
            assertEquals(BAD_REQUEST, result.status());
        }
    }

    @Test
    public void goodSearch() {

        Http.RequestBuilder rb = new Http.RequestBuilder().method(GET);
        List<Result> goodList = new ArrayList<>();

        goodList.add(route(rb.uri("/products")));
        goodList.add(route(rb.uri("/products?id=1")));
        goodList.add(route(rb.uri("/products?id=1&keyword=cheap")));
        goodList.add(route(rb.uri("/products?id=1&keyword=cheap&keyword=product")));
        goodList.add(route(rb.uri("/products?id=1&keyword=cheap&keyword=product&max=1000")));

        for(Result result : goodList) {
            assertEquals(OK, result.status());
        }
    }

    @Test
    public void badSearch() {

        Http.RequestBuilder rb = new Http.RequestBuilder().method(GET);
        List<Result> badList = new ArrayList<>();

        badList.add(route(rb.uri("/products?id=1&id=2")));           // idを二つ指定
        badList.add(route(rb.uri("/products?id=bad")));              // idに文字を指定
        badList.add(route(rb.uri("/products?max=100&min=500")));     // max < minとなっている
        badList.add(route(rb.uri("/products?price=500")));           // 不正なパラメータ指定

        for(Result result : badList) {
            assertEquals(BAD_REQUEST, result.status());
        }
    }


    @Test
    public void goodDelete() {

        Http.RequestBuilder rb = new Http.RequestBuilder().method(DELETE).uri("/products");

        List<JsValue> jsValues = new ArrayList<>();
        // idのみ指定
        jsValues.add(Json.parse("{\"id\":7}"));
        // id, keyword指定
        jsValues.add(Json.parse("{\"id\":8,\"keyword\":\"product8\"}"));
        // id, price指定
        jsValues.add(Json.parse("{\"id\":7,\"price\":777}"));
        // id, keyword, price全指定
        jsValues.add(Json.parse("{\"id\":7,\"keyword\":\"product7\",\"price\":777}"));

        for( JsValue jsValue : jsValues ) {
            Result result = route(rb.bodyJson(jsValue));
            assertEquals(OK, result.status());
        }
    }

    @Test
    public void badDelete() {

        Http.RequestBuilder rb = new Http.RequestBuilder().method(DELETE).uri("/products");

        List<JsValue> jsValues = new ArrayList<>();
        // idの指定なし
        jsValues.add(Json.parse("{\"keyword\":\"product8\"}"));
        // idに文字指定
        jsValues.add(Json.parse("{\"id\":\"id\"}"));
        // priceに文字指定
        jsValues.add(Json.parse("{\"id\":7,\"price\":\"price\"}"));

        for( JsValue jsValue : jsValues ) {
            Result result = route(rb.bodyJson(jsValue));
            assertEquals(BAD_REQUEST, result.status());
        }
    }

}
