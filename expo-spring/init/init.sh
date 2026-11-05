#!/bin/bash

set -eou pipefail

MODE="${1:-}"

case "$MODE" in
  rf)
    rm -rf src/front
    mkdir -p src/front
    ;;
  rb)
    rm -rf src/back
    mkdir -p src/back
    ;;
esac

## front

if [ ! -f "src/front/package.json" ]; then 
  docker compose run --rm front bash -c '
    set -euo pipefail

    npx --yes create-expo-app -e with-router .

    npx expo install typescript @expo/ngrok expo-updates expo-splash-screen @biomejs/biome axios zustand tamagui @tamagui/config react-hook-form zod @hookform/resolvers lucide-react-native\
      @tamagui/helpers-icon react-native-svg lucide-react-native\
      @tamagui/animations-reanimated react-native-reanimated
    
    npm dedupe
  '

  cp -r init/front/* src/front/
  rm -rf src/front/.git
else
  echo "already front project"
fi

## back
