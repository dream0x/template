import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { Button, Text, YStack } from "tamagui";
import { z } from "zod";
import FormTextField from "@/domain/common/FormTextField";
import { useTodoStore } from "@/domain/todo/todo.store";
import type { Todo } from "./todo.types";

const formSchema = z.object({
	title: z
		.string()
		.trim()
		.min(1, "タイトルを入力してください")
		.max(50, "タイトルは50文字以内で入力してください"),
	description: z
		.string()
		.trim()
		.max(200, "説明は200文字以内で入力してください"),
});

type FormValues = z.infer<typeof formSchema>;

export default function UpdateTodoForm(props: { resource: Todo }) {
	const update = useTodoStore((state) => state.updateTodo);

	const {
		control,
		handleSubmit,
		reset,
		formState: { errors, isSubmitting },
	} = useForm<FormValues>({
		resolver: zodResolver(formSchema),
		defaultValues: props.resource,
	});

	const onSubmit = (values: FormValues) => {
		update(props.resource.id, values);
		reset(values);
	};

	return (
		<YStack gap="$3" padding="$4">
			<FormTextField
				control={control}
				name="title"
				label="タイトル"
				placeholder="タイトルを入力"
				error={errors.title?.message}
			/>

			<FormTextField
				control={control}
				name="description"
				label="説明"
				placeholder="説明を入力"
				error={errors.description?.message}
				multiline
			/>

			<Button
				onPress={() => void handleSubmit(onSubmit)()}
				disabled={isSubmitting}
			>
				<Text>更新</Text>
			</Button>
		</YStack>
	);
}
