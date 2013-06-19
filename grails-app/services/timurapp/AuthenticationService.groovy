package timurapp

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.util.WebUtils
import uk.co.desirableobjects.oauth.scribe.OauthService
import org.scribe.model.Token

class AuthenticationService {
    OauthService oauthService

    /**
     * Checks whether the current user of application is authenticated.
     */
    boolean isAuthenticated() {
        getToken() != null
    }

    /**
     * Retrieves the e-mail of currently authenticated user.
     */
    String getMail() {
        def session = WebUtils.retrieveGrailsWebRequest().getSession()
        def email = session['email']
        if (!email) {
            Token accessToken = getToken()
            if (accessToken) {
                def response = oauthService.getGoogleResource(
                    accessToken,
                    'https://www.googleapis.com/oauth2/v1/userinfo?alt=json'
                )
                JSONObject userJson = JSON.parse(response.body)
                email = session['email'] = userJson['email']
            }
        }
        email
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
