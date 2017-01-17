package controllers;

import com.fasterxml.jackson.databind.*;
import models.Converter;
import play.db.Database;
import play.mvc.*;
import javax.inject.Inject;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class ProductController extends Controller{

    private Database db;
    private Statement stmt;

    @Inject
    public ProductController(Database db) {
        this.db = db;
        Connection con = db.getConnection();

        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /********************
        商品データ登録
     ********************/
    @BodyParser.Of(BodyParser.Json.class)
    public Result register() {

        JsonNode json_input = request().body().asJson();

        //　POSTされたパラメータを取得
        int    id           = json_input.findPath("id").intValue();
        String image_url    = json_input.findPath("image_url").textValue();
        String title        = json_input.findPath("title").textValue();
        String description  = json_input.findPath("description").textValue();
        int    price        = json_input.findPath("price").intValue();

        //　POSTされたパラメータが正常な値かチェック
        if( !json_input.findPath("id").isInt() || !json_input.findPath("price").isInt() ) {
            return badRequest( "'id' or 'price' must be of type int" );
        } else if( !json_input.findPath("image_url").isTextual() || !json_input.findPath("title").isTextual()
                || !json_input.findPath("description").isTextual() ) {
            return badRequest("'image_url' or 'title' or 'description' must be of type String" );
        } else if( id == 0 || image_url == null || title == null || description == null ) {
            return badRequest( "Missing parameter! [Required: id, image_url, title, description(, price)]" );
        } else if( title.length() > 100 ) {
            return badRequest("The parameter is too long! [Title length is 100 characters]" );
        } else if( description.length() > 500 ) {
            return badRequest("The parameter is too long! [Description length is 500 characters]" );
        } else if( price < 0 ) {
            return badRequest("Invalid parameter! [The inputted 'price' value is negative]" );
        }

        // クエリ作成
        StringBuilder sb = new StringBuilder();
        String query_r = sb.append("INSERT INTO PRODUCT VALUES ( '")
                .append(String.valueOf(id)).append("', '")
                .append(image_url).append("', '")
                .append(title).append("', '")
                .append(description).append("', '")
                .append(String.valueOf(price)).append("' );")
                .toString();
        String query_s = "SELECT * FROM product";

        try {
            // 商品データ登録
            stmt.executeUpdate(query_r);

            // 全商品データ検索
            ResultSet rs = stmt.executeQuery(query_s);
            return created(Converter.convertToJsonString(rs));

        } catch(SQLException e) {
            e.printStackTrace();
            return badRequest("Failed to execute sql operation");
        }
    }

    /********************
        商品データ検索
     ********************/
    public Result search() {

        int id = 0;
        String[] keywords = {};
        int max_price = Integer.MAX_VALUE;
        int min_price = 0;

        // リクエストパラメータを取得
        Map<String, String[]> form = request().queryString();
        if( form.containsKey("id") )       id        = Integer.parseInt(form.get("id")[0]);
        if( form.containsKey("keyword") )  keywords  = form.get("keyword");
        if( form.containsKey("max") )      max_price = Integer.parseInt(form.get("max")[0]);
        if( form.containsKey("min") )      min_price = Integer.parseInt(form.get("min")[0]);

        // パラメータチェック
        if( max_price < min_price ) {
            return badRequest("Invalid parameter [ Please set it to be 'max' > 'min' ]");
        } else if( form.containsKey("id") && form.get("id").length > 1 ) {
            return badRequest("Please set the 'id' only one");
        } else if( form.containsKey("max") && form.get("max").length > 1 ) {
            return badRequest("Please set the 'max' only one");
        } else if( form.containsKey("min") && form.get("min").length > 1 ) {
            return badRequest("Please set the 'min' only one");
        }

        //　クエリ作成
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM PRODUCT WHERE ");
        if( id != 0 ) {
            sb.append("id = ").append(String.valueOf(id)).append(" AND ");
        }
        for( String word : keywords ) {
            sb.append("(title LIKE '%").append(word).append("%' OR description LIKE '%").append(word).append("%') AND ");
        }
        sb.append("price BETWEEN ").append(String.valueOf(min_price)).append(" AND ").append(String.valueOf(max_price));
        String query = sb.toString();

        try {
            // データ検索
            ResultSet rs = stmt.executeQuery(query);
            return ok(Converter.convertToJsonString(rs));

        } catch(SQLException e) {
            e.printStackTrace();
            return badRequest("Failed to execute sql operation");
        }
    }

    /********************
        商品データ削除
     ********************/
    @BodyParser.Of(BodyParser.Json.class)
    public Result delete() {

        JsonNode json_input = request().body().asJson();

        //　リクエストパラメータを取得
        int          id        = json_input.findPath("id").intValue();
        List<String> keywords  = json_input.findValuesAsText("keyword");
        int          price     = json_input.findPath("price").intValue();

        // パラメータチェック
        if( !json_input.findPath("id").isInt() || !json_input.findPath("price").isInt() ) {
            return badRequest("'id' or 'price' must be of type int");
        } else if( id == 0 && keywords.isEmpty() && price == 0 ) {
            return badRequest("Missing parameter! ['id' or 'keyword' or 'price' is required]");
        } else if( price < 0 ) {
            return badRequest("Invalid parameter! [The inputted 'price' value is negative]");
        }

        //　クエリ作成
        String query_select = "SELECT * FROM PRODUCT WHERE ";
        String query_delete = "DELETE FROM PRODUCT WHERE ";

        StringBuilder sb = new StringBuilder();
        if( id != 0 ) {
            sb.append("id = ").append(String.valueOf(id)).append(" AND ");
        }
        for( String word : keywords ) {
            sb.append("(title LIKE '%").append(word).append("%' OR description LIKE '%").append(word).append("%') AND ");
        }
        sb.append("price = ").append(String.valueOf(price));

        try {
            //　削除するデータを格納
            ResultSet rs = stmt.executeQuery( query_select + sb.toString() );
            String deleted_product_data = Converter.convertToJsonString(rs);

            //  データ削除
            stmt.executeUpdate( query_delete + sb.toString() );

            //  削除したデータを返す
            return ok(deleted_product_data);

        } catch(SQLException e) {
            e.printStackTrace();
            return badRequest("Failed to execute sql operation");
        }
    }
}