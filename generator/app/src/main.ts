import { Eta } from "eta";
import { glob, mkdir, writeFile } from "node:fs/promises";
import { dirname, join } from "node:path";
import type { Props } from "./types.js";

const inputDir = join(import.meta.dirname, "input");
const outputDir = join(import.meta.dirname, "output");

const eta = new Eta({
  views: inputDir,
  tags: ["{{", "}}"],
  varName: "props",
});

const props: Props = {
  singular: "user",
  plural: "users",
  pascalSingular: "User",
  pascalPlural: "Users",
};

// "*.*" で拡張子のないディレクトリを除外する
const inputs = await Array.fromAsync(glob("**/*.*", { cwd: inputDir }));

await Promise.all(
  inputs.map(async (file) => {
    const outputPath = join(
      outputDir,
      file.replaceAll("Resource", props.pascalSingular).replace(/\.eta$/, ""),
    );

    await mkdir(dirname(outputPath), { recursive: true });
    await writeFile(outputPath, await eta.render(file, props));
  }),
);
