package com.example.hibernateproxydemo;

import com.example.hibernateproxydemo.model.*;
import com.example.hibernateproxydemo.model.pets.Cat;
import com.example.hibernateproxydemo.model.pets.Pet;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class HibernateProxyDemoApplication {
    public static void main(String[] args) {
		SpringApplication.run(HibernateProxyDemoApplication.class, args);
	}

    @Component
    public static class App implements CommandLineRunner {
        private static Logger logger = LoggerFactory.getLogger(HibernateProxyDemoApplication.class);

        private final EntityManager entityManager;
        private final TransactionTemplate txTemplate;

        public App(EntityManager entityManager, TransactionTemplate txTemplate) {
            this.entityManager = requireNonNull(entityManager);
            this.txTemplate = requireNonNull(txTemplate);
        }

        @Override
        public void run(String... args) throws Exception {

        }
    }
}
