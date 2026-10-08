#!/bin/bash

set -eou pipefail

cd ../

## front

if [ ! -f "src/front/package.json" ]; then 
  docker compose run --rm front bash -c '
    set -euo pipefail

    npx --yes create-expo-app -e with-router .

    npx expo install axios zustand tamagui @tamagui/config/v5 react-hook-form \
      @tamagui/lucide-icons-2 react-native-svg \
      @tamagui/animations-reanimated react-native-reanimated
  '
fi

## back

if [ ! -f "src/back/build.gradle" ]; then 
  docker compose run --rm back bash -c '
    set -euo pipefail

    curl -G https://start.spring.io/starter.tgz \
      -d type=gradle-project \
      -d bootVersion=4.0 \
      -d javaVersion=25 \
      -d groupId=com \
      -d artifactId=app \
      -d configurationFileFormat=yaml \
      -d dependencies=devtools,lombok,web,validation,mybatis,postgresql,springdoc-openapi \
      | tar -xzvf -
  '
fi
