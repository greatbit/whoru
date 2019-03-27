Jira auth provider works through cookies.
Jira sets up JSESSIONID cookie which is being passed into authentication requests to Jira.

If sour application and Jira are located on different domains - Jira session cookie should be set up for YourApp domain.

As an example - you can use nginx and proxy_cookie_domain (if Jira sets domain property for cookies)

or a hack like this:

    proxy_cookie_path / "/; domain=yoursite.basic.domain.com;";

