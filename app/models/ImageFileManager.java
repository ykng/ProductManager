package models;

import play.Application;
import play.mvc.Http;

import javax.inject.Provider;
import java.io.File;

public class ImageFileManager {

    public static Http.MultipartFormData.FilePart<File> upload(Http.Request request) {
        Http.MultipartFormData<File> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> image = body.getFile("image");
        if (image.getContentType().contains("image")) {
            return image;
        }
        return new Http.MultipartFormData.FilePart<File>(
                "default", "default.png","image/png", new File("/dummy"));
    }
    public static void renameFile(Provider<Application> appProvider, File file, Image image) {
        if (image.contentType.contains("image")) {
            String fullPath = appProvider.get().path().getPath() + "/public/images/upload/";
            file.renameTo(new File(fullPath, String.valueOf(image.id) + "_" + image.name));
        }
    }

    public static void deleteFile(Provider<Application> appProvider, Image image) {
        if (!image.name.equals("default.png")) {
            String fullPath = appProvider.get().path().getPath() + "/public/images/upload/";
            new File(fullPath + String.valueOf(image.id) + "_" + image.name).delete();
        }
    }
}
