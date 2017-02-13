package controllers;

import models.Image;
import models.Product;
import models.SearchCondition;
import play.Application;
import play.data.Form;
import play.data.FormFactory;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ProductController extends Controller{

    private Provider<Application> appProvider;
    private Form<SearchCondition> searchForm;

    @Inject
    public ProductController(Provider<Application> applicationProvider, FormFactory formFactory) {
        this.appProvider = applicationProvider;
        this.searchForm = formFactory.form(SearchCondition.class);
    }

    /********************
        商品データ登録
     ********************/
    @Transactional
    public Result register() {
        return FormController.registerOrUpdate(false, appProvider);
    }

    /********************
         商品データ更新
     ********************/
    @Transactional
    public Result update(long id) {
        if (Product.find.byId(id) == null) {
            return badRequest("There is no product with ID : " + String.valueOf(id) );
        }
        Image.find.byId(id).deleteFile(appProvider);
        return FormController.registerOrUpdate(true, appProvider);
    }

    /********************
        商品データ検索
     ********************/
    public Result search() {
        Form<SearchCondition> form = searchForm.bindFromRequest();
        return form.hasErrors()
                ? badRequest("Invalid parameter")
                : ok(Json.prettyPrint(Json.toJson(form.get().search())));
    }

    /********************
          検索 by ID
     ********************/
    public Result searchByID(long id) {
        return ok(Json.prettyPrint(Json.toJson(Product.find.byId(id))));
    }

    /********************
         商品データ削除
     ********************/
    @Transactional
    public Result delete(long id) {
        Product product = Product.find.byId(id);
        if (product == null) {
            return badRequest("There is no product with ID : " + String.valueOf(id) );
        }
        product.delete();
        Image image = Image.find.byId(id);
        image.deleteFile(appProvider);
        image.delete();
        return ok(Json.prettyPrint(Json.toJson(product)));
    }
}