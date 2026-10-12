#!/bin/bash

set -eou pipefail

## front

if [ ! -f "src/front/package.json" ]; then 
  docker compose run --rm front bash -c '
    set -euo pipefail

    npx --yes create-expo-app -e with-router .

    npx expo install typescript expo-updates expo-splash-screen @biomejs/biome axios zustand tamagui @tamagui/config react-hook-form \
      @tamagui/helpers-icon react-native-svg \
      @tamagui/animations-reanimated react-native-reanimated
  '

  cp -r init/front/* src/front/
else
  echo "already front project"
fi

cp -r init/front/* src/front/
