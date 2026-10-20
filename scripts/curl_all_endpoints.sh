#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-${BASE_URL:-http://localhost:8080}}"

print_header() {
  echo "================================================================================"
  echo "$1"
  echo "================================================================================"
}

request() {
  local method="$1"
  local path="$2"
  shift 2
  echo ">>> ${method} ${BASE_URL}${path}"
  curl -sS -i -X "${method}" "${BASE_URL}${path}" "$@"
  echo -e "\n"
}

# 1. GET /posts (全件取得)
print_header "1. GET /posts (投稿一覧の取得)"
request GET "/posts"

# 2. GET /posts (クエリパラメータ指定: フィルタ・ページネーション・ソート)
print_header "2. GET /posts?userId=1&_page=1&_limit=2&_sort=id&_order=desc (条件付き取得)"
request GET "/posts?userId=1&_page=1&_limit=2&_sort=id&_order=desc"

# 3. GET /posts/{id} (1件取得)
print_header "3. GET /posts/1 (投稿の1件取得)"
request GET "/posts/1"

# 4. POST /posts (新規作成)
print_header "4. POST /posts (投稿の新規作成)"
echo ">>> POST ${BASE_URL}/posts"
POST_RESPONSE=$(curl -sS -i -X POST "${BASE_URL}/posts" \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "title": "テスト投稿タイトル", "body": "テスト投稿の本文です。"}')
echo "${POST_RESPONSE}"
echo ""

# 作成された投稿のIDを抽出 (初期データを壊さず後続の更新・削除をテストするため)
CREATED_ID=$(echo "${POST_RESPONSE}" | grep -oE '"id"\s*:\s*[0-9]+' | head -n 1 | grep -oE '[0-9]+' || echo "4")

# 5. PUT /posts/{id} (全体更新)
print_header "5. PUT /posts/${CREATED_ID} (投稿の全体更新)"
request PUT "/posts/${CREATED_ID}" \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "title": "PUTで更新したタイトル", "body": "PUTで更新した本文です。"}'

# 6. PATCH /posts/{id} (部分更新)
print_header "6. PATCH /posts/${CREATED_ID} (投稿の一部更新)"
request PATCH "/posts/${CREATED_ID}" \
  -H "Content-Type: application/json" \
  -d '{"title": "PATCHで部分更新したタイトル"}'

# 7. DELETE /posts/{id} (削除)
print_header "7. DELETE /posts/${CREATED_ID} (投稿の削除)"
request DELETE "/posts/${CREATED_ID}"

# 8. Spring Boot Actuator & OpenAPI エンドポイント
print_header "8. GET /actuator/health (ヘルスチェック)"
request GET "/actuator/health"

print_header "9. GET /actuator/info (アプリケーション情報)"
request GET "/actuator/info"

print_header "10. GET /actuator/metrics (メトリクス一覧)"
request GET "/actuator/metrics"

print_header "11. GET /v3/api-docs (OpenAPI 仕様 JSON)"
request GET "/v3/api-docs"

echo "すべてのエンドポイントへのアクセスが完了しました。"
