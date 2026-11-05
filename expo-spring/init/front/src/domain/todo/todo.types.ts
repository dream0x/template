export type Todo = {
	id: string;
	title: string;
	description: string;
	completed: boolean;
};

export type TodoState = {
	todos: Todo[];
	createTodo: (title: string, description: string) => void;
	updateTodo: (
		id: string,
		patch: { title: string; description: string },
	) => void;
	toggleTodo: (id: string) => void;
	deleteTodo: (id: string) => void;
};
