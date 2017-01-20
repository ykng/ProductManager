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

public class DeleteCondition {

    private int id;
    private String keyword;
    private int price;

    // setter
    public void setId(int id) { this.id = id; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public void setPrice(int price) { this.price = price; }

    // getter
    public int getId() { return this.id; }
    public String getKeyword() { return this.keyword; }
    public int getPrice() { return this.price; }

    // BodyParser
    public static class SearchConditionBodyParser implements BodyParser<DeleteCondition> {

        private BodyParser.Json jsonParser;
        private Executor executor;

        @Inject
        public SearchConditionBodyParser(BodyParser.Json jsonParser, Executor executor) {
            this.jsonParser = jsonParser;
            this.executor = executor;
        }

        public Accumulator<ByteString, F.Either<Result, DeleteCondition>> apply(Http.RequestHeader request) {
            Accumulator<ByteString, F.Either<Result, JsonNode>> jsonAccumulator = jsonParser.apply(request);
            return jsonAccumulator.map(resultOrJson -> {
                if (resultOrJson.left.isPresent()) {
                    return F.Either.Left(resultOrJson.left.get());
                } else {
                    JsonNode json = resultOrJson.right.get();
                    try {
                        DeleteCondition sc = play.libs.Json.fromJson(json, DeleteCondition.class);
                        List<String> errors = new ArrayList<>();
                        if( sc.getId() == 0 ){
                            errors.add("Missing parameter! ['id' is required]");
                        }
                        return errors.isEmpty() ? F.Either.Right(sc) : F.Either.Left(Results.badRequest(errors.toString()));
                    } catch (Exception e) {
                        return F.Either.Left(Results.badRequest("Unable to read Product from json: " + e.getMessage()));
                    }
                }
            }, executor);
        }
    }

}

