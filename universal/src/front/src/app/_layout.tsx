import { defaultConfig } from "@tamagui/config/v5";
import { Home, List } from "@tamagui/lucide-icons-2";
import { TabList, TabSlot, Tabs, TabTrigger } from "expo-router/ui";
import { createTamagui, TamaguiProvider, YStack } from "tamagui";
import TabItem from "@/components/tab/TabItem";
import type { TabItemType } from "../types/types";

const config = createTamagui(defaultConfig);
const tabItems: TabItemType[] = [
  {
    name: "home",
    href: "/",
    icon: <Home />,
    label: "ホーム",
  },
  {
    name: "resources",
    href: "/resources",
    icon: <List />,
    label: "リソース",
  },
];

export default function RootLayout() {
  return (
    <TamaguiProvider config={config} defaultTheme={"light"}>
      <YStack flex={1} backgroundColor={"$gray7"}>
        <Tabs>
          <TabSlot />
          {/* タブ一覧*/}
          <TabList>
            {/* タブ要素 */}
            {tabItems.map((tabItem) => (
              <TabTrigger
                key={tabItem.name}
                name={tabItem.name}
                href={tabItem.href}
                style={{ flex: 1 }}
              >
                <TabItem
                  name={tabItem.name}
                  icon={tabItem.icon}
                  label={tabItem.label}
                />
              </TabTrigger>
            ))}
          </TabList>
        </Tabs>
      </YStack>
    </TamaguiProvider>
  );
}
