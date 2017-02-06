package models;

import com.avaje.ebean.Expr;

import java.util.List;

public class SearchCondition {

    public String keyword = "";
    public long max = Long.MAX_VALUE;
    public long min = 0;

    public List<Product> search(){
        return Product.find.where()
                .or(Expr.contains("title", this.keyword), Expr.contains("description", this.keyword))
                .between("price", this.min, this.max)
                .findList();
    }
}
