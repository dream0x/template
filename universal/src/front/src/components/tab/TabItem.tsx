import { useTabTrigger } from "expo-router/ui";
import { Button, Text, XStack, YStack } from "tamagui";
import type { TabItemType } from "@/types/types";

export default function TabItem(props: TabItemType) {
  const { trigger } = useTabTrigger({ name: props.name });
  const isFocused = trigger?.isFocused ?? false;

  return (
    <Button flex={1} padding={"$6"} borderRadius={"unset"}>
      <YStack>
        <XStack
          padding={"$2"}
          background={isFocused ? "$gray8" : undefined}
          borderRadius={"$3"}
        >
          {props.icon}
        </XStack>
        <Text>{props.label}</Text>
      </YStack>
    </Button>
  );
}
