package controllers;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import play.libs.Json;
import play.mvc.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ProductController extends Controller{

    /********************
        商品データ登録
     ********************/
    @BodyParser.Of(BodyParser.Json.class)
    public Result register() {

        JsonNode json_input = request().body().asJson();

        //　POSTされたパラメータを取得
        String image_url    = json_input.findPath("image_url").textValue();
        String title        = json_input.findPath("title").textValue();
        String description  = json_input.findPath("description").textValue();
        int    price        = json_input.findPath("price").intValue();

        //　POSTされたパラメータが正常な値かチェック
        if( image_url == null || title == null || description == null ) {
            return badRequest( "Missing parameter! [Required: image_url, title, description(, price)]");
        } else if( title.length() > 100 ) {
            return badRequest("The parameter is too long! [Title length is 100 characters]");
        } else if( description.length() > 500 ) {
            return badRequest("The parameter is too long! [Description length is 500 characters]");
        } else if( price < 0 ) {
            return badRequest("Invalid parameter! [The price inputted]");
        }

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode rootNode = mapper.createArrayNode();

        //　現存するjsonデータにPOSTされたjsonデータを追加
        try {
            File file = new File("data/product.json");
            JsonNode current;
            for(int i=0; (current = mapper.readTree(file).get(i)) != null; i++) {
                rootNode.add(current);
            }
            rootNode.add(json_input);

            mapper.writeValue(file, rootNode);

        } catch( IOException e ) {
            e.printStackTrace();
            return badRequest("Failed to open or write file.");
        }

        //　レスポンス内容はリクエストデータ追加後の全商品データ
        return ok(Json.prettyPrint(rootNode));
    }

    /********************
        商品データ検索
     ********************/
    public Result search() {

        String[] words = {};
        int max_price = Integer.MAX_VALUE;
        int min_price = 0;

        // POSTされたパラメータを取得
        Map<String, String[]> form = request().body().asFormUrlEncoded();
        if( form.containsKey("word") )  words = form.get("word");
        if( form.containsKey("max") )   max_price = Integer.parseInt(form.get("max")[0]);
        if( form.containsKey("min") )   min_price = Integer.parseInt(form.get("min")[0]);

        // パラメータチェック
        if( max_price < min_price ) {
            return badRequest("Invalid parameter [ Please set it to be 'max' > 'min' ]");
        } else if( form.containsKey("max") && form.get("max").length > 1 ) {
            return badRequest("Please set the 'max' only one");
        } else if( form.containsKey("min") && form.get("min").length > 1 ) {
            return badRequest("Please set the 'min' only one");
        }

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode rootNode = mapper.createArrayNode();

        try{
            // 全商品データを読み込む
            File file = new File("data/product.json");
            JsonNode current;
            for(int i=0; (current = mapper.readTree(file).get(i)) != null; i++) {
                String title = current.get("title").textValue();
                String description = current.get("description").textValue();
                int price = current.get("price").asInt();

                // 検索条件と一致するデータを後に出力するrootNodeに追加
                int flag = 0;
                for( String word : words ) {
                    if( !(title.contains(word) || description.contains(word)) ) {
                        flag = 1;
                        break;
                    }
                }
                if( flag == 0 && min_price <= price && price <= max_price ) {
                    rootNode.add(current);
                }
            }

        } catch( IOException e ) {
            e.printStackTrace();
            return badRequest("Failed to open file.");
        }

        return ok(Json.prettyPrint(rootNode));
    }

    /********************
        商品データ削除
     ********************/
    public Result delete() {

        String[] words = {};
        int price = 0;

        // POSTされたパラメータを取得
        Map<String, String[]> form = request().body().asFormUrlEncoded();
        if( form.containsKey("word") )  words = form.get("word");
        if( form.containsKey("price") ) price = Integer.parseInt(form.get("price")[0]);

        // パラメータチェック
        if( !(form.containsKey("word") && form.containsKey("price")) ) {
            return badRequest("Please set 'word' and 'price' to delete product");
        } else if( form.containsKey("price") && form.get("price").length > 1 ) {
            return badRequest("Please set the 'price' only one");
        }

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode rootNode = mapper.createArrayNode();
        ArrayNode deleteNode = mapper.createArrayNode();

        try{
            // 全商品データを読み込む
            File file = new File("data/product.json");
            JsonNode current;
            for(int i=0; (current = mapper.readTree(file).get(i)) != null; i++) {
                String title = current.get("title").textValue();
                int product_price = current.get("price").asInt();

                // 検索条件と一致していればdeleteNodeに、一致していなければrootNodeに追加
                int flag = 1;
                for( String word : words ) {
                    if( !title.contains(word) ) {
                        flag = 0;
                        break;
                    }
                }
                if( flag == 1 && (price == product_price) ) {
                    deleteNode.add(current);
                } else {
                    rootNode.add(current);
                }
            }

            // rootNodeにあるデータ（検索条件に一致しないデータ）をファイルに書きこむ
            mapper.writeValue(file, rootNode);

        } catch( IOException e ) {
            e.printStackTrace();
            return badRequest("Failed to open or write file.");
        }

        // 削除されたデータを表示
        return ok(Json.prettyPrint(deleteNode));
    }
}