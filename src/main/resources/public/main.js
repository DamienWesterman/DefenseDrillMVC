/*
 * To be used with tab_with_window.html to highlight the currently selected tab.
 */
document.addEventListener("DOMContentLoaded", () => {
    const sideNavLinks = document.querySelectorAll("#sideNav a");

    sideNavLinks.forEach(link => {
        link.addEventListener("click", () => {
            sideNavLinks.forEach(link => link.classList.remove("active"));
            link.classList.add("active");
        });
    });
});

/*
 * Make sure when a user presses "Enter" in a textarea, it submits the form.
 * Also dynamically resize textarea inputs as the user types.
 * All textarea tags should be used inside an HTMX request inside of tab_with_window.html.
 */
function setupTextareas() {
    document.querySelectorAll("textarea").forEach(textarea => {
        if (!textarea.dataset.htmxListenerAdded) { 
            textarea.dataset.htmxListenerAdded = "true";

            // Auto-resize on input
            textarea.addEventListener("input", function () {
                if (textarea.scrollHeight > textarea.clientHeight) {
                    textarea.style.height = textarea.scrollHeight + "px";
                }
            });

            // Submit form on Enter
            textarea.addEventListener("keydown", function (event) {
                if (event.key === "Enter") {
                    event.preventDefault(); // Prevent new line
                    const form = this.closest("form");
                    if (form) {
                        htmx.trigger(form, "submit");
                    }
                }
            });
        }
    });
}
document.addEventListener("DOMContentLoaded", setupTextareas);
document.addEventListener("htmx:afterSwap", setupTextareas);

