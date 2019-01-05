package se2.trackMe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import se2.trackMe.controller.thirdPartyController.AnonymousRequestBuilder;
import se2.trackMe.model.security.Authority;
import se2.trackMe.model.security.AuthorityName;
import se2.trackMe.storageController.AuthorityRepository;

//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication

public class TrackMeApplication {
    @Autowired
    private AnonymousRequestBuilder anonymousRequestBuilder;

    @Autowired
    private AuthorityRepository authorityRepository;

    public static void main(String[] args) {
        SpringApplication.run(TrackMeApplication.class, args);
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * If the server crashes we need to restart the threads which handle the anonymous requests
     * @return
     */



    @Bean
    CommandLineRunner initAuthorities(){
        return args -> {
            if(authorityRepository.count() == 0){
                authorityRepository.save(new Authority(AuthorityName.ROLE_INDIVIDUAL));
                authorityRepository.save(new Authority(AuthorityName.ROLE_THIRDPARTY));

            }
        };
    }

    @Bean
    CommandLineRunner restartAnonymousRequests(){
        return args -> {
            anonymousRequestBuilder.restart();};
    }



}

