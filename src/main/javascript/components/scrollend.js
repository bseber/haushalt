export function scrollend(element) {
  // worked with number of 3 on my Apple M1 Max.
  // worst case is that the list stands still at random point when transactions are rendered.
  // therefore... this should be ok...
  const maxPoll = 3;

  let pollCount = maxPoll;
  let last = element.getBoundingClientRect().top;

  return new Promise((resolve) => {
    requestAnimationFrame(check);

    function check() {
      let top = element.getBoundingClientRect().top;
      if (top === last) {
        pollCount--;
        if (pollCount === 0) {
          resolve();
          return;
        }
      } else {
        pollCount = maxPoll;
        last = top;
      }
      requestAnimationFrame(check);
    }
  });
}
