package controllers;

import models.Image;
import models.Product;
import play.Application;
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

    @Inject
    public ProductController(Provider<Application> applicationProvider) {
        this.appProvider = applicationProvider;
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
        return FormController.registerOrUpdate(true, appProvider);
    }

    /********************
        全商品データ検索
     ********************/
    public Result searchAll() {
        return ok(Json.prettyPrint(Json.toJson(Product.find.all())));
    }

    /********************
      商品データ検索 by ID
     ********************/
    public Result search(long id) {
        return ok(Json.prettyPrint(Json.toJson(Product.find.byId(id))));
    }

    // TODO id以外での検索もできるように

    /********************
         商品データ削除
     ********************/
    @Transactional
    public Result delete(long id) {
        Product product = Product.find.byId(id);
        if( product == null ) {
            return badRequest("There is no product with ID : " + String.valueOf(id) );
        }
        product.delete();
        Image image = Image.find.byId(id);
        image.deleteFile(appProvider);
        image.delete();
        return ok(Json.prettyPrint(Json.toJson(product)));
    }
}