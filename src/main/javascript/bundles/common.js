import * as Turbo from "@hotwired/turbo";

console.log("Turbo 🐌 enabled.", Turbo);

document.addEventListener("turbo:before-visit", function (event) {
  console.log("turbo:before-visit", event.detail);
});

document.addEventListener("turbo:visit", function (event) {
  console.log("turbo:visit", event.detail);
});

document.addEventListener("turbo:before-cache", function (event) {
  console.log("turbo:before-cache", event.detail);
});

document.addEventListener("turbo:before-render", function (event) {
  console.log("turbo:before-render", event.detail);
});

document.addEventListener("turbo:render", function (event) {
  console.log("turbo:render", event.detail);
  // utility css classes must be last to cascade other stuff
  // this flickers with developer tool open (layout shift), or on low performance devices I think...
  // however, no use case for me :p utilities last is more important
  const selector = "[rel=stylesheet][href*='03-utility']";
  if (
    !(
      document.head.lastElementChild.matches &&
      document.head.lastElementChild.matches(selector)
    )
  ) {
    const utilityCss = document.head.querySelector(selector);
    document.head.lastElementChild.insertAdjacentElement(
      "afterend",
      utilityCss,
    );
  }
});

document.addEventListener("turbo:morph", function (event) {
  console.log("turbo:morph", event.detail);
});

document.addEventListener("turbo:before-morph-element", function (event) {
  console.log("turbo:before-morph-element", event.detail);
});

document.addEventListener("turbo:morph-element", function (event) {
  console.log("turbo:morph-element", event.detail);
});
