document.addEventListener("turbo:before-visit", function (event) {
  // console.log("transactions-list ::: turbo:before-visit", event.detail);
});

let transactionCardContainerScrollEnd;

document.addEventListener("turbo:visit", function (event) {
  console.log("transactions-list ::: turbo:visit", event.detail);

  transactionCardContainerScrollEnd = Promise.resolve();

  const { pathname } = new URL(event.detail.url);
  if (
    /\/transactions\/\d*$/.test(window.location.pathname) &&
    pathname === "/transactions"
  ) {
    // navigated to transactions page
    // scroll transactions list to the top
    const container = document.querySelector(".transaction-card-container");
    if (container) {
      // transactionCardContainerScrollEnd = new Promise(resolve => {
      //   container.parentElement.addEventListener("scrollend", function (event) {
      //     resolve();
      //   }, { once: true });
      // });

      container.scrollIntoView({ behavior: "smooth" });

      // unselect selected card
      document
        .querySelector(".transaction-card.active")
        ?.classList.remove("active");
    }
  }
});

document.addEventListener("turbo:load", function (event) {
  console.log("transactions-list ::: turbo:load", event.detail);
  // const { pathname } = new URL(event.detail.url);
  // setTimeout(function () {
  //   if (/^\/transactions\/\d+$/.test(pathname)) {
  //     document.querySelector(".transaction-card.active")
  //       ?.scrollIntoView({ behavior: "smooth" });
  //   }
  // }, 0);
});

document.addEventListener("turbo:before-render", async function (event) {
  console.log("transactions-list ::: turbo:before-render", event.detail);

  // console.log("wait for scroll end")
  // await transactionCardContainerScrollEnd;
  //
  // console.log("resume render")

  const { pathname } = window.location;
  if (pathname === "/transactions") {
    const container = document.querySelector(".transaction-card-container");
    if (container) {
      console.log(">> prevent rendering");
      event.preventDefault();

      transactionCardContainerScrollEnd = new Promise((resolve) => {
        container.parentElement.addEventListener(
          "scrollend",
          function (event) {
            console.log(">> scrollend -- resume");
            event.detail.resume();
          },
          { once: true },
        );
      });
      // setTimeout(function () {
      //   console.log("resume");
      //   event.detail.resume();
      // }, 500)

      container.scrollIntoView({ behavior: "smooth" });

      // // unselect selected card
      // document
      //   .querySelector(".transaction-card.active")
      //   ?.classList.remove("active");
    }
  }
});

document.addEventListener("turbo:render", async function (event) {
  console.log("transactions-list ::: turbo:render", event.detail);
});
