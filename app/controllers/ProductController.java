package controllers;

import models.Product;
import play.data.Form;
import play.data.FormFactory;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProductController extends Controller{

    private Form<Product> forms;

    @Inject
    public ProductController(FormFactory formFactory) {
        this.forms = formFactory.form(Product.class);
    }

    public Result init() {
        Product product = new Product();
        return ok(views.html.index.render(forms.fill(product), false));
    }

    public Result edit(Long id) {
        Product product = Product.find.byId(id);
        if( product == null ) {
            return badRequest("There is no product with ID : " + String.valueOf(id) );
        }
        return ok(views.html.index.render(forms.fill(product), true));
    }

    /********************
        商品データ登録
     ********************/
    @Transactional
    public Result register() {
        return createOrUpdate(false);
    }

    /********************
         商品データ更新
     ********************/
    @Transactional
    public Result update() {
        return createOrUpdate(true);
    }

    private Result createOrUpdate(boolean isEdit) {
        Form<Product> requestForm = forms.bindFromRequest();
        if (requestForm.hasErrors()) {
            return badRequest(views.html.index.render(requestForm, isEdit));
        }
        Product product = requestForm.get();
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
        return ok(Json.prettyPrint(Json.toJson(product)));
    }
}