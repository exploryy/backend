package com.hits.open.world;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication
public class OpenTheWorldApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(OpenTheWorldApplication.class)
                .beanNameGenerator(new FullyQualifiedAnnotationBeanNameGenerator())
                .run(args);
    }
}

/*
docker exec -it 9556682afb1a bash
cd /opt/keycloak/bin/
./kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin
./kcadm.sh update realms/master -s sslRequired=NONE
./kcadm.sh update realms/hits-project -s sslRequired=NONE
*/