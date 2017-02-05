package models;

import com.avaje.ebean.Model;
import play.Application;
import play.data.validation.Constraints;

import javax.inject.Provider;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.File;

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

    public void renameFile(Provider<Application> appProvider, File file) {
        if (this.contentType.contains("image")) {
            String fullPath = appProvider.get().path().getPath() + "/public/images/upload/";
            file.renameTo(new File(fullPath, String.valueOf(this.id) + "_" + this.name));
        }
    }

    public void deleteFile(Provider<Application> appProvider) {
        if (!this.name.equals("default.png")) {
            String fullPath = appProvider.get().path().getPath() + "/public/images/upload/";
            new File(fullPath + String.valueOf(this.id) + "_" + this.name).delete();
        }
    }
}
