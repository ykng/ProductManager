package models;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class Product {

    private int id;
    private String image_url;
    private String title;
    private String description;
    private int price;

    // setter
    public void setId(int id) { this.id = id; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(int price) { this.price = price; }

    // getter
    public int getId(){ return this.id; }
    public String getImage_url(){ return this.image_url; }
    public String getTitle(){ return this.title; }
    public String getDescription(){ return this.description; }
    public int getPrice(){ return this.price; }

    public void setParam(String param_name, Object param) {
        switch ( param_name ) {
            case "id" :
                setId((int) param);
                break;
            case "image_url" :
                setImage_url((String) param);
                break;
            case "title" :
                setTitle((String) param);
                break;
            case "description" :
                setDescription((String) param);
                break;
            case "price" :
                setPrice((int) param);
                break;
        }
    }

    // BodyParser
    public static class ProductBodyParser implements BodyParser<Product> {

        private BodyParser.Json jsonParser;
        private Executor executor;

        @Inject
        public ProductBodyParser(BodyParser.Json jsonParser, Executor executor) {
            this.jsonParser = jsonParser;
            this.executor = executor;
        }

        public Accumulator<ByteString, F.Either<Result, Product>> apply(Http.RequestHeader request) {
            Accumulator<ByteString, F.Either<Result, JsonNode>> jsonAccumulator = jsonParser.apply(request);
            return jsonAccumulator.map(resultOrJson -> {
                if (resultOrJson.left.isPresent()) {
                    return F.Either.Left(resultOrJson.left.get());
                } else {
                    JsonNode json = resultOrJson.right.get();
                    try {
                        Product product = play.libs.Json.fromJson(json, Product.class);

                        List<String> errors = new ArrayList<>();
                        if( product.getId() < 0 )                errors.add("Invalid parameter! ['id' must be positive]");
                        if( product.getImage_url().isEmpty() )   errors.add("Missing parameter! [image_url]");
                        if( product.getTitle().isEmpty() )       errors.add("Missing parameter! [title]");
                        if( product.getDescription().isEmpty() ) errors.add("Missing parameter! [description]");
                        if( product.getPrice() < 0)              errors.add("Invalid parameter! ['price' must be positive]");
                        if( product.getTitle().length() > 100 ) {
                            errors.add("'title' is too long! ['title' length is 100 characters]");
                        }
                        if( product.getDescription().length() > 500 ) {
                            errors.add("'description' is too long! ['description' length is 100 characters]");
                        }

                        return errors.isEmpty() ? F.Either.Right(product) : F.Either.Left(Results.badRequest(errors.toString()));
                    } catch (Exception e) {
                        return F.Either.Left(Results.badRequest("Unable to read Product from json: " + e.getMessage()));
                    }
                }
            }, executor);
        }
    }
}
