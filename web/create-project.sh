#!/bin/bash

set -eou pipefail

## front

if [ ! -f "src/front/package.json" ]; then 
  docker compose exec front bash -c '
    set -euo pipefail

    npx --yes create-expo-app -e with-router .
  '
fi
