#!/bin/sh
set -eu
api_url="${API_URL:-http://localhost:8080/api}"
api_url_escaped=$(printf '%s' "$api_url" | sed 's/[&|]/\\&/g')
sed -i "s|http://localhost:8080/api|$api_url_escaped|g" /usr/share/nginx/html/config.js
