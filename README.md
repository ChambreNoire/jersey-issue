# jersey-issue

2 problems:

POST { "email": "user@test.com", "password": "password" } to /login
should return an XSRF-TOKEN cookie as well as the ACCESS-TOKEN cookie. doesn't (/login2 written in Spring MVC does)

after logging in GET /users
should fail because user@test.com doesn't have the 'ADMIN' role but it works so @RolesAllowed is ignored
