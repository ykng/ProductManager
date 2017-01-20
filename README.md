# ProductManager

商品データの登録・検索・削除を行う、RESTfulなAPIアプリケーション  

## 商品データ  
| パラメータ | 型　| 必須 | サイズ | 説明　|
| ---- | ---- | ---- | ---- | ---- |
| id | 数値 | ◯ | 　 | 商品id |
| image_url | 文字列 | ◯　| 　 | 商品画像URL |
| title | 文字列 | ◯　| 最大100文字 | 商品タイトル |
| description | 文字列 | ◯　| 最大500文字 | 商品の説明文 |
| price | 数値 | 　　| 　 | 価格 |


## routes  

| HTTPメソッド | URL | 説明 |  
| ---- | ---- | ---- |  
| POST | /products | 登録 |  
| GET | /products | 検索 |  
| DELETE | /products | 削除 |  
　  

----------------

## 登録
- HTTPメソッド : POST
- 入力パラメータ : 商品データのJSON  
- レスポンス形式 : JSON  
- レスポンス内容 : 登録後の全商品データ  
- レスポンスステータス : （成功した場合）201, (失敗した場合)400

入力パラメータに基づいて商品データをデータベースに登録。  
price(価格)の指定がなければ、0円となる。  
　  
**リクエスト例**  
<code>curl -X POST http://localhost:9000/products 　-H "Content-Type: application/json" -d '{"id":7, "image_url":"image/product7", "title":"product7", "description":"it is lucky product", "price":777}'</code>
　  
   
## 検索  
- HTTPメソッド : GET
- 入力パラメータ : id（商品id）, keyword（検索キーワード）, max（最大価格）, min（最小価格） [全て任意] 
- レスポンス形式 : JSON  
- レスポンス内容 : 検索条件にマッチする全商品データ
- レスポンスステータス : （成功した場合）200, (失敗した場合)400

入力したパラメータによる検索を行い、該当する商品データを全て表示。  
全てのパラメータを指定しても、複数指定でも、単独指定でも検索可能。  
パラメータの指定が1つもなければ、全商品検索になる。  
idについては完全一致かどうか、maxかminが指定されている場合は価格がその範囲内にあるか、keywordはtitleまたはdiscriptionの内容に含まれているかで検索を行う。  

**リクエスト例**  
<code>curl http://localhost:9000/products\?id=7\&keyword=lucky\&keyword=product\&max=1000\&min=100</code>
　  
   
## 削除
- HTTPメソッド : DELETE
- 入力パラメータ : id, keyword, price のJSON
- レスポンス形式 : JSON  
- レスポンス内容 : 削除した商品データ
- レスポンスステータス : （成功した場合）200, (失敗した場合)400
  
入力したパラメータによる検索を行い、該当する商品を削除。  
検索方法については、上の検索とほぼ同じ。priceについては完全一致で検索する。  

**リクエスト例**  
<code>curl -X DELETE http://localhost:9000/products -H "Content-Type: application/json" -d '{"id":7, "keyword":"sample", "price":298}'</code>
