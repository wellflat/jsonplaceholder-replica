#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ローカル設定ファイル (config.env) が存在する場合は読み込み
if [ -f "${SCRIPT_DIR}/config.env" ]; then
  # shellcheck source=/dev/null
  source "${SCRIPT_DIR}/config.env"
fi

BASE_URL="${BASE_URL:-https://YOUR_CLOUD_RUN_URL}"

# 第1引数: エンドポイントパス (例: /posts, /posts/1) または フルURL
ENDPOINT="${1:-/posts}"
if [ $# -gt 0 ]; then
  shift
fi

# フルURLが指定されているか、相対パスかを判定
if [[ "${ENDPOINT}" =~ ^https?:// ]]; then
  TARGET_URL="${ENDPOINT}"
else
  if [[ "${ENDPOINT}" != /* ]]; then
    ENDPOINT="/${ENDPOINT}"
  fi
  TARGET_URL="${BASE_URL}${ENDPOINT}"
fi

# gcloud から ID トークンを取得
TOKEN=$(gcloud auth print-identity-token 2>/dev/null)

if [ -z "${TOKEN}" ]; then
  echo "エラー: gcloud から ID トークンを取得できませんでした。" >&2
  echo "'gcloud auth login' が完了しているか確認してください。" >&2
  exit 1
fi

# ID トークンをヘッダーにセットして curl 実行
curl -s -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        "${TARGET_URL}" "$@"
echo ""
