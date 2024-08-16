import { Idiomorph } from "idiomorph/dist/idiomorph.esm.js";
import * as Turbo from "@hotwired/turbo";

console.log("Turbo 🐌 enabled.", Turbo);

document.addEventListener("turbo:before-render", function (event) {
  // morph all the things!
  event.detail.render = (currentElement, newElement) => {
    Idiomorph.morph(currentElement, newElement, {
      // ignore value of active element
      ignoreActiveValue: true,
      head: {
        style: "morph", // order is important for stylesheets! (utility classes has to be last)
      },
    });
  };
});

document.addEventListener("turbo:render", function (event) {
  // utility css classes must be last to cascade other stuff
  // this flickers with developer tool open (layout shift), or on low performance devices I think...
  // however, no use case for me :p utilities last is more important
  const selector = "[rel=stylesheet][href*='03-utility']";
  if (
    !(
      document.head.lastChild.matches &&
      document.head.lastChild.matches(selector)
    )
  ) {
    const utilityCss = document.head.querySelector(selector);
    document.head.lastChild.insertAdjacentElement("afterend", utilityCss);
  }
});
