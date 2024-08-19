document.addEventListener("turbo:before-visit", function (event) {
  console.log("transactions-list ::: turbo:load", window.location.href, event.detail.url);
  if (event.detail.url.endsWith("/transactions")) {
    // scroll transactions to top
    document
      .querySelector(".transaction-card-container")
      ?.scrollIntoView({ behavior: "smooth" });

    // unselect selected card
    document
      .querySelector(".transaction-card.active")
      ?.classList.remove("active");
  }
});
