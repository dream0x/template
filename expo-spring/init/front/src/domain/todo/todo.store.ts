import { create } from "zustand";
import type { TodoState } from "@/domain/todo/todo.types";

export const useTodoStore = create<TodoState>()((set) => ({
	todos: [
		{
			id: "1",
			title: "勉強する",
			description: "Springの学習",
			completed: false,
		},
		{
			id: "2",
			title: "運動する",
			description: "筋トレする",
			completed: false,
		},
		{
			id: "3",
			title: "掃除する",
			description: "部屋の掃除",
			completed: true,
		},
	],

	createTodo: (title, description) =>
		set((state) => ({
			todos: [
				...state.todos,
				{
					id: crypto.randomUUID(),
					title,
					description,
					completed: false,
				},
			],
		})),

	updateTodo: (id, { title, description }) =>
		set((state) => ({
			todos: state.todos.map((t) =>
				t.id === id ? { ...t, title, description } : t,
			),
		})),

	toggleTodo: (id) =>
		set((state) => ({
			todos: state.todos.map((t) =>
				t.id === id ? { ...t, completed: !t.completed } : t,
			),
		})),

	deleteTodo: (id) =>
		set((state) => ({
			todos: state.todos.filter((t) => t.id !== id),
		})),
}));
