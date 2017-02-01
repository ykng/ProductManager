# ProductManager

商品データの登録・検索・削除を行う、RESTfulなAPIアプリケーション  

## 商品データ  
| パラメータ | 型　| 必須 | サイズ | 説明　|  
| ---- | ---- | ---- | ---- | ---- |  
| id | 数値 |  | 　 | 商品id |  
| image_url | 文字列 | ◯　| 　 | 商品画像URL |  
| title | 文字列 | ◯　| 最大100文字 | 商品タイトル |  
| description | 文字列 |  　| 最大500文字 | 商品の説明文 |  
| price | 数値 | ○ | 　 | 価格 |  
※　必須　・・・　登録時に入力パラメータとして必須か  


## routes  

| HTTPメソッド | URL | 説明 |  
| ---- | ---- | ---- |  
| GET | /products/register | 登録フォームへアクセス |  
| GET | /products/update/:id | 更新フォームへアクセス |  
| POST | /products | 登録 |  
| PATCH | /products | idと一致する商品データを更新 |
| GET | /products | 全商品データ検索 |  
| GET | /products/:id | idによる商品データ検索 |  
| DELETE | /products/:id | 削除 |  
　　  

----------------

## 登録フォーム・更新フォーム
- (登録)　URL : /products/register
- (更新)　URL : /products/update/:id

登録・更新をブラウザから行う。  
両者とも画面は一緒だが、隠しパラメータとしてidが格納（登録時：NULL、更新時：URLで指定したid）されており、NULLかどうかで登録か更新か判定する。  
　　  
    
----------------

## 登録
- HTTPメソッド : POST
- URL : /products
- 入力パラメータ : フォームによる商品データ(idを除く)  
- レスポンス形式 : JSON  
- レスポンス内容 : 登録後の全商品データ  
- レスポンスステータス : （成功した場合）201, (失敗した場合)400

入力パラメータに基づいて商品データをデータベースに登録。
ただし、IDについては商品データ保存時に自動的に決定されるので入力する必要はない。  
　  
**リクエスト例**  
<code>curl -F 'image_url=image/url' -F 'title=sample_product' -F 'description=cheap' -F 'price=298' http://localhost:9000/products</code>
　  
　  
   
## 更新  
- HTTPメソッド : PATCH  
- URL : /products
- 入力パラメータ : フォームによる商品データ
- レスポンス形式 : JSON  
- レスポンス内容 : 登録後の全商品データ  
- レスポンスステータス : （成功した場合）201, (失敗した場合)400

入力パラメータに基づいて商品データを更新。  
登録時とは違い、入力パラメータにidを指定する必要がある。  
　　  
**リクエスト例**  
<code>curl -F 'id=3' -F 'image_url=image/url3' -F 'title=sample_product3' -F 'description=BestPrice' -F 'price=598' http://localhost:9000/products</code>
　  
　  

## 全商品検索  
- HTTPメソッド : GET  
- URL : /products  
- レスポンス形式 : JSON  
- レスポンス内容 : 全商品データ
- レスポンスステータス : （成功した場合）200, (失敗した場合)400

**リクエスト例**  
<code>curl http://localhost:9000/products</code>  
　  
　  

## IDによる商品検索   
- HTTPメソッド : GET  
- URL : /products/:id  
- レスポンス形式 : JSON  
- レスポンス内容 : 検索条件に一致する商品データ
- レスポンスステータス : （成功した場合）200, (失敗した場合)400

**リクエスト例**  
<code>curl http://localhost:9000/products/7</code>  
　  
　  
   
## 削除
- HTTPメソッド : DELETE  
- URL : /products/:id  
- レスポンス形式 : JSON  
- レスポンス内容 : 削除した商品データ
- レスポンスステータス : （成功した場合）200, (失敗した場合)400
  
idによる検索を行い、該当する商品を削除。  

**リクエスト例**  
<code>curl -X DELETE http://localhost:9000/products/7 -H "Content-Type: application/json"</code>
