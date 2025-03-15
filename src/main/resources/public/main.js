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
 * Dynamically resize textarea inputs as the user types
 */
function resizeTextarea(textarea) {
    if (textarea.scrollHeight > textarea.clientHeight) {
        textarea.style.height = textarea.scrollHeight + "px";
    }
}
