package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Product extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Required
    public String image_url;

    @Required
    @MaxLength(100)
    public String title;

    @MaxLength(500)
    public String description;

    @Required
    public Long price;

    public static Finder<Long, Product> find = new Finder<Long, Product>(Product.class);

}
