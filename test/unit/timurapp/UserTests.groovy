package timurapp



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(User)
class UserTests {

    void testSomething() {
       //fail "Implement me"
        assert new User(email:"reshet.ukr@gmail.com").getContact() == "User: reshet.ukr@gmail.com";
        assert new User(email:"reshet.ukr@gmail.com",phone:"0955066611").getContact() == "User: reshet.ukr@gmail.com Phone: 0955066611";

    }
}
