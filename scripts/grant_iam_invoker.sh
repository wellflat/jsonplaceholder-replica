#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ローカル設定ファイル (config.env) が存在する場合は読み込み
if [ -f "${SCRIPT_DIR}/config.env" ]; then
  # shellcheck source=/dev/null
  source "${SCRIPT_DIR}/config.env"
fi

# デフォルト設定 (プレースホルダー)
PROJECT_ID="${PROJECT_ID:-YOUR_PROJECT_ID}"
SERVICE_NAME="${SERVICE_NAME:-YOUR_SERVICE_NAME}"
REGION="${REGION:-YOUR_REGION}"

# 引数がある場合はそれを利用し、無い場合は現在ログイン中の gcloud アカウントを取得
TARGET_ACCOUNT="${1:-$(gcloud config get-value account 2>/dev/null)}"

if [ -z "${TARGET_ACCOUNT}" ]; then
  echo "エラー: 付与対象のアカウント（メールアドレス）が取得できませんでした。"
  echo "使い方: $0 [user:email | serviceAccount:email]"
  exit 1
fi

# プレフィックスの補完 (user: や serviceAccount: が無い場合は user: を付与)
if [[ "${TARGET_ACCOUNT}" != user:* ]] && [[ "${TARGET_ACCOUNT}" != serviceAccount:* ]]; then
  MEMBER="user:${TARGET_ACCOUNT}"
else
  MEMBER="${TARGET_ACCOUNT}"
fi

echo "=========================================="
echo "Cloud Run IAM 呼び出し権限 (roles/run.invoker) の付与"
echo "=========================================="
echo "Project ID:   ${PROJECT_ID}"
echo "Service Name: ${SERVICE_NAME}"
echo "Region:       ${REGION}"
echo "Member:       ${MEMBER}"
echo "=========================================="

gcloud run services add-iam-policy-binding "${SERVICE_NAME}" \
  --project="${PROJECT_ID}" \
  --region="${REGION}" \
  --member="${MEMBER}" \
  --role="roles/run.invoker"

echo "完了: ${MEMBER} に Cloud Run 呼び出し権限を付与しました。"
