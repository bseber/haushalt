import "../components/transactions-list.js";
import { initAutoSubmit } from "../components/autosubmit.js";

initAutoSubmit();

// submit button not required due to auto-submit clicking the checkboxes
hideTagUpdateSubmitButton(document);

document.addEventListener("turbo:before-render", function (event) {
  hideTagUpdateSubmitButton(event.detail.newBody);
});

function hideTagUpdateSubmitButton(root) {
  root
    .querySelector("#transaction-detail-tags-update-button")
    ?.classList.add("sr-only");
}
