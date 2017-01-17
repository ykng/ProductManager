package models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import play.libs.Json;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Converter {

    public static String convertToJsonString(ResultSet rs) throws SQLException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        ArrayNode rootNode = mapper.createArrayNode();

        while( rs.next() ) {
            Product product = new Product();
            int total_rows = rs.getMetaData().getColumnCount();
            for (int i = 0; i < total_rows; i++) {
                product.setParam(rs.getMetaData().getColumnLabel(i + 1).toLowerCase(), rs.getObject(i + 1));
            }
            rootNode.add(mapper.convertValue(product, JsonNode.class));
        }

        return Json.prettyPrint(rootNode);
    }

}
