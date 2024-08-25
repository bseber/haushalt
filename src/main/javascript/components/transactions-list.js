import { scrollend } from "./scrollend.js";

function isTransactionDetailView(url) {
  return /\/transactions\/\d*$/.test(url);
}

document.addEventListener("turbo:before-visit", function (event) {
  console.log("transactions-list ::: turbo:before-visit", event.detail);

  if (/\/transactions$/.test(event.detail.url)) {
    const container = document.querySelector(".transaction-card-container");
    if (container) {
      container.scrollIntoView({ behavior: "smooth" });
      transactionCardContainerScrollEnd = scrollend(container);
    }
  }
});

let transactionCardContainerScrollEnd;

document.addEventListener("turbo:visit", function (event) {
  console.log("transactions-list ::: turbo:visit", event.detail);

  const { pathname } = new URL(event.detail.url);
  if (
    isTransactionDetailView(window.location.pathname) &&
    pathname === "/transactions"
  ) {
    const container = document.querySelector(".transaction-card-container");
    if (container) {
      document
        .querySelector(".transaction-card.active")
        ?.classList.remove("active");
    }
  }
});

document.addEventListener("turbo:load", function (event) {
  // console.log("transactions-list ::: turbo:load", event.detail);
  if (isTransactionDetailView(event.detail.url)) {
    document
      .querySelector(".transaction-card.active")
      ?.scrollIntoView({ behavior: "smooth" });
  }
});

document.addEventListener("turbo:before-render", async function (event) {
  console.log("transactions-list ::: turbo:before-render", event.detail);

  const { pathname } = window.location;
  if (pathname === "/transactions") {
    const container = document.querySelector(".transaction-card-container");
    if (container) {
      event.preventDefault();
      transactionCardContainerScrollEnd.then(() => {
        event.detail.resume();
      });
    }
  }
});

document.addEventListener("turbo:render", async function (event) {
  console.log("transactions-list ::: turbo:render", event.detail);
});
