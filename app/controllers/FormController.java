package controllers;

import models.ImageFileManager;
import models.Image;
import models.Product;
import play.Application;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.File;

@Singleton
public class FormController extends Controller {

    private static Form<Product> forms;

    @Inject
    public FormController(FormFactory formFactory) {
        this.forms = formFactory.form(Product.class);
    }

    /********************
      　　登録フォーム
     ********************/
    public Result init() {
        return ok(views.html.form.render(forms.fill(new Product()), false, "Register Form"));
    }

    /********************
     　　 編集フォーム
     ********************/
    public Result edit(long id) {
        Product product = Product.find.byId(id);
        if( product == null ) {
            return badRequest("There is no product with ID : " + String.valueOf(id));
        }
        return ok(views.html.form.render(
                forms.fill(product), true, "Edit product data of ID : " + String.valueOf(id)));
    }

    protected static Result registerOrUpdate(boolean isEdit, Provider<Application> appProvider) {
        Form<Product> requestForm = forms.bindFromRequest();
        if (requestForm.hasErrors()) {
            return badRequest(views.html.form.render(requestForm, isEdit,
                    "Invalid parameter is included or there is no entry for required item"));
        }
        Http.MultipartFormData.FilePart<File> file = ImageFileManager.upload(request());

        /** 商品データ登録・更新 **/
        Product product = requestForm.get();
        product.image = file.getFilename();
        if (!isEdit) {
            product.save();
        } else {
            product.update();
        }

        /** 画像データ登録・更新 **/
        Image image = new Image(product.id, file.getFilename(), file.getContentType());
        image.renameFile(appProvider, file.getFile());
        if (!isEdit) {
            image.save();
            return created(Json.prettyPrint(Json.toJson(Product.find.all())));
        } else {
            image.update();
            return created(Json.prettyPrint(Json.toJson(Product.find.byId(product.id))));
        }
    }

}
