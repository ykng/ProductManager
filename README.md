## ProductManager

商品データの登録・検索・削除を行う、RESTfulなAPIアプリケーション  

### routes  

| HTTPメソッド | URL | 説明 |  
| ---- | ---- | ---- |  
| POST | /products | 登録 |  
| GET | /products | 検索 |  
| DELETE | /products | 削除 |  

### 商品データ    
- image_url : 商品画像  
- title : 商品タイトル(最大100文字)
- description : 説明文(最大500文字)
- price : 価格  

----------------

#### 登録
- HTTPメソッド : POST
- パラメータ : 商品データのJSON  
- レスポンス形式 : JSON  
- レスポンス内容 : 検索条件にマッチする全商品データ

**リクエスト例**  
<code>
curl -X POST http://localhost:9000/products 
　　　-H "Content-Type: application/json" 
　　　-d '{"image_url":"image/sample", "title":"sample", "description":"it is sample product", "price":298}'  
</code>

#### 検索  
- HTTPメソッド : GET
- パラメータ : keyword（検索キーワード）, max（最大価格）, min（最小価格）  
- レスポンス形式 : JSON  
- レスポンス内容 : 登録後の全商品データ

**リクエスト例**  
<code>
curl http://localhost:9000/products\?keyword=cheap\&keyword=product\&max=500\&min=10  
</code>

#### 削除
- HTTPメソッド : DELETE
- パラメータ : keyword, price のJSON
- レスポンス形式 : JSON  
- レスポンス内容 : 削除した商品データ

**リクエスト例**  
<code>
curl -X DELETE http://localhost:9000/products 
　　　-H "Content-Type: application/json" 
　　　-d '{"keyword":["sample", "product"], "price":298}'
</code>
