import {
  BarController,
  BarElement,
  CategoryScale,
  Chart,
  Colors,
  LinearScale,
  Tooltip,
} from "chart.js";

Chart.register(
  Colors,
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
);

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
