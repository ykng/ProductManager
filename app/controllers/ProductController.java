package controllers;

import models.Product;
import models.SearchCondition;
import play.db.Database;
import play.mvc.*;
import utils.Converter;

import javax.inject.Inject;
import java.sql.*;
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
    @BodyParser.Of(Product.ProductBodyParser.class)
    public Result register() {

        //　リクエストパラメータ取得
        Product product = request().body().as(Product.class);

        // クエリ作成
        StringBuilder sb = new StringBuilder();
        String query_r = sb.append("INSERT INTO PRODUCT VALUES ( '")
                .append(String.valueOf(product.getId())).append("', '")
                .append(product.getImage_url()).append("', '")
                .append(product.getTitle()).append("', '")
                .append(product.getDescription()).append("', '")
                .append(String.valueOf(product.getPrice())).append("' );")
                .toString();
        String query_s = "SELECT * FROM product";

        try {
            // DBに商品データ登録
            stmt.executeUpdate(query_r);

            // 全商品データ検索
            ResultSet rs = stmt.executeQuery(query_s);
            return created(Converter.convertResultSetToJson(rs));

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
            return ok(Converter.convertResultSetToJson(rs));

        } catch(SQLException e) {
            e.printStackTrace();
            return badRequest("Failed to execute sql operation");
        }
    }

    /********************
        商品データ削除
     ********************/
    @BodyParser.Of(SearchCondition.SearchConditionBodyParser.class)
    public Result delete() {

        // リクエストパラメータを取得
        SearchCondition sc = request().body().as(SearchCondition.class);

        //　クエリ作成
        String query_select = "SELECT * FROM PRODUCT WHERE ";
        String query_delete = "DELETE FROM PRODUCT WHERE ";
        StringBuilder sb = new StringBuilder();
        if( !sc.getKeyword().isEmpty() ) {
            String word = sc.getKeyword();
            sb.append("(title LIKE '%").append(word).append("%' OR description LIKE '%").append(word).append("%') AND ");
        }
        if( sc.getPrice() != 0 ) {
            sb.append("price = ").append(String.valueOf(sc.getPrice())).append(" AND ");
        }
        sb.append("id = ").append(String.valueOf(sc.getId()));

        try {
            //　削除するデータを表示用に取り置き
            ResultSet rs = stmt.executeQuery( query_select + sb.toString() );
            String deleted_product_data = Converter.convertResultSetToJson(rs);

            //  DBからデータ削除
            stmt.executeUpdate( query_delete + sb.toString() );

            //  削除したデータを表示
            return ok(deleted_product_data);

        } catch(SQLException e) {
            e.printStackTrace();
            return badRequest("Failed to execute sql operation");
        }
    }
}