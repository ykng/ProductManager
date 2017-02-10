package models;

import play.mvc.Http;

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
}
