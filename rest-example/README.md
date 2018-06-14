Non authenticated access:
http://localhost:8080/example/all

Restricted access for authenticated only:
http://localhost:8080/example/usersonly

Login:
http://localhost:8080/example/login/auth?login=somelogin&password=somepass

Re-try restricted - access granted:
http://localhost:8080/example/usersonly
