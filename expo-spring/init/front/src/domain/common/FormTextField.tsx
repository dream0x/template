import type { Control, FieldPath, FieldValues } from "react-hook-form";
import { Controller } from "react-hook-form";
import { Input, Text, TextArea, YStack } from "tamagui";

type FormTextFieldProps<T extends FieldValues> = {
	control: Control<T>;
	name: FieldPath<T>;
	label: string;
	placeholder?: string;
	error?: string;
	multiline?: boolean;
};

export default function FormTextField<T extends FieldValues>(
	props: FormTextFieldProps<T>,
) {
	const { control, name, label, placeholder, error, multiline = false } = props;

	return (
		<Controller
			control={control}
			name={name}
			render={({ field: { onChange, onBlur, value } }) => (
				<YStack gap="$1">
					<Text>{label}</Text>
					{multiline ? (
						<TextArea
							placeholder={placeholder}
							value={value as string}
							onChangeText={onChange}
							onBlur={onBlur}
						/>
					) : (
						<Input
							placeholder={placeholder}
							value={value as string}
							onChangeText={onChange}
							onBlur={onBlur}
						/>
					)}
					{error && <Text color="$red10">{error}</Text>}
				</YStack>
			)}
		/>
	);
}
