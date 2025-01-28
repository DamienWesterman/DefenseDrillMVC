document.addEventListener("DOMContentLoaded", () => {
    const sideNavLinks = document.querySelectorAll("#sideNav a");

    sideNavLinks.forEach(link => {
        link.addEventListener("click", () => {
            // Remove the 'active' class from all links
            sideNavLinks.forEach(link => link.classList.remove("active"));
            // Add the 'active' class to the clicked link
            link.classList.add("active");
        });
    });
});
