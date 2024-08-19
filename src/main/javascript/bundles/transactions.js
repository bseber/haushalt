import {BarController, BarElement, CategoryScale, Chart, Colors, LinearScale, Tooltip,} from "chart.js";
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
  const parent = document.querySelector("#expenses-chart-canvas");
  if (!parent) {
    return;
  }

  // { month: "August"; payee: "Amazon", amount: 42.2 },
  const data = window.haushalt.expenses.chart.data;
  parent.innerHTML = "<canvas></canvas>";
  new Chart(parent.firstElementChild, {
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

// document.addEventListener("turbo:before-visit", function (event) {
//   console.log("turbo:before-visit", window.location.href, event.detail.url);
//   if (
//     window.location.href === "/" &&
//     window.location.href === event.detail.url
//   ) {
//     // nothing to navigate again
//     // TODO check why turbo wants to load it again?
//     event.preventDefault();
//   }
// });

document.addEventListener("turbo:load", function (event) {
  console.log("transactions ::: turbo:load", event.detail);
  if (window.location.pathname === "/transactions") {
    console.log("init chart");
    initChart();
  }
});
