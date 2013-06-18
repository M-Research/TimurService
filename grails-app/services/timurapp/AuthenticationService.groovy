package timurapp

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.util.WebUtils
import uk.co.desirableobjects.oauth.scribe.OauthService
import org.scribe.model.Token

class AuthenticationService {
    OauthService oauthService

    /**
     * Calls a given function (without arguments) only if user is
     * authenticated, otherwise redirects to the login page.
     */
    void withAuthentication(Closure f) {
        Token accessToken = getToken()
        if (accessToken == null) {
            redirect(url: "/")
        } else {
            f.call()
        }
    }

    boolean isAuthenticated() {
        getToken() != null
    }

    /**
     * Retrieves the e-mail of currently authenticated user.
     */
    Object getMail() {
        Token accessToken = getToken()
        if (accessToken != null) {
            def response = oauthService.getGoogleResource(
                accessToken,
                'https://www.googleapis.com/oauth2/v1/userinfo?alt=json'
            )
            JSONObject userJson = JSON.parse(response.body)
            userJson['email']
        }
    }

    /**
     * Retrieves the OAuth access token of currently authenticated user.
     */
    Token getToken() {
        def session = WebUtils.retrieveGrailsWebRequest().getSession()
        String sessionKey = oauthService.findSessionKeyForAccessToken('google')
        session[sessionKey]
    }
}
