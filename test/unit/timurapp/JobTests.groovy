package timurapp

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class JobTests {

    void setUp() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void testSomething() {
        //fail "Implement me"

        User user = new User(email:"reshet.ukr@gmail.com");
       // user.save();
        user.jobs.add(new Job(title:"Новая работа", description: "Тратата", reward: 3,address: "Подол, у фонтана"));
        //user.getJobs().add(job);
        assert 3 == user.jobs.find{it.title == "Новая работа"}.reward
    }
}
