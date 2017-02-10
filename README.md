# ProductManager

商品データの登録・検索・削除を行う、RESTfulなAPIアプリケーション  

## 商品データ  
| パラメータ | 型　| 必須 | サイズ | 説明　|  
| ---- | ---- | ---- | ---- | ---- |  
| id | 数値 |  | 　 | 商品id |  
| image | 文字列 |  　| 　 | 商品画像名 |  
| title | 文字列 | ◯　| 最大100文字 | 商品タイトル |  
| description | 文字列 |  　| 最大500文字 | 商品の説明文 |  
| price | 数値 | ○ | 　 | 価格 |  
※　必須　・・・　登録時に入力パラメータとして必須か  


## routes  

| HTTPメソッド | URL | 説明 |  
| ---- | ---- | ---- |  
| GET | /products/form | 登録フォームへアクセス |  
| GET | /products/form/:id | 更新フォームへアクセス |  
| POST | /products | 登録 |  
| POST | /products/:id | idと一致する商品データを更新 |  
| GET | /products | 全商品データ検索 |  
| GET | /products/:id | idによる商品データ検索 |  
| DELETE | /products/:id | 削除 |  
　　  

----------------

## 登録フォーム・更新フォーム
- (登録)　URL : /products/form
- (更新)　URL : /products/form/:id

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
<code>curl -F 'image=@/path/to/image_file' -F 'title=sample' -F 'description=cheap' -F 'price=298' http://localhost:9000/products</code>
　  
　  
   
## 更新  
- HTTPメソッド : POST  
- URL : /products/:id
- 入力パラメータ : フォームによる商品データ
- レスポンス形式 : JSON  
- レスポンス内容 : 登録後の全商品データ  
- レスポンスステータス : （成功した場合）201, (失敗した場合)400

入力パラメータに基づいて商品データを更新。  
登録時とは違い、URLとは別に入力パラメータにidを指定する必要がある。  
　　  
**リクエスト例**  
<code>curl -F 'id=7' -F 'image=@/path/to/image_file' -F 'title=sample' -F 'description=cheap' -F 'price=298' http://localhost:9000/products/7</code>
　  
　  

## 商品検索  
- HTTPメソッド : GET  
- URL : /products  
- レスポンス形式 : JSON  
- レスポンス内容 : 全商品データ
- レスポンスステータス : （成功した場合）200, (失敗した場合)400

検索条件を入力パラメータで指定して、該当する商品データを表示  
- keyword (String型): 検索キーワード
- max (long型): 最大価格
- min (long型): 最小価格  

検索条件を指定しなければ、全商品データを表示する  

**リクエスト例**  
<code>curl http://localhost:9000/products?keyword=cheap&max=500&min=200</code>  
　  
　  

## IDによる商品検索   
- HTTPメソッド : GET  
- URL : /products/:id  
- レスポンス形式 : JSON  
- レスポンス内容 : 検索条件に一致する商品データ
- レスポンスステータス : （成功した場合）200, (失敗した場合)400

IDによる商品検索を行う  

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
<code>curl -X DELETE http://localhost:9000/products/7</code>
