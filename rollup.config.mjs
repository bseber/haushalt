import resolve from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import replace from "@rollup/plugin-replace";
import esbuild from "rollup-plugin-esbuild";
import {assetsManifest} from "./rollup-plugin-assets-manifest.mjs";
import {rimraf} from "rimraf";
import glob from "fast-glob";

const NODE_ENV = process.env.NODE_ENV;
const MODE = process.env.MODE || NODE_ENV || "development";
const isProduction = MODE === "production";

const paths = {
  src: "src/main/javascript",
  dist: "target/classes/static/assets",
};

export default {
  input: {
    ...inputFiles(),
  },
  output: {
    dir: paths.dist,
    format: "es",
    sourcemap: true,
    entryFileNames: isProduction ? `[name].[hash].js` : `[name].js`,
    // custom assets like css files extracted by `rollup-plugin-styles`
    assetFileNames: "[name].[hash].[ext]",
    manualChunks(id) {
      if (id.includes("@hotwired")) {
        return `npm.hotwired`;
      }
      if (id.includes("chart.js")) {
        return `npm.chartjs`;
      }
      if (id.includes("idiomorph")) {
        return `npm.idiomorph`;
      }
    },
  },
  plugins: [
    {
      name: "clean",
      buildStart() {
        rimraf.sync(paths.dist);
      },
    },
    replace({
      preventAssignment: true,
      "process.env.NODE_ENV": JSON.stringify(NODE_ENV),
      "process.env.MODE": JSON.stringify(MODE),
    }),
    resolve({
      preferBuiltins: false,
    }),
    commonjs(),
    esbuild({
      sourceMap: true,
      minify: isProduction,
    }),
    assetsManifest({
      output: "target/classes/assets-manifest.json",
      publicPath: "/assets",
    }),
  ],
};

function inputFiles() {
  const files = glob.sync(`${paths.src}/bundles/*.js`);
  return Object.fromEntries(files.map((file) => [entryName(file), file]));
}

function entryName(file) {
  const filename = file.slice(Math.max(0, file.lastIndexOf("/") + 1));
  return filename.slice(0, Math.max(0, filename.lastIndexOf(".")));
}
