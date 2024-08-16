import {
  BarController,
  BarElement,
  CategoryScale,
  Chart,
  Colors,
  LinearScale,
  Tooltip,
} from "chart.js";
import "../components/transactions-list.js";

Chart.register(
  Colors,
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
);

function initChart() {
  // { month: "August"; payee: "Amazon", amount: 42.2 },
  const data = window.haushalt.expenses.chart.data;

  new Chart(document.querySelector("#expenses-chart-canvas"), {
    type: "bar",
    options: {
      indexAxis: "y",
      animation: false,
      plugins: {
        legend: {
          display: false,
        },
        tooltip: {
          enabled: true,
        },
      },
    },
    data: {
      labels: data.map((row) => row.payee),
      datasets: [
        {
          data: data.map((row) => row.amount),
        },
      ],
    },
  });
}

document.addEventListener("turbo:before-visit", function (event) {
  console.log("turbo:before-visit", window.location.href, event.detail.url);
  if (
    window.location.href === "/" &&
    window.location.href === event.detail.url
  ) {
    // nothing to navigate again
    // TODO check why turbo wants to load it again?
    event.preventDefault();
  }
});

document.addEventListener("turbo:load", function (event) {
  console.log("turbo:load", window.location.href, event.detail.url);
  if (event.detail.url.endsWith("/transactions")) {
    // TODO clicking home must not push an history item to the stack
    //      (the same url is advanced)
    // TODO do not do stuff when url did not change (why does turbo dispatch the :load event in this case?)
    initChart();
  }
});
