# ProductManager

商品データの登録・検索・削除を行う、RESTfulなAPIアプリケーション  

## routes  

| HTTPメソッド | URL | 説明 |  
| ---- | ---- | ---- |  
| POST | /products | 登録 |  
| GET | /products | 検索 |  
| DELETE | /products | 削除 |  

## 商品データ  
- id : 商品id
- image_url : 商品画像  
- title : 商品タイトル(最大100文字)
- description : 説明文(最大500文字)
- price : 価格  

----------------

## 登録
- HTTPメソッド : POST
- パラメータ : 商品データのJSON  
- レスポンス形式 : JSON  
- レスポンス内容 : 検索条件にマッチする全商品データ  
  
入力データに基づいて商品データをデータベースに登録。
登録後の全商品一覧を表示する。  
  
**リクエスト例**  
　　<code>
curl -X POST http://localhost:9000/products 
　　　-H "Content-Type: application/json" 
　　　-d '{"id":7, "image_url":"image/product7", "title":"product7", "description":"it is lucky product", "price":777}'  
　　</code>

## 検索  
- HTTPメソッド : GET
- パラメータ : id（商品id）, keyword（検索キーワード）, max（最大価格）, min（最小価格）  
- レスポンス形式 : JSON  
- レスポンス内容 : 登録後の全商品データ

入力したパラメータによる検索を行い、該当する商品データを全て表示。  
全てのパラメータを指定しても、複数指定でも、単独指定でも検索可能。  
idとpriceについては完全一致かどうか、keywordはtitleまたはdiscriptionの内容に含まれているかで検索を行う。  

**リクエスト例**  
<code>
curl http://localhost:9000/products\?id=7\&keyword=lucky\&keyword=product\&max=1000\&min=100  
</code>

## 削除
- HTTPメソッド : DELETE
- パラメータ : id, keyword, price のJSON
- レスポンス形式 : JSON  
- レスポンス内容 : 削除した商品データ
  
入力したパラメータによる検索を行い、該当する商品を削除。  
検索方法については、上の検索と同じ。  

**リクエスト例**  
<code>
curl -X DELETE http://localhost:9000/products 
　　　-H "Content-Type: application/json" 
　　　-d '{"id":7, "keyword":["sample", "product"], "price":298}'
</code>
