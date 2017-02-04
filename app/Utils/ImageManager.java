package Utils;

import play.Application;
import play.mvc.Http;

import javax.inject.Provider;
import java.io.File;

public class ImageManager {

    public static Http.MultipartFormData.FilePart<File> upload(Http.Request request, Provider<Application> appProvider) {
        Http.MultipartFormData<File> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> image = body.getFile("image");
        if (image != null) {
            String fileName = image.getFilename();
            String contentType = image.getContentType();
            File file = image.getFile();

            // TODO 画像ファイルだけ保存するようにしたいかな
            String fullPath = appProvider.get().path().getPath() + "/public/images/upload/";
            file.renameTo(new File(fullPath, fileName));
        }
        return image;
    }

}
