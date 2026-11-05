import { Circle, CircleChevronDown } from "lucide-react-native";
import { Button, Text, XStack, YStack } from "tamagui";
import CustomDialog from "@/domain/common/CustomDialog";
import { omitId } from "@/domain/common/utils";
import { useTodoStore } from "@/domain/todo/todo.store";
import type { Todo } from "@/domain/todo/todo.types";
import UpdateTodoForm from "@/domain/todo/UpdateTodoForm";

export default function Home() {
	const { todos, toggleTodo } = useTodoStore((state) => state);
	const incompleteTodos = todos.filter((todo) => !todo.completed);
	const completedTodos = todos.filter((todo) => todo.completed);

	return (
		<YStack>
			<Text>未完了</Text>
			{incompleteTodos.map((todo) => (
				<TodoItem key={todo.id} todo={todo} toggleTodo={toggleTodo} />
			))}

			<Text>完了</Text>
			{completedTodos.map((todo) => (
				<TodoItem key={todo.id} todo={todo} toggleTodo={toggleTodo} />
			))}
		</YStack>
	);
}

function TodoItem(props: { todo: Todo; toggleTodo: (id: string) => void }) {
	return (
		<XStack key={props.todo.id}>
			<Button onPress={() => props.toggleTodo(props.todo.id)}>
				{props.todo.completed ? <CircleChevronDown /> : <Circle />}
			</Button>
			<CustomDialog
				title={props.todo.title}
				description={props.todo.description}
				contents={<UpdateTodoForm resource={props.todo} />}
			/>
		</XStack>
	);
}
