import { animations } from "@tamagui/config/animations-reanimated";
import { defaultConfig } from "@tamagui/config/v6";
import { Stack } from "expo-router";
import { createTamagui, TamaguiProvider, YStack } from "tamagui";

const config = createTamagui({ ...defaultConfig, animations });

export default function RootLayout() {
	return (
		<TamaguiProvider config={config} defaultTheme="light">
			<YStack flex={1}>
				<Stack screenOptions={{ headerShown: false }} />
			</YStack>
		</TamaguiProvider>
	);
}
