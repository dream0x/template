export const omitId = <T extends { id: unknown }>(obj: T): Omit<T, "id"> => {
	const { id, ...rest } = obj;
	return rest;
};
