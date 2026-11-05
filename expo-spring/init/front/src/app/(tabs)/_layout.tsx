import { Tabs } from "expo-router";
import { Inbox, Settings } from "lucide-react-native";
import { useTheme } from "tamagui";

export default function TabLayout() {
	const theme = useTheme();

	return (
		<Tabs
			screenOptions={{
				headerStyle: { backgroundColor: theme.background?.val },
				headerShadowVisible: false,
				headerTintColor: theme.color?.val,
				sceneStyle: { backgroundColor: theme.background?.val },
			}}
		>
			<Tabs.Screen
				name="index"
				options={{
					title: "インボックス",
					tabBarIcon: ({ color, size }) => <Inbox color={color} size={size} />,
				}}
			/>
			<Tabs.Screen
				name="about"
				options={{
					title: "設定",
					tabBarIcon: ({ color, size }) => (
						<Settings color={color} size={size} />
					),
				}}
			/>
		</Tabs>
	);
}
