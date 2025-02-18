# DefenseDrillMVC
Spring MVC Front End for the [DefenseDrillWeb backend](https://github.com/DamienWesterman/DefenseDrillWeb/).

# Purpose
This microservice is responsible for rendering and serving the front end for the web portal. Uses inter service communication to retrieve the data from the [rest-api](https://github.com/DamienWesterman/DefenseDrillRestAPI) and [security](https://github.com/DamienWesterman/DefenseDrillSecurity) backends.

# Design Considerations
This microservice makes heavy use of a couple of technologies: [Thymeleaf](https://www.thymeleaf.org/) and [HTMX](https://htmx.org/). It employs an MVC architecture by retrieving the data from the respective backends and using thymeleaf templates to render the data into the view. HTMX is used to mirror some SPA like features without needing to use any JavaScript. The decision to exclude JavaScript was for simplicity's sake, minimizing the languages used to Java, HTML, and CSS.

# Security Considerations
Due to the simplicity and low sensitivity nature of the application, this microservice does not check for authorization. As such, it should only be accessed through the [API Gateway](https://github.com/DamienWesterman/DefenseDrillGateway).
