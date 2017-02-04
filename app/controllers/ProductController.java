package controllers;

import Utils.ImageManager;
import models.Product;
import play.Application;
import play.data.Form;
import play.data.FormFactory;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ProductController extends Controller{

    private Form<Product> forms;
    private Provider<Application> appProvider;

    @Inject
    public ProductController(FormFactory formFactory, Provider<Application> applicationProvider) {
        this.forms = formFactory.form(Product.class);
        this.appProvider = applicationProvider;
    }

    public Result init() {
        Product product = new Product();
        return ok(views.html.index.render(forms.fill(product), false, "Register Form"));
    }

    public Result edit(Long id) {
        Product product = Product.find.byId(id);
        if( product == null ) {
            return badRequest("There is no product with ID : " + String.valueOf(id) );
        }
        // TODO 商品画像が渡されていない
        return ok(views.html.index.render(forms.fill(product), true, "Edit product data of ID " + String.valueOf(id)));
    }

    /********************
        商品データ登録
     ********************/
    @Transactional
    public Result register() {
        return registerOrUpdate(false);
    }

    /********************
         商品データ更新
     ********************/
    @Transactional
    public Result update() {
        return registerOrUpdate(true);
    }

    private Result registerOrUpdate(boolean isEdit){
        Form<Product> requestForm = forms.bindFromRequest();
        if (requestForm.hasErrors()) {
            return badRequest("Illegal parameter is included or there is no entry for required item");
        }
        Product product = requestForm.get();
        product.image = ImageManager.upload(request(), appProvider).getFilename();
        if( product.id == null ) {
            product.save();
        } else {
            product.update();
        }
        return created(Json.prettyPrint(Json.toJson(Product.find.all())));
    }

    /********************
        全商品データ検索
     ********************/
    public Result searchAll() {
        return ok(Json.prettyPrint(Json.toJson(Product.find.all())));
    }

    /********************
     　IDで商品データ検索
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
        // TODO 対応する画像も削除
        return ok(Json.prettyPrint(Json.toJson(product)));
    }
}