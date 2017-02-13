package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Image extends Model {

    @Id
    @Constraints.Required
    public long id;

    public String name;

    public String contentType;

    public Image(long id, String name, String contentType) {
        this.id = id;
        this.name = name;
        this.contentType = contentType;
    }

    public static Finder<Long, Image> find = new Finder<Long, Image>(Image.class);
}
