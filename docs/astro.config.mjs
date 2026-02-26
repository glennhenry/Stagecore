// @ts-check
import { defineConfig } from "astro/config";
import starlight from "@astrojs/starlight";
import starlightThemeObsidian from "starlight-theme-obsidian";
import rehypeExternalLinks from "rehype-external-links";
import externalLinkIcon from "./src/assets/externalLinkIcon.js";
import { fontHeadTags } from "./src/assets/headlinks.js";

// https://astro.build/config
export default defineConfig({
  // site: "", // deployment URL
  base: "docs/",
  markdown: {
    rehypePlugins: [[rehypeExternalLinks, externalLinkIcon]],
  },
  integrations: [
    starlight({
      head: [...fontHeadTags],
      plugins: [starlightThemeObsidian()],
      favicon: "favicon.ico",
      customCss: ["./src/assets/custom.css"],
      tableOfContents: { minHeadingLevel: 2, maxHeadingLevel: 6 },
      credits: true,
      lastUpdated: true,
      title: "REPLACE",
      components: {
        Pagination: "./src/components/Pagination.astro",
        PageFrame: "./src/components/PageFrame.astro",
      },
      editLink: {
        // REPLACE
        baseUrl: "https://github.com/glennhenry/Stagecore/edit/main/",
      },
      social: [
        {
          icon: "github",
          label: "GitHub",
          // REPLACE
          href: "https://github.com/glennhenry/Stagecore",
        },
      ],
      sidebar: [
        { label: "Intro", slug: "index" },
        { label: "Flow", slug: "flow" },
      ],
    }),
  ],
});
