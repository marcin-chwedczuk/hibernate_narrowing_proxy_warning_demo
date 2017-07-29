package com.example.hibernateproxydemo;

import com.example.hibernateproxydemo.model.Person;
import com.example.hibernateproxydemo.model.houses.SingleFamilyDetachedHouse;
import com.example.hibernateproxydemo.model.pets.Cat;
import com.example.hibernateproxydemo.model.pets.Pet;
import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;
import org.hibernate.proxy.HibernateProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class HibernateProxyDemoApplicationTests {
    private static Logger logger = LoggerFactory.getLogger(HibernateProxyDemoApplicationTests.class);

	@PersistenceContext
	private EntityManager entityManager;

	private UUID bobId;
	private UUID aliceId;
	private UUID gerardId;

	@Before
    public void before() {
        SingleFamilyDetachedHouse bobAndAliceHouse = new SingleFamilyDetachedHouse(
                "45 Park Avenue, New York, NY 10016",
                3,
                true);
        entityManager.persist(bobAndAliceHouse);

        Person bob = new Person("bob", bobAndAliceHouse);
        entityManager.persist(bob);
        bobId = bob.getId();

        Person alice = new Person("alice", bobAndAliceHouse);
        Cat gerard = new Cat("gerard");
        alice.addPet(gerard);
        entityManager.persist(alice);
        aliceId = alice.getId();
        gerardId = alice.getPets().iterator().next().getId();

	    // Spring executes before - test method - after
        // in a single db transaction that is
        // finally rolled back.
        // We must clear Hibernate session, otherwise it will
        // interfere with our tests.
	    entityManager.flush();
	    entityManager.clear();

	    logger.info("----- TEST DATA CREATED -----");
    }

	@Test
	public void demo_getReference_proxy_behaviour() {
	    Pet pet = entityManager.getReference(Pet.class, gerardId);

	    assertThat(pet)
                .is(hibernateProxy())
                .is(uninitialized());

	    // initialize proxy
	    logger.info("Pet is a cat: " + pet.makeNoise());

	    assertThat(pet)
                .isNot(uninitialized());

	    Pet pet2 = entityManager.getReference(Pet.class, gerardId);
	    assertThat(pet2)
                .isNotInstanceOf(Cat.class)
                .is(hibernateProxy())
                .isSameAs(pet);

	    logger.info("Requesting Cat reference...");
	    // HHH000179: Narrowing proxy to class Cat - this operation breaks ==
	    Pet pet3 = entityManager.getReference(Cat.class, gerardId);
	    assertThat(pet3)
                .isInstanceOf(Cat.class)
                .isNot(hibernateProxy());

	    // Now we get "narrowed" instance
	    Pet pet4 = entityManager.getReference(Pet.class, gerardId);
	    assertThat(pet4)
                .isSameAs(pet3);

	    // Without custom hashCode()/equals() implementation pet2 and pet4 cannot
        // be recognized as equal.

        // All proxies now refer to Cat entity.
        pet2.setName("new-gerard-name");
        assertThat(pet4.getName())
                .isEqualTo("new-gerard-name");
	}

	private Condition<Object> hibernateProxy() {
        return new Condition<Object>() {
            @Override
            public Description description() {
                return new Description() {
                    @Override
                    public String value() {
                        return "Hibernate Proxy";
                    }
                };
            }

            @Override
            public boolean matches(Object value) {
                return (value != null) && HibernateProxy.class.isInstance(value);
            }
        };
    }

    private Condition<Object> uninitialized() {
	    return new Condition<Object>() {
            @Override
            public Description description() {
                return new Description() {
                    @Override
                    public String value() {
                        return "Uninitialized Hibernate Proxy";
                    }
                };
            }

            @Override
            public boolean matches(Object value) {
                if (!(value instanceof HibernateProxy))
                    return false;

                HibernateProxy proxy = (HibernateProxy) value;
                return proxy.getHibernateLazyInitializer().isUninitialized();
            }
        };
    }
}
