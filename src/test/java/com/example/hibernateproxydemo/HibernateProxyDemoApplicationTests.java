package com.example.hibernateproxydemo;

import com.example.hibernateproxydemo.model.Person;
import com.example.hibernateproxydemo.model.houses.House;
import com.example.hibernateproxydemo.model.houses.SingleFamilyDetachedHouse;
import com.example.hibernateproxydemo.model.legacysystem.Comment;
import com.example.hibernateproxydemo.model.legacysystem.ExtendedUser;
import com.example.hibernateproxydemo.model.legacysystem.LegacyDocument;
import com.example.hibernateproxydemo.model.legacysystem.LegacyUser;
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
import java.util.*;

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

	@Test
    public void demo_lazyLoading_proxy_behaviour() {

	    Person alice = entityManager.find(Person.class, aliceId);
        House aliceHouse = alice.getHouse();

        assertThat(aliceHouse)
                .is(hibernateProxy())
                .is(uninitialized());

        logger.info("Load Bob from database...");

        Person bob = entityManager.createQuery(
                    "select p from Person p join fetch p.house where p.id = :id",
                    Person.class)
                .setParameter("id", bobId)
                .getSingleResult();

        assertThat(bob.getHouse())
                .isSameAs(aliceHouse);

        logger.info("Load actual house...");
        // HHH000179: Narrowing proxy to class SingleFamilyDetachedHouse - this operation breaks ==
        SingleFamilyDetachedHouse house =
                entityManager.find(SingleFamilyDetachedHouse.class, bob.getHouse().getId());

    }

    @Test
    public void demo_narrowing_proxy_warning_in_real_world() {
	    // Setup the stage:
        ExtendedUser joe = new ExtendedUser();
        joe.setUserPreference3("red-theme-color");
        entityManager.persist(joe);

        // simulate transaction commit
        entityManager.flush();
        entityManager.clear();

        Long documentId = null;
        {
            // 1) In the legacy part of the system we have code
            // that uses LegacyUser entity, e.g.
            LegacyUser currentUser = entityManager.find(LegacyUser.class, joe.getId());

            LegacyDocument document = new LegacyDocument();
            document.setOwner(currentUser);
            document.setContents("GOTO Statement Considered Harmful");
            entityManager.persist(document);

            documentId = document.getId();

            entityManager.flush();
            entityManager.clear();
        }

        {
            // 2) In the new part of the system we operate mostly using ExtendedUser
            // entity:

            ExtendedUser currentUser = entityManager.find(ExtendedUser.class, joe.getId());
            LegacyDocument existingDocument = entityManager.find(LegacyDocument.class, documentId);

            Comment comment = new Comment();
            comment.setAuthor(currentUser);
            comment.setDocument(existingDocument);
            comment.setContents("+1");
            entityManager.persist(comment);

            entityManager.flush();
            entityManager.clear();
        }

        {
            // 3) In the *new* part of the system we load document and it's comments

            LegacyDocument document = entityManager.find(LegacyDocument.class, documentId);

            // we load some data from document owner
            LegacyUser documentOwner = document.getOwner();
            String ownerPreference1 = documentOwner.getUserPreference1();

            logger.info("Load comments...");
            // HHH000179: Narrowing proxy to class ExtendedUser - this operation breaks ==
            // When Hibernate loads comment that has field of type ExtendedUser with
            // the same Id as LegacyUser it realizes that documentOwner is indeed ExtendedUser.
            // So this time Hibernate could figure out that it generated wrong proxy
            // without querying DB.
            List<Comment> comments = entityManager.createQuery(
                        "select c from Comment c where c.document.id = :docId",
                        Comment.class)
                    .setParameter("docId", document.getId())
                    .getResultList();

            logger.info("Iterate comments...");
            for(Comment comment: comments) {
                if (comment.getAuthor().getId().equals(documentOwner.getId())) {
                    logger.info("Some work...");
                }
            }

            // Now the most interesting part
            ExtendedUser commentAuthor = comments.get(0).getAuthor();

            assertThat(commentAuthor)
                    .isNotSameAs(documentOwner);

            assertThat(commentAuthor.getId())
                    .isEqualTo(documentOwner.getId());

            // Now without overloading hashCode()/equals() we may
            // expect troubles...
            Set<LegacyUser> users = new HashSet<>();
            users.add(commentAuthor);
            users.add(documentOwner);

            assertThat(users).hasSize(2);
        }
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
