import type { JSX } from "react/jsx-runtime";

export type TabItemType = {
	name: string;
	href?: string;
	icon: JSX.Element;
	label: string;
};
