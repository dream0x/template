import type { JSX } from "react/jsx-runtime";
import { Platform } from "react-native";
import { Button, Dialog, Text } from "tamagui";

export default function CustomDialog(props: {
	title: string;
	description: string;
	contents: JSX.Element;
}) {
	const isMobile = Platform.OS !== "web";

	return (
		<Dialog>
			<Dialog.Trigger asChild>
				<Button>{props.title}</Button>
			</Dialog.Trigger>
			<Dialog.FocusScope loop trapped focusOnIdle={true}>
				<Dialog.Portal>
					<Dialog.Overlay background="background" opacity="0.5" />
					<Dialog.Content>
						<Dialog.Title>
							<Text>{props.title}</Text>
						</Dialog.Title>
						<Dialog.Description>
							<Text>{props.description}</Text>
						</Dialog.Description>
						{/* コンテンツ */}
						{props.contents}
						<Dialog.Close asChild>
							<Button>キャンセル</Button>
						</Dialog.Close>
					</Dialog.Content>
				</Dialog.Portal>
			</Dialog.FocusScope>
		</Dialog>
	);
}
