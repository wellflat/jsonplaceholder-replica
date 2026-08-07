# JSONPlaceholder Replica (Spring Boot)

Spring Boot で構築した **JSONPlaceholder 互換の Web API** 学習プロジェクトです。  
RESTful API の基本構造から、3層アーキテクチャ、MyBatis XML マッパー、バリデーション、共通例外処理、ページネーション、Docker コンテナ化、自動テスト（JUnit 5）までを網羅しています。

---

## 🛠 技術スタック

| 分類 | 技術 / ライブラリ | バージョン / 詳細 |
| :--- | :--- | :--- |
| **Language / Framework** | Java / Spring Boot | Java 25 / Spring Boot 4.0.7 |
| **Data Access** | MyBatis / H2 Database | MyBatis 4.0.1 (XML Mapper) / H2 In-Memory DB |
| **API Doc / Validation** | Springdoc OpenAPI / Jakarta Validation | Swagger UI (OpenAPI 3.0) / `@Valid` |
| **Testing** | JUnit 5 / MockMvc / MyBatis Test | `@SpringBootTest`, `@WebMvcTest`, `@MybatisTest` |
| **Container / Tool** | Docker / Docker Compose / Lombok | Multi-stage / JRE 25 Alpine |

---

## 🚀 エンドポイント仕様一覧 (`/posts`)

| HTTP Method | パス | 説明 | 備考 |
| :--- | :--- | :--- | :--- |
| **GET** | `/posts` | 投稿一覧の取得 | クエリパラメータ対応<br>(`userId`, `_page`, `_limit`, `_sort`, `_order`)<br>ヘッダー: `X-Total-Count` 出力 |
| **GET** | `/posts/{id}` | 投稿の1件取得 | 存在しない場合は `404 Not Found` (ProblemDetail) |
| **POST** | `/posts` | 投稿の新規作成 | リクエストボディ検証 (`@Valid`) |
| **PUT** | `/posts/{id}` | 投稿の全体更新 (置換) | 全フィールド上書き |
| **PATCH** | `/posts/{id}` | 投稿の一部更新 (動的) | リクエストに含まれる項目のみ更新 |
| **DELETE** | `/posts/{id}` | 投稿の削除 | 成功時 `204 No Content` |

---

## 📖 アクセス可能なツール & ドキュメント

アプリケーション起動後、以下の URL にブラウザからアクセスできます。

- **Swagger UI (APIインタラクティブ仕様書)**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2 Console (Web DB管理画面)**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - **JDBC URL**: `jdbc:h2:mem:jsondb`
  - **User**: `sa` / **Password**: (空欄)

---

## 🏃 実行手順

### 1. ローカルで直接起動する場合 (Gradle)

```bash
# アプリケーションの起動
./gradlew bootRun

# テストの全件実行 (全19件のテストがパスします)
./gradlew test

# プロダクション用 JAR ファイルの作成
./gradlew bootJar
```

### 2. Docker コンテナで起動する場合 (Docker Compose)

```bash
# コンテナのビルド ＆ 起動
docker compose up -d --build

# 動作確認 (cURL)
curl -i http://localhost:8080/posts
curl -i "http://localhost:8080/posts?_page=1&_limit=2"

# コンテナの停止
docker compose down
```

---

## 📂 プロジェクト構造

```text
src/
├── main/
│   ├── java/net/wellflat/jsonplaceholder/
│   │   ├── config/            # OpenAPI (Swagger) 設定
│   │   ├── exception/         # @RestControllerAdvice, ProblemDetail 例外ハンドラー
│   │   └── post/              # Post ドメイン (Controller, Service, Mapper, DTO)
│   └── resources/
│       ├── application.properties
│       ├── schema.sql         # H2 DB テーブル定義
│       ├── data.sql           # H2 DB 初期データ
│       └── mapper/
│           └── PostMapper.xml # MyBatis XML マッパー (動的SQL)
└── test/
    └── java/net/wellflat/jsonplaceholder/
        └── post/              # PostControllerTest, PostMapperTest
```
